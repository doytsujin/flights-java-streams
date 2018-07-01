package airtraffic.app;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.splitByCharacterTypeCamelCase;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.enterprise.inject.se.SeContainer;
import javax.enterprise.inject.se.SeContainerInitializer;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;
import org.beryx.textio.TextTerminal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import airtraffic.Airport;
import airtraffic.Carrier;
import airtraffic.GeoLocation;
import airtraffic.ReportContext;
import airtraffic.Repository;
import airtraffic.annotations.IteratorStyle;
import airtraffic.annotations.StreamStyle;

public abstract class AbstractReportsApp<T> {
   private static final String METHOD_NAME_PREFIX = "report";
   private static final int METHOD_PARAMETER_COUNT = 1;
   private static final Class<?> METHOD_RETURN_TYPE = Void.TYPE;

   private final Logger logger = LoggerFactory.getLogger(AbstractReportsApp.class);
   private final TextIO io = TextIoFactory.getTextIO();
   private final TextTerminal<?> terminal = io.getTextTerminal();
   private final Repository repository = new Repository();
   private final SeContainer container = SeContainerInitializer.newInstance()
                                                               .initialize();

   protected abstract T impl();

   protected T getBean(Class<T> klass) {
      return container.select(klass, getStyleAnnotation()).get();
   }

   protected Annotation getStyleAnnotation() {
      String style = readStyleOption();
      switch(style) {
         case "iterator":  return IteratorStyle.INSTANCE;
         case "stream":    return StreamStyle.INSTANCE;
         default:
            throw new IllegalArgumentException("Unsupported style: " + style);
      }
   }

   protected ReportContext createReportContext() {
      return new ReportContext().setRepository(repository)
                                .setTerminal(terminal);
   }

   protected String readStyleOption() {
      String format = "%2d  %s\n";
      terminal.println("Style options:\n");
      terminal.printf(format, 0, "Exit program");
      terminal.printf(format, 1, "Iterator-based");
      terminal.printf(format, 2, "Stream-based");
      terminal.println();
      int option = io.newIntInputReader()
                     .withDefaultValue(0)
                     .withMinVal(0)
                     .withMaxVal(2)
                     .read("Style");
      if(option == 0) {
         System.exit(0);
      }
      return option == 1 ? "iterator" : "stream";
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

   protected int readYear() {
      Set<Integer> years = repository.getFlightYears();
      int min = years.stream().reduce(Integer::min).get();
      int year = years.stream().reduce(Integer::max).get();
      if(years.size() > 1) {
         terminal.println("There is flight data for the following years:");
         terminal.println(years.toString());
         year = readYear(min, year);
      } else {
         terminal.println("There is flight data for the year " + year);
      }
      return year;
   }

   protected String readState() {
      return readString("State").toUpperCase();
   }

   protected GeoLocation readGeoLocation() {
      return new GeoLocation() {
         private final double latitude = readDouble("Latitude", -90.0, 90.0);
         private final double longitude = readDouble("Longitude", -180.0, 180.0);
         @Override public double getLatitude()  { return latitude;  }
         @Override public double getLongitude() { return longitude; }
      };
   }

   protected int readDistanceInMiles() {
      return readDistance("(miles)");
   }

   private int readDistance(String units) {
      return readInt("Distance " + units, 1, 1000);
   }

   public void executeSelectedReport() throws Exception {
      List<Method> reportMethods = getReportMethods();
      int option = getReportOption(reportMethods, io);
      if(option == 0) {
         System.exit(0);
      }
      Method method = reportMethods.get(option-1);
      logger.debug("User requested invocation of method {}", method.getName());
      TextTerminal<?> terminal = io.getTextTerminal();
      terminal.println();
      terminal.println(getReportDescription(method));
      terminal.println();
      method.invoke(this, createReportContext());
      terminal.println("\n=== Report complete ===");
   }

   private List<Method> getReportMethods() {
      return Arrays.stream(getClass().getDeclaredMethods())
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

   private int getReportOption(List<Method> printMethods, TextIO io) {
      if(printMethods.size() == 0) {
         logger.warn("No report options available for this class");
         return 0;
      }
      TextTerminal<?> terminal = io.getTextTerminal();
      terminal.println("\nReport options:\n");
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

   private String getReportDescription(Method method) {
      String name = method.getName().substring(METHOD_NAME_PREFIX.length());
      String[] words = splitByCharacterTypeCamelCase(name);
      return Arrays.stream(words).collect(joining(" "));
   }
}