package de.vinogradoff;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.text.*;
import java.time.LocalDate;
import java.util.*;

public class CustomPollBot extends TelegramLongPollingBot {

  public CustomPollBot(String token) {
    super(token);
  }

  @Override
  public void onUpdateReceived(Update update) {
    // We check if the update has a message and the message has text
    try {
      if (update.hasMessage() && update.getMessage().hasText()) {
        var cmd = update.getMessage().getText();

        var chatId = update.getMessage().getChatId().toString();
        SendPoll poll = null;
        SendMessage message = null;

        if (cmd.startsWith("/chgk")) {
          poll = createPoll(chatId, cmd,
                  "ЧГК %s, начало в %s в %s",
                  "???", "19:30", "APROPO");
        } else if (cmd.startsWith("/quiz")) {
          poll = createPoll(chatId, cmd,
                  "Квиз %s, начало в %s в %s",
                  "???", "17:00", "мебельном");
        } else if (cmd.startsWith("/")) {
          message = new SendMessage();
          message.setChatId(chatId);
          message.setText("""
                      Формат: /chgk или /quiz <дата dd.mm> [время] [место]
                      время, место (опциональные)- одним словом        
                  """);
        }

        if (poll != null) {
          execute(poll);
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

  private SendPoll createPoll(String chatId, String cmd,
                              String questionFormat,
                              String date, String time, String place) throws ParseException {
    var question = parseCmd(questionFormat, cmd,
            date, time, place);
    var poll = new SendPoll();
    poll.setChatId(chatId);
    poll.setQuestion(question);
    poll.setOptions(List.of("Буду", "Не буду", "Не знаю ещё"));
    poll.setIsAnonymous(false);
    return poll;
  }

  private String parseCmd(String questionFormat, String cmd, String... param) throws ParseException {
    var list = param;
    var parts = cmd.split(" ");
    var size = parts.length;
    if (size >= 4) {
      list[2] = parts[3];
    }
    if (size >= 3) {
      list[1] = parts[2];
    }
    if (size >= 2) {
      list[0] = parts[1];
      // convert to Day
      var sdf = new SimpleDateFormat("dd.MM.yyyy");
      var year = LocalDate.now().getYear();
      var date = sdf.parse(list[0] + "." + year);
      var simpleDay = new SimpleDateFormat("EEEE", new Locale("ru"));
      var day = simpleDay.format(date);
      list[0] += " (" + day + ")";
    }

    return String.format(questionFormat, (Object[]) list);
  }

}
