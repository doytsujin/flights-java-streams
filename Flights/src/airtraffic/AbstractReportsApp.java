package airtraffic;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.splitByCharacterTypeCamelCase;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
public abstract class AbstractReportsApp {
   private static final String METHOD_NAME_PREFIX = "report";
   private static final int METHOD_PARAMETER_COUNT = 1;
   private static final Class<?> METHOD_RETURN_TYPE = Void.TYPE;
   private static final DateTimeFormatter YEAR_MONTH_FORMAT = 
      DateTimeFormatter.ofPattern("MMM yyyy");

   private final Logger logger = 
      LoggerFactory.getLogger(AbstractReportsApp.class);
   private final TextIO io = TextIoFactory.getTextIO();
   private final TextTerminal<?> terminal = io.getTextTerminal();
   private final Repository repository = new Repository();

   protected List<Method> getReportMethods() {
      return Arrays.stream(this.getClass().getDeclaredMethods())
                   .filter(method -> methodFilter(method))
                   .sorted((m1, m2) -> m1.getName().compareTo(m2.getName()))
                   .collect(toList());
   }

   private boolean methodFilter(Method method) {
      return Modifier.isPublic(method.getModifiers()) &&
            method.getName().startsWith(METHOD_NAME_PREFIX) &&
            method.getParameterTypes().length == METHOD_PARAMETER_COUNT &&
            method.getReturnType().equals(METHOD_RETURN_TYPE);
   }

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

   protected Airport readAirport(String prompt) {
      String iata = io.newStringInputReader()
                      .withValueChecker((val, item) -> 
                         repository.validAirport(val) ? 
                               Collections.emptyList() : 
                            Arrays.asList("Unknown airport specified") 
                      ).read(prompt);
      return repository.getAirport(iata);
   }

   protected Carrier readCarrier() {
      String code = io.newStringInputReader()
                      .withValueChecker((val, item) -> 
                         repository.validCarrier(val) ? 
                            Collections.emptyList() : 
                            Arrays.asList("Unknown carrier specified")
                      ).read("Carrier");
      return repository.getCarrier(code);
   }

   protected int selectYear() {
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

   protected void executeSelectedReport() throws Exception {
      List<Method> reportMethods = getReportMethods();
      int option = getReportOption(reportMethods);
      if(option == 0) {
         System.exit(0);
      }
      Method method = reportMethods.get(option-1);
      logger.debug("User requested invocation of method {}", method.getName());
      terminal.println();
      terminal.println(getReportDescription(method));
      terminal.println();
      method.invoke(this, repository);
      terminal.println("\n=== Report complete ===");
   }

   protected int getReportOption(List<Method> printMethods) {
      if(printMethods.size() == 0) {
         logger.warn("No report options available for this class");
         return 0;
      }
      TextTerminal<?> terminal = io.getTextTerminal();
      terminal.println("Report options:\n");
      String format = "%2d  %s\n";
      int n = 0;
      terminal.printf(format, n++, "Exit program");
      for(Method m : printMethods) {
         terminal.printf(format, n++, getReportDescription(m));
         logger.debug("Found report method {}", m.getName());
      }
      terminal.println();
      return io.newIntInputReader()
               .withDefaultValue(0)
               .withMinVal(0)
               .withMaxVal(printMethods.size())
               .read("Option");
   }

   protected String getReportDescription(Method method) {
      String name = method.getName().substring(METHOD_NAME_PREFIX.length());
      String[] words = splitByCharacterTypeCamelCase(name);
      return Arrays.stream(words).collect(joining(" "));
   }

   protected String formatYearMonth(YearMonth value) {
      return YEAR_MONTH_FORMAT.format(value);
   }
}