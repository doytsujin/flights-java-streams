package airtraffic.app;

import static java.util.Comparator.comparingInt;
import static java.util.Comparator.reverseOrder;
import static java.util.Map.Entry.comparingByKey;
import static java.util.Map.Entry.comparingByValue;
import static java.util.stream.Collectors.averagingInt;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Stream;

import airtraffic.Airport;
import airtraffic.Flight;
import airtraffic.FlightDistanceRange;
import airtraffic.Repository;

/**
 * Generate various flight statistics using Java 8 streams.
 * 
 * @author tony@piazzaconsulting.com
 */
public class FlightReportsApp extends AbstractReportsApp {
   private static final List<FlightDistanceRange> DISTANCE_RANGES =
      Arrays.asList(FlightDistanceRange.between(   0,  100), 
                    FlightDistanceRange.between( 101,  250),
                    FlightDistanceRange.between( 251,  500),
                    FlightDistanceRange.between( 501, 1000),
                    FlightDistanceRange.between(1001, 2500),
                    FlightDistanceRange.between(2501, 5000));

   public static void main(String[] args) throws Exception {
      new FlightReportsApp().executeSelectedReport();
   }

   public void reportTotalFlightsFromOrigin(Repository repository) {
      int year = selectYear();
      Stream<Flight> source = repository.getFlightStream(year);
      Airport origin = readAirport("Origin");
      long count = source.filter(f -> f.notCancelled() && 
                                      f.getOrigin().equals(origin))
                         .count();
      printf("Total flights from %s is %,d\n", origin.getName().trim(), count);
   }

   public void reportTotalFlightsToDestination(Repository repository) {
      int year = selectYear();
      Stream<Flight> source = repository.getFlightStream(year);
      Airport destination = readAirport("Destination");
      long count = source.filter(f -> f.notCancelled() && 
                                      f.notDiverted() && 
                                      f.getDestination().equals(destination))
                         .count();
      printf("Total flights to %s is %,d\n", destination.getName().trim(), count);
   }

   public void reportTotalFlightsFromOriginToDestination(Repository repository) {
      int year = selectYear();
      Stream<Flight> source = repository.getFlightStream(year);
      Airport origin = readAirport("Origin");
      Airport destination = readAirport("Destination");
      long count = source.filter(f -> f.notCancelled() && 
                                      f.notDiverted() &&
                                      f.getOrigin().equals(origin) &&
                                      f.getDestination().equals(destination))
                         .count();
      printf("Total of %,d flights from %s (%s)\nto %s (%s)\n", 
             count,
             origin.getName().trim(), 
             origin.getIATA(), 
             destination.getName().trim(), 
             destination.getIATA()
      ); 
   }

   public void reportTopFlightsByOrigin(Repository repository) {
      int year = selectYear();
      Stream<Flight> source = repository.getFlightStream(year);
      int limit = readLimit(10, 1, 100);
      println("\nOrigin\t\tCount");
      println("---------------------------");
      source.filter(f -> f.notCancelled())
            .collect(groupingBy(Flight::getOrigin, counting()))
            .entrySet()
            .stream()
            .sorted(comparingByValue(reverseOrder()))
            .limit(limit)
            .forEachOrdered(e -> printf(" %3s\t\t%,10d\n", 
                                        e.getKey().getIATA(), e.getValue()));
   }

   public void reportTopDestinationsFromOrigin(Repository repository) {
      int year = selectYear();
      Stream<Flight> source = repository.getFlightStream(year);
      Airport origin = readAirport("Origin");
      int limit = readLimit(10, 1, 100);
      printf("Top destinations from %s\n\n", origin.getName());
      println("Destination\t   Count");
      println("------------------------------");
      source.filter(f -> f.notCancelled() && f.getOrigin().equals(origin))
            .collect(groupingBy(Flight::getDestination, counting()))
            .entrySet()
            .stream()
            .sorted(comparingByValue(reverseOrder()))
            .limit(limit)
            .forEachOrdered(e -> printf(" %3s\t\t%,10d\n", 
                                        e.getKey().getIATA(), e.getValue()));
   }

   public void reportMostPopularRoutes(Repository repository) {
      int year = selectYear();
      Stream<Flight> source = repository.getFlightStream(year);
      int limit = readLimit(10, 1, 100);
      println("Route\t\t    Count");
      println("---------------------------");
      source.collect(groupingBy(Flight::getRoute, counting()))
            .entrySet()
            .stream()
            .sorted(comparingByValue(reverseOrder()))
            .limit(limit)
            .forEachOrdered(e -> printf("%s\t%,10d\n", 
                                        e.getKey(), e.getValue().intValue()));
   }

   public void reportWorstAverageDepartureDelayByOrigin(Repository repository) {
      int year = selectYear();
      Stream<Flight> source = repository.getFlightStream(year);
      int limit = readLimit(10, 1, 100);
      println("Origin\tDelay (min)");
      println("----------------------");
      source.filter(f -> f.notCancelled())
            .collect(groupingBy(Flight::getOrigin, 
                                averagingInt(f -> f.getDepartureDelay())))
            .entrySet()
            .stream()
            .sorted(comparingByValue(reverseOrder()))
            .limit(limit)
            .forEachOrdered(e -> printf(" %3s\t\t%.0f\n", 
                                        e.getKey().getIATA(), e.getValue()));
   }

   public void reportWorstAverageArrivalDelayByDestination(Repository repository) {
      int year = selectYear();
      Stream<Flight> source = repository.getFlightStream(year);
      int limit = readLimit(10, 1, 100);
      println("Destination\tDelay (min)");
      println("----------------------------");
      source.filter(f -> f.notCancelled() && f.notDiverted())
            .collect(groupingBy(Flight::getDestination, 
                                averagingInt(f -> f.getArrivalDelay())))
            .entrySet()
            .stream()
            .sorted(comparingByValue(reverseOrder()))
            .limit(limit)
            .forEachOrdered(e -> printf(" %3s\t\t\t%.0f\n", 
                                        e.getKey().getIATA(), e.getValue()));
   }

   public void reportMostCancelledFlightsByOrigin(Repository repository) {
      int year = selectYear();
      Stream<Flight> source = repository.getFlightStream(year);
      int limit = readLimit(10, 1, 100);
      println("Origin\t\t  Count");
      println("---------------------------");
      source.filter(f -> f.cancelled())
            .collect(groupingBy(Flight::getOrigin, counting()))
            .entrySet()
            .stream()
            .sorted(comparingByValue(reverseOrder()))
            .limit(limit)
            .forEachOrdered(e -> printf(" %3s\t\t%,8d\n", 
                                        e.getKey().getIATA(), e.getValue()));
   }

   public void reportTotalFlightsByOriginState(Repository repository) {
      int year = selectYear();
      Stream<Flight> source = repository.getFlightStream(year);
      int limit = readLimit(10, 1, 100);
      println("State\t  Count");
      println("-------------------");
      source.filter(f -> f.notCancelled())
            .map(f -> f.getOrigin())
            .collect(groupingBy(Airport::getState, counting()))
            .entrySet()
            .stream()
            .sorted(comparingByValue(reverseOrder()))
            .limit(limit)
            .forEachOrdered(e -> printf("%2s\t%,10d\n", e.getKey(), e.getValue()));
   }

   public void reportTotalFlightsByDestinationState(Repository repository) {
      int year = selectYear();
      Stream<Flight> source = repository.getFlightStream(year);
      int limit = readLimit(10, 1, 100);
      println("State\tCount");
      println("-------------------");
      source.filter(f -> f.notCancelled() && f.notDiverted())
            .map(f -> f.getDestination())
            .collect(groupingBy(Airport::getState, counting()))
            .entrySet()
            .stream()
            .sorted(comparingByValue(reverseOrder()))
            .limit(limit)
            .forEachOrdered(e -> printf("%2s\t%,10d\n", e.getKey(), e.getValue()));
   }

   public void reportLongestFlights(Repository repository) {
      byDistance(repository, comparingInt(Flight::getDistance).reversed());
   }

   public void reportShortestFlights(Repository repository) {
      byDistance(repository, comparingInt(f -> f.getDistance()));
   }

   private void byDistance(Repository repository, Comparator<Flight> comparator) {
      int year = selectYear();
      Stream<Flight> source = repository.getFlightStream(year);
      int limit = readLimit(10, 1, 100);
      println("Flight #     Date\tOrigin\tDestination\tDistance");
      println(repeat("-", 57));
      source.filter(f -> f.notCancelled() && f.notDiverted())
            .sorted(comparator)
            .limit(limit)
            .forEach(f -> printf("%-8s  %10s\t %3s\t    %3s\t\t%4d\n",
                                 f.getFlightNumber(),
                                 f.getDate(),
                                 f.getOrigin().getIATA(),
                                 f.getDestination().getIATA(),
                                 f.getDistance())
            );
   }

   public void reportTotalFlightsByDistanceRange(Repository repository) {
      int year = selectYear();
      Stream<Flight> source = repository.getFlightStream(year);
      println("Range\t\tCount");
      println(repeat("-", 27));
      source.filter(f -> f.notCancelled() && f.notDiverted())
            .collect(groupingBy(FlightDistanceRange.classifier(DISTANCE_RANGES),
                                counting()))
            .entrySet()
            .stream()
            .sorted(comparingByKey())
            .forEach(e -> printf("%-10s\t%,10d\n", e.getKey(), e.getValue()));
   }

   public void reportDaysWithLeastCancellations(Repository repository) {
      byDaysWithCancellations(repository, comparingByValue());
   }

   public void reportDaysWithMostCancellations(Repository repository) {
      byDaysWithCancellations(repository, comparingByValue(reverseOrder()));
   }

   private void byDaysWithCancellations(Repository repository, 
      Comparator<Entry<LocalDate, Long>> comparator) {
      int year = selectYear();
      Stream<Flight> source = repository.getFlightStream(year);
      int limit = readLimit(10, 1, 100);
      println("Date\t\tCount");
      println(repeat("-", 24));
      source.filter(f -> f.cancelled())
            .collect(groupingBy(Flight::getDate, counting()))
            .entrySet()
            .stream()
            .sorted(comparator)
            .limit(limit)
            .forEach(e -> printf("%-10s       %,3d\n", e.getKey(), e.getValue()));
   }

   public void reportTotalMonthlyFlights(Repository repository) {
      int year = selectYear();
      Stream<Flight> source = repository.getFlightStream(year);
      println("Month\t\tCount");
      println("---------------------------");
      source.filter(f -> f.notCancelled())
            .collect(groupingBy(Flight::getYearMonth, counting()))
            .entrySet()
            .stream()
            .sorted(comparingByKey())
            .forEach(e -> printf("%s\t%,10d\n", 
                                 formatYearMonth(e.getKey()), e.getValue()));
   }

   public void reportTotalDailyFlights(Repository repository) {
      int year = selectYear();
      Stream<Flight> source = repository.getFlightStream(year);
      println("Day\t\t   Count");
      println("---------------------------");
      source.filter(f -> f.notCancelled())
            .collect(groupingBy(Flight::getDate, counting()))
            .entrySet()
            .stream()
            .sorted(comparingByKey())
            .forEach(e -> printf("%s\t%,10d\n", e.getKey(), e.getValue()));
   }

   public void reportTotalFlightsByDayOfWeek(Repository repository) {
      int year = selectYear();
      Stream<Flight> source = repository.getFlightStream(year);
      println("Day of Week\t   Count");
      println("---------------------------");
      source.filter(f -> f.notCancelled())
            .map(f -> f.getDate())
            .collect(groupingBy(LocalDate::getDayOfWeek, counting()))
            .entrySet()
            .stream()
            .sorted(comparingByKey())
            .forEach(e -> printf("%10s\t%,10d\n", e.getKey(), e.getValue()));
   }
}