package de.vinogradoff.telegrambot.chgk.poll.scheduling;

import org.telegram.telegrambots.bots.*;
import org.telegram.telegrambots.meta.api.methods.polls.StopPoll;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.*;
import java.time.format.DateTimeFormatter;

public class ClosePoll extends DefaultAbsSender implements Runnable {

  Integer messageId;
  String chatId;

  Long sleepTime;

  public ClosePoll(String token, Long sleepTime, String chatId, Integer messageId) {
    super(new DefaultBotOptions(), token);
    this.sleepTime = sleepTime;
    this.chatId = chatId;
    this.messageId = messageId;
  }

  public void run() {
    var time = LocalDateTime.now().plus(Duration.ofSeconds(sleepTime));
    System.out.println("will close " + messageId + " in " + sleepTime + " seconds. " +
            "That is:" + time.format(DateTimeFormatter.ISO_DATE_TIME));
    try {
      if (sleepTime >= 0) {
        Thread.sleep(sleepTime * 1000);
      }
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    var poll = new StopPoll();
    poll.setChatId(chatId);
    poll.setMessageId(messageId);
    try {
      execute(poll);
    } catch (TelegramApiException e) {
      throw new RuntimeException(e);
    }
  }
}

