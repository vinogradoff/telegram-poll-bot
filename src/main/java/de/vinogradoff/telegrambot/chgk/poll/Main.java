package de.vinogradoff.telegrambot.chgk.poll;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {
  public static void main(String[] args) {
    var token=System.getenv("BOT_TOKEN");

    try {
      TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
      telegramBotsApi.registerBot(new CustomPollBot(token));
    } catch (TelegramApiException e) {
      e.printStackTrace();
    }
  }
}