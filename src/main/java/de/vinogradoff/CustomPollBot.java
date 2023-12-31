package de.vinogradoff;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.text.ParseException;
import java.time.*;
import java.util.List;

import static java.time.format.DateTimeFormatter.ofPattern;

public class CustomPollBot extends TelegramLongPollingBot {

  String token;

  public CustomPollBot(String token) {
    super(token);
    this.token = token;
  }


  @Override
  public void onUpdateReceived(Update update) {
    // We check if the update has a message and the message has text
    try {
      if (update.hasMessage() && update.getMessage().hasText()) {
        var cmd = update.getMessage().getText();

        var chatId = update.getMessage().getChatId().toString();
        EventPoll eventPoll = null;
        SendMessage message = null;

        if (cmd.startsWith("/") && cmd.split(" ").length == 1) {
          message = new SendMessage();
          message.setChatId(chatId);
          message.setText("""
                      Формат: /chgk или /quiz <дата dd.mm> [время] [место] [другая информация]
                      Пример: /chgk 4.12 19:30 APROPO Турнир сложности 3.5
                  """);
        } else if (cmd.startsWith("/chgk")) {
          eventPoll = createPoll(chatId, cmd,
                  "ЧГК %s, начало в %s в %s",
                  "19:30", "APROPO");
        } else if (cmd.startsWith("/quiz")) {
          eventPoll = createPoll(chatId, cmd,
                  "Квиз %s, начало в %s в %s",
                  "17:00", "мебельном");
        }

        if (eventPoll != null) {
          var msg = execute(eventPoll.poll());
          var timeUntilPollIsClosedInSeconds = EventPoll.calulateTimeToEvent(
                  LocalDateTime.now(), eventPoll.event(), Duration.ofHours(1));

          new Thread(new ClosePoll(this.token,
                  timeUntilPollIsClosedInSeconds,
                  chatId,
                  msg.getMessageId()))
                  .start();
        }
        if (message != null) {
          execute(message);
        }

      }
    } catch (TelegramApiException | ParseException e) {
      e.printStackTrace();
    }


  }

  @Override
  public String getBotUsername() {
    return "vino_chgk_poll_bot";
  }

  private EventPoll createPoll(String chatId, String cmd,
                              String questionFormat,
                               String defaultTime, String defaultPlace) throws ParseException {

    var event = Event.fromCmdArgs(enrichCommand(cmd, defaultTime, defaultPlace));
    var question = formatQuestion(questionFormat, event);
    var poll = new SendPoll();
    poll.setChatId(chatId);
    poll.setQuestion(question);
    poll.setOptions(List.of("Буду", "Не буду", "Не знаю ещё"));
    poll.setIsAnonymous(false);
    return new EventPoll(event, poll);
  }

  private String enrichCommand(String cmd, String defaultTime, String defaultPlace) {
    var numberOfParameters = cmd.split(" ").length - 1;
    switch (numberOfParameters) {
      case 2:
        cmd += " " + defaultPlace;
        break;
      case 1:
        cmd += " " + defaultTime + " " + defaultPlace;
        break;

    }
    return cmd;
  }

  private String formatQuestion(String questionFormat, Event event) {
    return String.format(questionFormat,
            event.startTime().format(ofPattern("dd.MM")) + "(" + event.dayOfWeek() + ")",
            event.startTime().format(ofPattern("HH:mm")),
            event.place() + " " + event.otherInformation());
  }


}
