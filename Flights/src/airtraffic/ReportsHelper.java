package airtraffic;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public final class ReportsHelper {
   private static final DateTimeFormatter YEAR_MONTH_FORMAT = 
        DateTimeFormatter.ofPattern("MMM yyyy");
   private ReportsHelper() {
      // prevents instantiation
   }

   public static String formatYearMonth(YearMonth value) {
      return YEAR_MONTH_FORMAT.format(value);
   }
}