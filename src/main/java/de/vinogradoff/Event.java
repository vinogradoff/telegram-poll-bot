package de.vinogradoff;

import java.text.*;
import java.time.*;
import java.util.*;

public record Event(LocalDateTime startTime, String dayOfWeek, String place, String otherInformation) {

  /**
   * @param cmdArgs must be at least 3 parameters "/cmd param1 param2 param3 [param4] [more paramX]".
   *                Formats
   *                param1: dd.MM (Date)
   *                param2: HH:mm (Time)
   *                param3: any text
   *                param4: any text
   * @return event
   * @throws ParseException if param1 or param2 malformed
   */
  public static Event fromCmdArgs(String cmdArgs) throws ParseException {
    var parts = Arrays.asList(cmdArgs.split(" "));
    if (parts.size() < 4) throw new RuntimeException("Must be at least 3 parts");
    var shortDate = parts.get(1);
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
    var sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
    //TODO fix for the year change
    var year = LocalDate.now().getYear();
    var date = sdf.parse(shortDate + "." + year + " " + time);
    var simpleDay = new SimpleDateFormat("EEEE", new Locale("ru"));
    var day = simpleDay.format(date);

    return new Event(
            date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(),
            day,
            place,
            otherInformation
    );

  }
}
