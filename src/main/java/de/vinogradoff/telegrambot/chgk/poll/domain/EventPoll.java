package de.vinogradoff.telegrambot.chgk.poll.domain;

import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;

import java.time.*;


public record EventPoll(Event event, SendPoll poll) {

  /**
   * Calculates number of second between from and Event plus waiting period
   *
   * @param from          e.g. now
   * @param event         event
   * @param waitingPeriod e.g. 1 hour
   * @return
   */
  public static Long calulateTimeToEvent(LocalDateTime from, Event event, Duration waitingPeriod) {
    return Duration.between(from, event.startTime().plus(waitingPeriod)).getSeconds();
  }
}
