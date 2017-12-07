package airtraffic.stream;

import static airtraffic.PairGroup.pairAirportDay;
import static airtraffic.PairGroup.pairCarrierDay;
import static java.util.Comparator.comparingInt;
import static java.util.Comparator.reverseOrder;
import static java.util.Map.Entry.comparingByKey;
import static java.util.Map.Entry.comparingByValue;
import static java.util.stream.Collectors.averagingInt;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static org.apache.commons.lang3.StringUtils.left;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;

import airtraffic.Airport;
import airtraffic.Carrier;
import airtraffic.Flight;
import airtraffic.FlightDistanceRange;
import airtraffic.FlightReports;
import airtraffic.PairGroup;
import airtraffic.ReportContext;

/**
 * Generate various flight statistics using Java 8 streams.
 * 
 * @author tony@piazzaconsulting.com
 */
public class StreamFlightReports implements FlightReports {
   private static final List<FlightDistanceRange> DISTANCE_RANGES =
      Arrays.asList(FlightDistanceRange.between(   0,  100), 
                    FlightDistanceRange.between( 101,  250),
                    FlightDistanceRange.between( 251,  500),
                    FlightDistanceRange.between( 501, 1000),
                    FlightDistanceRange.between(1001, 2500),
                    FlightDistanceRange.between(2501, 5000),
                    FlightDistanceRange.between(5001, 9999));

   @Override
   public void reportTotalFlightsFromOrigin(ReportContext context) {
      final int year = context.getYear();
      final Airport origin = context.getOrigin();

      long count = context.getRepository()
                          .getFlightStream(year)
                          .filter(f -> f.notCancelled() && 
                                       f.getOrigin().equals(origin))
                          .count();

      context.getTerminal()
             .printf("Total flights from %s is %,d\n", 
                     origin.getName().trim(), 
                     count);
   }

   @Override
   public void reportTotalFlightsToDestination(ReportContext context) {
      final int year = context.getYear();
      final Airport destination = context.getDestination();

      long count = context.getRepository()
                          .getFlightStream(year)
                          .filter(f -> f.notCancelled() && 
                                       f.notDiverted() && 
                                       f.getDestination().equals(destination))
                          .count();

      context.getTerminal()
             .printf("Total flights to %s is %,d\n", 
                     destination.getName().trim(), 
                     count);
   }

   @Override
   public void reportTotalFlightsFromOriginToDestination(ReportContext context) {
      final int year = context.getYear();
      final Airport origin = context.getOrigin();
      final Airport destination = context.getDestination();

      long count = context.getRepository()
                          .getFlightStream(year)
                          .filter(f -> f.notCancelled() && 
                                       f.notDiverted() &&
                                       f.getOrigin().equals(origin) &&
                                       f.getDestination().equals(destination))
                          .count();

      context.getTerminal()
             .printf("Total of %,d flights from %s (%s)\nto %s (%s)\n", 
                     count,
                     origin.getName().trim(), 
                     origin.getIATA(), 
                     destination.getName().trim(), 
                     destination.getIATA()); 
   }

   @Override
   public void reportTopFlightsByOrigin(ReportContext context) {
      final int year = context.getYear();
      final int limit = context.getLimit();

      context.getRepository()
             .getFlightStream(year)
             .filter(f -> f.notCancelled())
             .collect(groupingBy(Flight::getOrigin, counting()))
             .entrySet()
             .stream()
             .sorted(comparingByValue(reverseOrder()))
             .limit(limit)
             .forEachOrdered(e -> context.getTerminal()
                                         .printf("%3s\t\t%,10d\n", 
                                                 e.getKey().getIATA(), 
                                                 e.getValue()));
   }

   @Override
   public void reportTopDestinationsFromOrigin(ReportContext context) {
      final int year = context.getYear();
      final Airport origin = context.getOrigin();
      final int limit = context.getLimit();

      context.getRepository()
             .getFlightStream(year)
             .filter(f -> f.notCancelled() && f.getOrigin().equals(origin))
             .collect(groupingBy(Flight::getDestination, counting()))
             .entrySet()
             .stream()
             .sorted(comparingByValue(reverseOrder()))
             .limit(limit)
             .forEachOrdered(e -> context.getTerminal()
                                         .printf("%3s\t\t%,10d\n", 
                                                 e.getKey().getIATA(), 
                                                 e.getValue()));
   }

   @Override
   public void reportMostPopularRoutes(ReportContext context) {
      final int year = context.getYear();
      final int limit = context.getLimit();

      context.getRepository()
             .getFlightStream(year)
             .collect(groupingBy(Flight::getRoute, counting()))
             .entrySet()
             .stream()
             .sorted(comparingByValue(reverseOrder()))
             .limit(limit)
             .forEachOrdered(e -> context.getTerminal()
                                         .printf("%s\t%,10d\n", 
                                                 e.getKey(), 
                                                 e.getValue().intValue()));
   }

   @Override
   public void reportWorstAverageDepartureDelayByOrigin(ReportContext context) {
      final int year = context.getYear();
      final int limit = context.getLimit();

      context.getRepository()
             .getFlightStream(year)
             .filter(f -> f.notCancelled())
             .collect(groupingBy(Flight::getOrigin, 
                                 averagingInt(f -> f.getDepartureDelay())))
             .entrySet()
             .stream()
             .sorted(comparingByValue(reverseOrder()))
             .limit(limit)
             .forEachOrdered(e -> context.getTerminal()
                                         .printf("%3s\t\t%.0f\n", 
                                                 e.getKey().getIATA(), 
                                                 e.getValue()));
   }

   @Override
   public void reportWorstAverageArrivalDelayByDestination(ReportContext context) {
      final int year = context.getYear();
      final int limit = context.getLimit();

      context.getRepository()
             .getFlightStream(year)
             .filter(f -> f.notCancelled() && f.notDiverted())
             .collect(groupingBy(Flight::getDestination, 
                                 averagingInt(f -> f.getArrivalDelay())))
             .entrySet()
             .stream()
             .sorted(comparingByValue(reverseOrder()))
             .limit(limit)
             .forEachOrdered(e -> context.getTerminal()
                                         .printf("%3s\t\t\t%.0f\n", 
                                                 e.getKey().getIATA(), 
                                                 e.getValue()));
   }

   @Override
   public void reportMostCancelledFlightsByOrigin(ReportContext context) {
      final int year = context.getYear();
      final int limit = context.getLimit();

      context.getRepository()
             .getFlightStream(year)
             .filter(f -> f.cancelled())
             .collect(groupingBy(Flight::getOrigin, counting()))
             .entrySet()
             .stream()
             .sorted(comparingByValue(reverseOrder()))
             .limit(limit)
             .forEachOrdered(e -> context.getTerminal()
                                         .printf("%3s\t\t%,8d\n", 
                                                 e.getKey().getIATA(), 
                                                 e.getValue()));
   }

   @Override
   public void reportTotalFlightsByOriginState(ReportContext context) {
      final int year = context.getYear();
      final int limit = context.getLimit();

      context.getRepository()
             .getFlightStream(year)
             .filter(f -> f.notCancelled())
             .map(f -> f.getOrigin())
             .collect(groupingBy(Airport::getState, counting()))
             .entrySet()
             .stream()
             .sorted(comparingByValue(reverseOrder()))
             .limit(limit)
             .forEachOrdered(e -> context.getTerminal()
                                         .printf("%2s\t%,10d\n", 
                                                 e.getKey(), 
                                                 e.getValue()));
   }

   @Override
   public void reportTotalFlightsByDestinationState(ReportContext context) {
      final int year = context.getYear();
      final int limit = context.getLimit();

      context.getRepository().getFlightStream(year)
                .filter(f -> f.notCancelled() && f.notDiverted())
                .map(f -> f.getDestination())
                .collect(groupingBy(Airport::getState, counting()))
                .entrySet()
                .stream()
                .sorted(comparingByValue(reverseOrder()))
                .limit(limit)
                .forEachOrdered(e -> context.getTerminal()
                                            .printf("%2s\t%,10d\n", 
                                                    e.getKey(), 
                                                    e.getValue()));
   }

   @Override
   public void reportLongestFlights(ReportContext context) {
      byDistance(context, comparingInt(Flight::getDistance).reversed());
   }

   @Override
   public void reportShortestFlights(ReportContext context) {
      byDistance(context, comparingInt(Flight::getDistance));
   }

   private void byDistance(ReportContext context, Comparator<Flight> comparator) {
      final int year = context.getYear();
      final int limit = context.getLimit();

      context.getRepository().getFlightStream(year)
                .filter(f -> f.notCancelled() && f.notDiverted())
                .sorted(comparator)
                .limit(limit)
                .forEach(f -> context.getTerminal()
                                     .printf("%-8s  %10s\t  %2s\t %3s\t    %3s\t\t%6d\n",
                                             f.getFlightNumber(),
                                             f.getDate(),
                                             f.getCarrier().getCode(),
                                             f.getOrigin().getIATA(),
                                             f.getDestination().getIATA(),
                                             f.getDistance()));
   }

   @Override
   public void reportTotalFlightsByDistanceRange(ReportContext context) {
      final int year = context.getYear();

      context.getRepository().getFlightStream(year)
                .filter(f -> f.notCancelled() && f.notDiverted())
                .collect(groupingBy(FlightDistanceRange.classifier(DISTANCE_RANGES),
                                    counting()))
                .entrySet()
                .stream()
                .sorted(comparingByKey())
                .forEach(e -> context.getTerminal()
                                     .printf("%-10s\t%,10d\n", 
                                             e.getKey(), 
                                             e.getValue()));
   }

   @Override
   public void reportDaysWithLeastCancellations(ReportContext context) {
      byDaysWithCancellations(context, comparingByValue());
   }

   @Override
   public void reportDaysWithMostCancellations(ReportContext context) {
      byDaysWithCancellations(context, comparingByValue(reverseOrder()));
   }

   private void byDaysWithCancellations(ReportContext context, 
      Comparator<Entry<LocalDate, Long>> comparator) {
      final int year = context.getYear();
      final int limit = context.getLimit();

      context.getRepository().getFlightStream(year)
                .filter(f -> f.cancelled())
                .collect(groupingBy(Flight::getDate, counting()))
                .entrySet()
                .stream()
                .sorted(comparator)
                .limit(limit)
                .forEach(e -> context.getTerminal()
                                     .printf("%-10s       %,3d\n", 
                                             e.getKey(), 
                                             e.getValue()));
   }

   @Override
   public void reportTotalMonthlyFlights(ReportContext context) {
      final int year = context.getYear();

      context.getRepository().getFlightStream(year)
                .filter(f -> f.notCancelled())
                .collect(groupingBy(Flight::getYearMonth, counting()))
                .entrySet()
                .stream()
                .sorted(comparingByKey())
                .forEach(e -> context.getTerminal()
                                     .printf("%s\t%,10d\n", 
                                            YEAR_MONTH_FORMAT.format(e.getKey()), 
                                             e.getValue()));
   }

   @Override
   public void reportTotalDailyFlights(ReportContext context) {
      final int year = context.getYear();

      context.getRepository().getFlightStream(year)
                .filter(f -> f.notCancelled())
                .collect(groupingBy(Flight::getDate, counting()))
                .entrySet()
                .stream()
                .sorted(comparingByKey())
                .forEach(e -> context.getTerminal()
                                     .printf("%s\t%,10d\n", 
                                             e.getKey(), 
                                             e.getValue()));
   }

   @Override
   public void reportTotalFlightsByDayOfWeek(ReportContext context) {
      final int year = context.getYear();

      context.getRepository().getFlightStream(year)
                .filter(f -> f.notCancelled())
                .map(f -> f.getDate())
                .collect(groupingBy(LocalDate::getDayOfWeek, counting()))
                .entrySet()
                .stream()
                .sorted(comparingByKey())
                .forEach(e -> context.getTerminal()
                                     .printf("%10s\t%,10d\n", 
                                             e.getKey(), 
                                             e.getValue()));
   }

   @Override
   public void reportMostFlightsByDay(ReportContext context) {
      byDay(context, comparingByValue(reverseOrder()));
   }

   @Override
   public void reportLeastFlightsByDay(ReportContext context) {
      byDay(context, comparingByValue());
   }

   private void byDay(ReportContext context, 
      Comparator<Entry<LocalDate, Long>> comparator) {
      final int year = context.getYear();
      final int limit = context.getLimit();

      context.getRepository().getFlightStream(year)
                .filter(f -> f.notCancelled())
                .collect(groupingBy(Flight::getDate, counting()))
                .entrySet()
                .stream()
                .sorted(comparator)
                .limit(limit)
                .forEach(e -> context.getTerminal()
                                     .printf("%s\t%,10d\n", 
                                             e.getKey(), 
                                             e.getValue()));
   }

   @Override
   public void reportMostFlightsByOriginByDay(ReportContext context) {
      final int year = context.getYear();
      final int limit = context.getLimit();

      context.getRepository()
             .getFlightStream(year)
             .filter(f -> f.notCancelled())
             .collect(groupingBy(f -> pairAirportDay(f.getOrigin(), 
                                                     f.getDate()), 
                                 counting()))
             .entrySet()
             .stream()
             .sorted(comparingByValue(reverseOrder()))
             .limit(limit)
             .forEach(entry -> {
                PairGroup<Airport, LocalDate> key = entry.getKey();
                context.getTerminal()
                       .printf("%-30s\t%s\t%,10d\n", 
                               left(key.getFirst().getName(), 30), 
                               key.getSecond(), 
                               entry.getValue());
             });
   }

   @Override
   public void reportMostFlightsByCarrierByDay(ReportContext context) {
      final int year = context.getYear();
      final int limit = context.getLimit();

      context.getRepository()
             .getFlightStream(year)
             .filter(f -> f.notCancelled())
             .collect(groupingBy(f -> pairCarrierDay(f.getCarrier(), 
                                                     f.getDate()),
                                 counting()))
             .entrySet()
             .stream()
             .sorted(comparingByValue(reverseOrder()))
             .limit(limit)
             .forEach(entry -> {
                PairGroup<Carrier, LocalDate> key = entry.getKey();
                context.getTerminal()
                       .printf("%-30s\t%s\t%,10d\n", 
                               left(key.getFirst().getName(), 30), 
                               key.getSecond(), 
                               entry.getValue());
             });
   }
}