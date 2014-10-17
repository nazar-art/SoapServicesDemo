/**
 *
 */
package core.utils;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class DateUtils {

    public static final String DEFAULT_DATE_FORMAT = "dd.MM.yyyy";

    public static String formatDate(DateTime date, String format) {
        return DateTimeFormat.forPattern(format).print(date);
    }

    public static DateTime parseDate(String dateTime, String pattern) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern(pattern);
        return formatter.parseDateTime(dateTime);
    }

    public static String formateDate(DateTime date) {
        return formatDate(date, DEFAULT_DATE_FORMAT);
    }

    public static int getTimeStamp() {
        return DateTime.now().toDateTime().getMillisOfDay();
    }

    public static DateTime getFirstDayOfCurrentYear() {
        return getFirstDayOfYear(DateTime.now());
    }

    public static DateTime getFirstDayOfYear(DateTime date) {
        return date.withDayOfYear(1);
    }
}
