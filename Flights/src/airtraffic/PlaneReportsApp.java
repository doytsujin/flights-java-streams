package airtraffic;

import static java.util.Comparator.reverseOrder;
import static java.util.Map.Entry.comparingByKey;
import static java.util.Map.Entry.comparingByValue;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

import java.util.stream.Stream;

/**
 * Generate various airplane statistics using Java 8 streams.
 *
 * @author tony@piazzaconsulting.com
 */
public class PlaneReportsApp extends AbstractReportsApp {
   public static void main(String[] args) throws Exception {
      new PlaneReportsApp().executeSelectedReport();
   }

   public void reportPlaneCountsByManfacturer(Repository repository) {
      Stream<Plane> source = repository.getPlaneStream();
      println("Manufacturer\t\t\tCount");
      println("---------------------------------------");
      source.collect(groupingBy(Plane::getManufacturer, counting()))
            .entrySet()
            .stream()
            .sorted(comparingByValue(reverseOrder()))
            .forEach(e -> printf("%-25s\t%5d\n", e.getKey(), e.getValue()));
   }

   public void reportPlaneCountsByYear(Repository repository) {
      Stream<Plane> source = repository.getPlaneStream();
      println("Year\tCount");
      println("------------------");
      source.filter(p -> p.getYear() > 0)
            .collect(groupingBy(Plane::getYear, counting()))
            .entrySet()
            .stream()
            .sorted(comparingByKey(reverseOrder()))
            .forEach(e -> printf("%4d\t%5d\n", e.getKey(), e.getValue()));
   }

   public void reportPlaneCountsByAircraftType(Repository repository) {
      Stream<Plane> source = repository.getPlaneStream();
      println("Aircraft Type\t\t\tCount");
      println("---------------------------------------");
      source.collect(groupingBy(Plane::getAircraftType, counting()))
            .entrySet()
            .stream()
            .sorted(comparingByValue(reverseOrder()))
            .forEach(e -> printf("%-25s\t%5d\n", e.getKey(), e.getValue()));
   }

   public void reportPlaneCountsByEngineType(Repository repository) {
      Stream<Plane> source = repository.getPlaneStream();
      println("Engine Type\t\t\tCount");
      println("---------------------------------------");
      source.collect(groupingBy(Plane::getEngineType, counting()))
            .entrySet()
            .stream()
            .sorted(comparingByValue(reverseOrder()))
            .forEach(e -> printf("%-25s\t%5d\n", e.getKey(), e.getValue()));
   }
}