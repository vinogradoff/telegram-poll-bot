package de.vinogradoff.telegrambot.chgk.poll.domain;

import org.junit.jupiter.api.Test;

import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;


class EventTest {
  @Test
  void shouldParse3Params() {
    var event = Event.fromCmdArgs("/chgk понедельник 12:00 что-то");
    assertThat(event.dayOfWeek()).isEqualTo("понедельник");
    assertThat(event.place()).isEqualTo("что-то");
    assertThat(event.startTime().format(DateTimeFormatter.ISO_LOCAL_TIME)).isEqualTo("12:00:00");
    assertThat(event.otherInformation()).isNullOrEmpty();
  }

  @Test
  void shouldParseMoreParams() {
    var event = Event.fromCmdArgs("/chgk понедельник 12:00 что-то где-то почему то ");
    assertThat(event.dayOfWeek()).isEqualTo("понедельник");
    assertThat(event.place()).isEqualTo("что-то");
    assertThat(event.startTime().format(DateTimeFormatter.ISO_LOCAL_TIME)).isEqualTo("12:00:00");
    assertThat(event.otherInformation()).isEqualTo("где-то почему то");
  }

  @Test
  void shouldParseShortDay() {
    var event = Event.fromCmdArgs("/chgk ср 12:00 что-то");
    assertThat(event.dayOfWeek()).isEqualTo("среда");
  }

  @Test
  void shouldParseShortTime() {
    var event = Event.fromCmdArgs("/chgk ср 6:05 что-то");
    assertThat(event.startTime().format(DateTimeFormatter.ISO_LOCAL_TIME)).isEqualTo("06:05:00");

  }

  @Test
  void shouldParseShortDate() {
    var event = Event.fromCmdArgs("/chgk 1.3 12:00 что-то");
    assertThat(event.dayOfWeek()).isNotBlank();
  }
}