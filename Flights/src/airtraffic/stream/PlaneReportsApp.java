package airtraffic.stream;

import static java.util.Comparator.reverseOrder;
import static java.util.Map.Entry.comparingByKey;
import static java.util.Map.Entry.comparingByValue;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

import java.util.Arrays;
import java.util.List;

import airtraffic.AbstractReportsApp;
import airtraffic.Flight;
import airtraffic.Plane;
import airtraffic.PlaneAgeRange;
import airtraffic.PlaneModel;
import airtraffic.Repository;

/**
 * Generate various airplane statistics using Java 8 streams.
 *
 * @author tony@piazzaconsulting.com
 */
public class PlaneReportsApp extends AbstractReportsApp {
   private static final List<PlaneAgeRange> AGE_RANGES =
      Arrays.asList(PlaneAgeRange.between(   0,  5),
                    PlaneAgeRange.between(   6,  10),
                    PlaneAgeRange.between(  11,  20),
                    PlaneAgeRange.between(  21,  30),
                    PlaneAgeRange.between(  31,  40),
                    PlaneAgeRange.between(  41,  50),
                    PlaneAgeRange.between(  51, 100));

   public static void main(String[] args) throws Exception {
      new PlaneReportsApp().executeSelectedReport();
   }

   public void reportTotalPlanesByManfacturer(Repository repository) {
      println("Manufacturer\t\t\tCount");
      println("---------------------------------------");

      repository.getPlaneStream()
                .collect(groupingBy(Plane::getManufacturer, counting()))
                .entrySet()
                .stream()
                .sorted(comparingByValue(reverseOrder()))
                .forEach(e -> printf("%-25s\t%5d\n", e.getKey(), e.getValue()));
   }

   public void reportTotalPlanesByYear(Repository repository) {
      println("Year\tCount");
      println("------------------");

      repository.getPlaneStream()
                .filter(p -> p.getYear() > 0)
                .collect(groupingBy(Plane::getYear, counting()))
                .entrySet()
                .stream()
                .sorted(comparingByKey(reverseOrder()))
                .forEach(e -> printf("%4d\t%5d\n", e.getKey(), e.getValue()));
   }

   public void reportTotalPlanesByAircraftType(Repository repository) {
      println("Aircraft Type\t\t\tCount");
      println("---------------------------------------");

      repository.getPlaneStream()
                .collect(groupingBy(Plane::getAircraftType, counting()))
                .entrySet()
                .stream()
                .sorted(comparingByValue(reverseOrder()))
                .forEach(e -> printf("%-25s\t%5d\n", e.getKey(), e.getValue()));
   }

   public void reportTotalPlanesByEngineType(Repository repository) {
      println("Engine Type\t\t\tCount");
      println("---------------------------------------");

      repository.getPlaneStream()
                .collect(groupingBy(Plane::getEngineType, counting()))
                .entrySet()
                .stream()
                .sorted(comparingByValue(reverseOrder()))
                .forEach(e -> printf("%-25s\t%5d\n", e.getKey(), e.getValue()));
   }

   public void reportPlanesWithMostCancellations(Repository repository) {
      final int year = selectYear();
      final int limit = readLimit(10, 1, 100);

      println("Tail #\t\tCount");
      println("-----------------------");

      repository.getFlightStream(year)
                .filter(f -> f.cancelled() && f.validTailNumber())
                .collect(groupingBy(Flight::getTailNumber, counting()))
                .entrySet()
                .stream()
                .sorted(comparingByValue(reverseOrder()))
                .limit(limit)
                .forEach(e -> printf("%-8s\t%,6d\n", e.getKey(), e.getValue()));
   }

   public void reportMostFlightsByPlane(Repository repository) {
      final int year = selectYear();
      final int limit = readLimit(10, 1, 100);

      println("Tail #\t  Manufacturer\t\tModel #\t\tCount");
      println(repeat("-", 67));

      repository.getFlightStream(year)
                .filter(f -> f.notCancelled() && f.validTailNumber())
                .collect(groupingBy(Flight::getPlane, counting()))
                .entrySet()
                .stream()
                .sorted(comparingByValue(reverseOrder()))
                .limit(limit)
                .forEachOrdered(e -> {
                   Plane plane = e.getKey();
                   printf("%-8s  %-20s  %-10s  %,10d\n", 
                          plane.getTailNumber(), 
                          left(plane.getManufacturer(), 20),
                          left(plane.getModel().getModelNumber(), 10),
                          e.getValue());
                });
   }

   public void reportMostFlightsByPlaneModel(Repository repository) {
      final int year = selectYear();
      final int limit = readLimit(10, 1, 100);

      println("Manufacturer\t\t\tModel #\t\t\t  Count\t\tDaily Avg");
      println(repeat("-", 82));

      repository.getFlightStream(year)
                .filter(f -> f.notCancelled())
                .map(p -> p.getPlane())
                .collect(groupingBy(Plane::getModel, counting()))
                .entrySet()
                .stream()
                .sorted(comparingByValue(reverseOrder()))
                .limit(limit)
                .map(e -> {
                   PlaneModel model = e.getKey();
                   Long count = e.getValue();
                   return String.format("%-25s\t%-20s\t%,10d\t%8.1f",
                                        model.getManufacturer(),
                                        model.getModelNumber(),
                                        count,
                                        count.floatValue() / 365);
                }).forEachOrdered(s -> println(s));
   }

   public void reportTotalFlightsByPlaneManufacturer(Repository repository) {
      final int year = selectYear();

      println("Manufacturer\t\t\t Count");
      println("-------------------------------------------");

      repository.getFlightStream(year)
                .filter(f -> f.notCancelled())
                .map(f -> f.getPlane())
                .collect(groupingBy(Plane::getManufacturer, counting()))
                .entrySet()
                .stream()
                .sorted(comparingByValue(reverseOrder()))
                .forEachOrdered(e -> printf("%-25s\t%,10d\n", 
                                            e.getKey(), 
                                            e.getValue()));
   }

   public void reportTotalFlightsByPlaneAgeRange(Repository repository) {
      final int year = selectYear();

      println("Age Range\tCount");
      println(repeat("-", 27));

      long total = repository.getFlightStream(year)
                             .filter(f -> f.notCancelled() && 
                                          f.getPlane().getYear() > 0)
                             .collect(groupingBy(PlaneAgeRange.classifier(AGE_RANGES), 
                                                 counting()))
                             .entrySet()
                             .stream()
                             .sorted(comparingByKey())
                             .peek(e -> printf("%-10s\t%,10d\n", 
                                               e.getKey(), e
                                               .getValue()))
                             .mapToLong(e -> e.getValue())
                             .sum();

      println(repeat("-", 27));
      printf("Total\t       %,11d\n", total);
   }

   public void reportTotalFlightsByAircraftType(Repository repository) {
      final int year = selectYear();

      println("Aircraft Type\t\t\tCount");
      println("-------------------------------------------");

      repository.getFlightStream(year)
                .filter(f -> f.notCancelled())
                .map(f -> f.getPlane())
                .collect(groupingBy(Plane::getAircraftType, counting()))
                .entrySet()
                .stream()
                .sorted(comparingByValue(reverseOrder()))
                .forEachOrdered(e -> printf("%-25s\t%,10d\n", 
                                            e.getKey(), 
                                            e.getValue()));
   }

   public void reportTotalFlightsByEngineType(Repository repository) {
      final int year = selectYear();

      println("Engine Type\t\t\tCount");
      println("-------------------------------------------");

      repository.getFlightStream(year)
                .filter(f -> f.notCancelled())
                .map(f -> f.getPlane())
                .collect(groupingBy(Plane::getEngineType, counting()))
                .entrySet()
                .stream()
                .sorted(comparingByValue(reverseOrder()))
                .forEachOrdered(e -> printf("%-25s\t%,10d\n", 
                                            e.getKey(), 
                                            e.getValue()));
   }
}