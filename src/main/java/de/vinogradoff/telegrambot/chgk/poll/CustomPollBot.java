package de.vinogradoff.telegrambot.chgk.poll;

import de.vinogradoff.telegrambot.chgk.poll.domain.*;
import de.vinogradoff.telegrambot.chgk.poll.scheduling.ClosePoll;
import org.apache.tools.ant.types.Commandline;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.text.ParseException;
import java.time.*;
import java.util.*;

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

        var params = new ArrayList<>(Arrays.asList(Commandline.translateCommandline(cmd)));
        if (cmd.startsWith("/") && params.size() == 1) {
          message = new SendMessage();
          message.setChatId(chatId);
          message.setText("""
                      Формат: /chgk или /quiz <дата dd.mm> [время] [место] [другая информация]
                      Пример: /chgk 4.12 19:30 APROPO Турнир сложности 3.5
                      
                      или /chgk или /quiz <день недели> [время] [место] [другая информация]
                      Примеры: /chgk ср 19:30 APROPO Турнир сложности 3.5
                               /chgk четверг 19:30 APROPO Турнир сложности 3.5
                  """);
        } else if (cmd.startsWith("/chgk")) {
          params.add(2, "");
          while (params.size() < 6) {
            params.add(null);
          }
          eventPoll = createPoll(chatId, params,
                  "ЧГК %s, начало в %s в %s",
                  List.of("Буду", "Не буду", "Не знаю ещё"),
                  "",
                  "19:30",
                  "Lu Fung",
                  "");
        } else if (cmd.startsWith("/quiz")) {
          params.add(2, "");
          while (params.size() < 6) {
            params.add(null);
          }
          eventPoll = createPoll(chatId, params,
                  "Квиз %s, начало в %s в %s",
                  List.of("Буду", "Не буду", "Не знаю ещё"),
                  "",
                  "17:00",
                  "мебельном",
                  "");
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

  private EventPoll createPoll(String chatId, List<String> params,
                              String questionFormat,
                               List<String> options,
                               String defaultTopic,
                               String defaultTime,
                               String defaultPlace,
                               String defaultOtherInformation) throws ParseException {

    var event = Event.fromCmdArgs(params, defaultTopic, defaultTime, defaultPlace, defaultOtherInformation);
    var question = formatQuestion(questionFormat, event);
    var poll = new SendPoll();
    poll.setChatId(chatId);
    poll.setQuestion(question);
    poll.setOptions(options);
    poll.setIsAnonymous(false);
    return new EventPoll(event, poll);
  }

  private List<String> enrichCommand(List<String> params, String defaultTopic, String defaultTime, String defaultPlace) {
    var ret = new ArrayList<>(params);
    var numberOfParameters = params.size();
    switch (numberOfParameters) {
      case 3:
        ret.add(defaultPlace);
        break;
      case 2:
        ret.add(defaultTime);
        ret.add(defaultPlace);
        break;
    }
    return ret;
  }

  private String formatQuestion(String questionFormat, Event event) {
    return String.format(questionFormat,
            event.startTime().format(ofPattern("dd.MM")) + "(" + event.dayOfWeek() + ")",
            event.startTime().format(ofPattern("HH:mm")),
            event.place() + " " + event.otherInformation());
  }


}
