package airtraffic;

import java.io.IOException;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;
import org.beryx.textio.TextTerminal;
import org.beryx.textio.jline.JLineTextTerminal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parent class for reporting apps.
 *
 * @author tony@piazzaconsulting.com
 */
public abstract class AbstractReportsProvider {
   protected static final int MAX_LIMIT = Integer.MAX_VALUE;
   private static final DateTimeFormatter YEAR_MONTH_FORMAT = 
      DateTimeFormatter.ofPattern("MMM yyyy");

   private final Logger logger = 
      LoggerFactory.getLogger(AbstractReportsProvider.class);
   private final TextIO io = TextIoFactory.getTextIO();
   private final TextTerminal<?> terminal = io.getTextTerminal();
//   private final Repository repository = new Repository();

   protected String left(String str, int len) {
      return StringUtils.left(str, len);
   }

   protected String repeat(String str, int len) {
      return StringUtils.repeat(str, len);
   }

   protected void clearScreen() {
      if(terminal instanceof JLineTextTerminal) {
         try {
            ((JLineTextTerminal) terminal).getReader().clearScreen();
         } catch (IOException e) {
            logger.debug("Unable to clear screen");
         }
      }
   }

   protected void moveLineToStart() {
      terminal.moveToLineStart();
   }

   protected void rawPrintf(String format, Object... args) {
      if(terminal instanceof JLineTextTerminal && 
         (SystemUtils.IS_OS_LINUX || SystemUtils.IS_OS_MAC_OSX)) {
         ((JLineTextTerminal) terminal).rawPrint(String.format(format, args));
      } else {
         terminal.printf(format, args);
      }
   }

   protected void print(String message) {
      terminal.print(message);
   }

   protected void println(String message) {
      terminal.println(message);
   }

   protected void println() {
      terminal.println();
   }

   protected void printf(String format, Object... args) {
      terminal.printf(format, args);
   }

   protected String readString(String prompt) {
      return io.newStringInputReader().read(prompt);
   }

   protected double readDouble(String prompt, double min, double max) {
      return io.newDoubleInputReader()
               .withMinVal(min)
               .withMaxVal(max)
               .read(prompt);
   }

   protected int readInt(String prompt, int min, int max) {
      return io.newIntInputReader()
               .withMinVal(min)
               .withMaxVal(max)
               .read(prompt);
   }

   protected int readLimit(int defaultValue, int min, int max) {
      return io.newIntInputReader()
               .withDefaultValue(defaultValue)
               .withMinVal(min)
               .withMaxVal(max)
               .read("Limit");
   }

   protected int readYear(int min, int max) {
      return io.newIntInputReader()
               .withDefaultValue(max)
               .withMinVal(min)
               .withMaxVal(max)
               .read("Year");
   }

   protected Airport readAirport(Repository repository, String prompt) {
      String iata = io.newStringInputReader()
                      .withValueChecker((val, item) -> 
                         repository.validAirport(val) ? 
                               Collections.emptyList() : 
                            Arrays.asList("Unknown airport specified") 
                      ).read(prompt);
      return repository.getAirport(iata);
   }

   protected Carrier readCarrier(Repository repository) {
      String code = io.newStringInputReader()
                      .withValueChecker((val, item) -> 
                         repository.validCarrier(val) ? 
                            Collections.emptyList() : 
                            Arrays.asList("Unknown carrier specified")
                      ).read("Carrier");
      return repository.getCarrier(code);
   }

   protected int selectYear(Repository repository) {
      Set<Integer> years = repository.getFlightYears();
      int min = years.stream().reduce(Integer::min).get();
      int year = years.stream().reduce(Integer::max).get();
      if(years.size() > 1) {
         println("There is flight data for the following years:");
         println(years.toString());
         year = readYear(min, year);
      } else {
         println("There is flight data for the year " + year);
      }
      return year;
   }

   protected String formatYearMonth(YearMonth value) {
      return YEAR_MONTH_FORMAT.format(value);
   }
}