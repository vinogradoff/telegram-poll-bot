package de.vinogradoff.telegrambot.chgk.poll.domain;

import org.apache.tools.ant.types.Commandline;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

public record Event(LocalDateTime startTime, String dayOfWeek, String topic, String place, String otherInformation) {

  /**
   * @param parts must be at least 3 parameters "/cmd param1 param2 [param3] [param4] [more paramX]".
   *              Formats
   *              param1: dd.MM (Date) or day (in Russion) понедельник, пн
   *              param2: HH:mm (Time)
   *              param3: topic
   *              param4: place
   *              param5: any text
   * @return event
   */
  public static Event fromCmdArgs(List<String> parts, String defaultTopic, String defaultTime, String defaultPlace, String defaultOtherInformation) {
    if (parts.size() < 2) throw new RuntimeException("Must be at least cmd and 1 params");
    // date or day of week
    String shortDate;
    if (Character.isLetter(parts.get(1).charAt(0))) {
      var dayOfWeek = parseRussianDay(parts.get(1));
      shortDate = LocalDate.now().with(TemporalAdjusters.next(dayOfWeek))
              .format(DateTimeFormatter.ofPattern("dd.MM"));
    } else {
      shortDate = parts.get(1);
    }
    var topic = parts.get(2) == null ? defaultTopic : parts.get(2);
    var time = parts.get(3) == null ? defaultTime : parts.get(3);
    var place = parts.get(4) == null ? defaultPlace : parts.get(4);

    var otherInformation = ""; // optional
    if (parts.size() > 6) {
      // add all other arguments
      StringBuilder otherInformationBuilder = new StringBuilder();
      for (int i = 5; i < parts.size(); i++) {
        otherInformationBuilder.append(parts.get(i)).append(" ");
      }
      otherInformation = otherInformationBuilder.toString();
      otherInformation = otherInformation.trim();
    } else {
      otherInformation = parts.get(5) == null ? defaultOtherInformation : parts.get(5);

    }
    // convert to Day
    var sdf = DateTimeFormatter.ofPattern("d.M.yyyy H:mm");
    var year = LocalDate.now().getYear();
    if (LocalDateTime.parse(shortDate + "." + year + " 23:59", sdf).isBefore(LocalDateTime.now()))
      year++; //if date in the past - take next year
    var date = LocalDateTime.parse(shortDate + "." + year + " " + time, sdf);

    var simpleDay = DateTimeFormatter.ofPattern("EEEE", Locale.forLanguageTag("ru"));
    var day = simpleDay.format(date);

    return new Event(
            date,
            day,
            topic,
            place,
            otherInformation
    );

  }


  private static DayOfWeek parseRussianDay(String russianDay) {
    switch (russianDay.toLowerCase()) {
      case "понедельник", "пн" -> {
        return DayOfWeek.MONDAY;
      }
      case "вторник", "вт" -> {
        return DayOfWeek.TUESDAY;
      }
      case "среда", "ср" -> {
        return DayOfWeek.WEDNESDAY;
      }
      case "четверг", "чт" -> {
        return DayOfWeek.THURSDAY;
      }
      case "пятница", "пт" -> {
        return DayOfWeek.FRIDAY;
      }
      case "суббота", "сб" -> {
        return DayOfWeek.SATURDAY;
      }
      case "воскресенье", "вс" -> {
        return DayOfWeek.SUNDAY;
      }
      default -> throw new RuntimeException("Invalid day of week: " + russianDay);
    }
  }

  public static Event fromCmdArgs(String cmd, String defaultTopic, String defaultTime, String defaultPlace, String defaultOtherInformation) {
    List<String> parts = new ArrayList<>(Arrays.asList(Commandline.translateCommandline(cmd)));
    while (parts.size() < 6) {
      parts.add(null);
    }

    return fromCmdArgs(parts, defaultTopic, defaultTime, defaultPlace, defaultOtherInformation);
  }
}
