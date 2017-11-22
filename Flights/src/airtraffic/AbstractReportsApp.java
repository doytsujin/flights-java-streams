package airtraffic;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.splitByCharacterTypeCamelCase;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;
import org.beryx.textio.TextTerminal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parent class for reporting apps.
 *
 * @author tony@piazzaconsulting.com
 */
public abstract class AbstractReportsApp {
   private static final String REPORT_METHOD_NAME_PREFIX = "report";
   private static final int REPORT_METHOD_PARAMETER_COUNT = 1;
   private static final Class<?> REPORT_METHOD_RETURN_TYPE = Void.TYPE;
   private static final DateTimeFormatter YEAR_MONTH_FORMAT = DateTimeFormatter.ofPattern("MMM yyyy");

   private final Logger logger = LoggerFactory.getLogger(AbstractReportsApp.class);
   private final TextIO io = TextIoFactory.getTextIO();
   private final TextTerminal<?> terminal = io.getTextTerminal();
   private final Repository repository = new Repository();

   protected List<Method> getReportMethods() {
      return Arrays.stream(this.getClass().getDeclaredMethods())
                   .filter(m -> Modifier.isPublic(m.getModifiers()) &&
                           m.getName().startsWith(REPORT_METHOD_NAME_PREFIX) &&
                           m.getParameterTypes().length == REPORT_METHOD_PARAMETER_COUNT &&
                           m.getReturnType().equals(REPORT_METHOD_RETURN_TYPE))
                   .sorted((m1, m2) -> m1.getName().compareTo(m2.getName()))
                   .collect(toList());
   }

   protected String left(String str, int len) {
      return StringUtils.left(str, len);
   }

   protected String repeat(String str, int len) {
      return StringUtils.repeat(str, len);
   }

   protected void print(String message) {
      terminal.print(message);
   }

   protected void println(String message) {
      terminal.println(message);
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
                         repository.getAirport(val.toUpperCase()) == null ? 
                            Arrays.asList("Unknown airport specified") :
                            Collections.emptyList()
                      )
                      .read(prompt);
      return repository.getAirport(iata.toUpperCase());
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
      int optionNum = getReportOption(reportMethods);
      if(optionNum == 0) {
         System.exit(0);
      }
      Method method = reportMethods.get(optionNum-1);
      logger.debug("User requested invocation of method {}", method.getName());
      terminal.println();
      terminal.println(getReportDescription(method));
      terminal.println();
      method.invoke(this, repository);
      terminal.println("\n=== Report complete ===");
   }

   protected int getReportOption(List<Method> printMethods) {
      TextTerminal<?> terminal = io.getTextTerminal();
      terminal.println("\nReport options:\n");
      String format = "%2d  %s\n";
      int n = 0;
      terminal.printf(format, n, "Exit program");
      for(Method m : printMethods) {
         terminal.printf(format, ++n, getReportDescription(m));
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
      String name = method.getName().substring(REPORT_METHOD_NAME_PREFIX.length());
      String[] words = splitByCharacterTypeCamelCase(name);
      return Arrays.stream(words).collect(joining(" "));
   }

   protected String formatYearMonth(YearMonth value) {
      return YEAR_MONTH_FORMAT.format(value);
   }
}