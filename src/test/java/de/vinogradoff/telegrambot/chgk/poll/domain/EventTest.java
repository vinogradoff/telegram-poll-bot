package de.vinogradoff.telegrambot.chgk.poll.domain;

import org.junit.jupiter.api.Test;

import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;


class EventTest {
  @Test
  void shouldParse4Params() {
    var event = Event.fromCmdArgs("/chgk понедельник тема 12:00 где-то", "", "", "", "");
    assertThat(event.dayOfWeek()).isEqualTo("понедельник");
    assertThat(event.place()).isEqualTo("где-то");
    assertThat(event.startTime().format(DateTimeFormatter.ISO_LOCAL_TIME)).isEqualTo("12:00:00");
    assertThat(event.otherInformation()).isNullOrEmpty();
  }

  @Test
  void shouldParseMoreParams() {
    var event = Event.fromCmdArgs("/chgk понедельник тема 12:00 где-то почему-то и это",
            "", "", "", "");
    assertThat(event.dayOfWeek()).isEqualTo("понедельник");
    assertThat(event.place()).isEqualTo("где-то");
    assertThat(event.startTime().format(DateTimeFormatter.ISO_LOCAL_TIME)).isEqualTo("12:00:00");
    assertThat(event.otherInformation()).isEqualTo("почему-то и это");
  }

  @Test
  void shouldParseShortDay() {
    var event = Event.fromCmdArgs("/chgk ср тема 12:00 где-то", "", "", "", "");
    assertThat(event.dayOfWeek()).isEqualTo("среда");
  }

  @Test
  void shouldParseShortTime() {
    var event = Event.fromCmdArgs("/chgk ср тема 6:05 где-то", "", "", "", "");
    assertThat(event.startTime().format(DateTimeFormatter.ISO_LOCAL_TIME)).isEqualTo("06:05:00");
  }

  @Test
  void shouldParseShortDate() {
    var event = Event.fromCmdArgs("/chgk 1.3", "", "12:00", "", "");
    assertThat(event.dayOfWeek()).isNotBlank();
  }
}