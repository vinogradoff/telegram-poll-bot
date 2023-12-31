package de.vinogradoff.telegrambot.chgk.poll.domain;

import java.text.ParseException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

public record Event(LocalDateTime startTime, String dayOfWeek, String place, String otherInformation) {

  /**
   * @param cmdArgs must be at least 3 parameters "/cmd param1 param2 param3 [param4] [more paramX]".
   *                Formats
   *                param1: dd.MM (Date) or day (in Russion) понедельник, пн
   *                param2: HH:mm (Time)
   *                param3: any text
   *                param4: any text
   * @return event
   * @throws ParseException if param1 or param2 malformed
   */
  public static Event fromCmdArgs(String cmdArgs) throws ParseException {
    var parts = Arrays.asList(cmdArgs.split(" "));
    if (parts.size() < 4) throw new RuntimeException("Must be at least 3 parts");
    // date or day of week
    String shortDate;
    if (Character.isLetter(parts.get(1).charAt(0))) {
      var dayOfWeek = parseRussianDay(parts.get(1));
      shortDate = LocalDate.now().with(TemporalAdjusters.next(dayOfWeek))
              .format(DateTimeFormatter.ofPattern("dd.MM"));
    } else {
      shortDate = parts.get(1);
    }
    var time = parts.get(2);
    var place = parts.get(3);
    var otherInformation = ""; // optional
    if (parts.size() > 4) {
      // add all other arguments
      for (int i = 4; i < parts.size(); i++) {
        otherInformation += parts.get(i) + " ";
      }
      otherInformation = otherInformation.trim();
    }


    // convert to Day
    var sdf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    var year = LocalDate.now().getYear();
    if (LocalDateTime.parse(shortDate + "." + year + " 23:59", sdf).isBefore(LocalDateTime.now()))
      year++; //if date in the past - take next year
    var date = LocalDateTime.parse(shortDate + "." + year + " " + time, sdf);

    var simpleDay = DateTimeFormatter.ofPattern("EEEE", new Locale("ru"));
    var day = simpleDay.format(date);

    return new Event(
            date,
            day,
            place,
            otherInformation
    );

  }

  private static DayOfWeek parseRussianDay(String russianDay) {
    switch (russianDay.toLowerCase()) {
      case "понедельник", "пн":
        return DayOfWeek.MONDAY;
      case "вторник", "вт":
        return DayOfWeek.TUESDAY;
      case "среда", "ср":
        return DayOfWeek.WEDNESDAY;
      case "четверг", "чт":
        return DayOfWeek.THURSDAY;
      case "пятница", "пт":
        return DayOfWeek.FRIDAY;
      case "суббота", "сб":
        return DayOfWeek.SATURDAY;
      case "воскресенье", "вс":
        return DayOfWeek.SUNDAY;
      default:
        throw new RuntimeException("Invalid day of week: " + russianDay);
    }
  }
}
