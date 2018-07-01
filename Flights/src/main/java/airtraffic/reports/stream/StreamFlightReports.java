package airtraffic.reports.stream;

import static java.util.Comparator.comparingInt;
import static java.util.Comparator.reverseOrder;
import static java.util.Map.Entry.comparingByKey;
import static java.util.Map.Entry.comparingByValue;
import static java.util.stream.Collectors.averagingDouble;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import java.sql.ResultSet;
import java.sql.Types;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import org.apache.commons.lang3.tuple.Pair;
import airtraffic.Airport;
import airtraffic.Carrier;
import airtraffic.Flight;
import airtraffic.FlightDistanceRange;
import airtraffic.ReportContext;
import airtraffic.annotations.StreamStyle;
import airtraffic.jdbc.ResultSetBuilder;
import airtraffic.reports.FlightReports;

/**
 * Implementation of flight reports using streams style that was introduced
 * in Java 8.
 *
 * @author Tony Piazza <tony@piazzaconsulting.com>
 */
@StreamStyle
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
   public ResultSet reportTotalFlightsFromOrigin(ReportContext context) {
      final int year = context.getYear();
      final Airport origin = context.getOrigin();
      final ResultSetBuilder builder = 
          new ResultSetBuilder().addColumn("Origin", Types.VARCHAR)
                                .addColumn("TotalFlights", Types.INTEGER);

      long count = context.getRepository()
                          .getFlightStream(year)
                          .parallel()
                          .filter(f -> f.notCancelled() && 
                                       f.getOrigin().equals(origin))
                          .count();

      return builder.addRow(origin.getName().trim(), count).build();
   }

   @Override
   public ResultSet reportTotalFlightsToDestination(ReportContext context) {
      final int year = context.getYear();
      final Airport destination = context.getDestination();
      final ResultSetBuilder builder = 
            new ResultSetBuilder().addColumn("Destination", Types.VARCHAR)
                                  .addColumn("TotalFlights", Types.INTEGER);

      long count = context.getRepository()
                          .getFlightStream(year)
                          .parallel()
                          .filter(f -> f.notCancelled() && 
                                       f.notDiverted() && 
                                       f.getDestination().equals(destination))
                          .count();

      return builder.addRow(destination.getName().trim(), count).build();
   }

   @Override
   public ResultSet reportTotalFlightsFromOriginToDestination(ReportContext context) {
      final int year = context.getYear();
      final Airport origin = context.getOrigin();
      final Airport destination = context.getDestination();
      final ResultSetBuilder builder = 
            new ResultSetBuilder().addColumn("Origin", Types.VARCHAR)
                                  .addColumn("Destination", Types.VARCHAR)
                                  .addColumn("TotalFlights", Types.INTEGER);

      long count = context.getRepository()
                          .getFlightStream(year)
                          .filter(f -> f.notCancelled() && 
                                       f.notDiverted() &&
                                       f.getOrigin().equals(origin) &&
                                       f.getDestination().equals(destination))
                          .count();

      return builder.addRow(origin.getName().trim(), 
                            origin.getIATA(), 
                            destination.getName().trim(), 
                            destination.getIATA(),
                            count)
                    .build();
   }

   @Override
   public ResultSet reportTopFlightsByOrigin(ReportContext context) {
      final int year = context.getYear();
      final int limit = context.getLimit();
      final ResultSetBuilder builder = 
          new ResultSetBuilder().addColumn("Origin", Types.VARCHAR)
                                .addColumn("TotalFlights", Types.INTEGER);

      context.getRepository()
             .getFlightStream(year)
             .filter(f -> f.notCancelled())
             .collect(groupingBy(Flight::getOrigin, counting()))
             .entrySet()
             .stream()
             .sorted(comparingByValue(reverseOrder()))
             .limit(limit)
             .forEachOrdered(e -> builder.addRow(e.getKey().getIATA(), 
                                                 e.getValue()));

      return builder.build();
   }

   @Override
   public ResultSet reportTopDestinationsFromOrigin(ReportContext context) {
      final int year = context.getYear();
      final Airport origin = context.getOrigin();
      final int limit = context.getLimit();
      final ResultSetBuilder builder = 
         new ResultSetBuilder().addColumn("Origin", Types.VARCHAR)
                               .addColumn("TotalFlights", Types.INTEGER);

      context.getRepository()
             .getFlightStream(year)
             .filter(f -> f.notCancelled() && f.getOrigin().equals(origin))
             .collect(groupingBy(Flight::getDestination, counting()))
             .entrySet()
             .stream()
             .sorted(comparingByValue(reverseOrder()))
             .limit(limit)
             .forEachOrdered(e -> 
                builder.addRow(e.getKey().getIATA(), e.getValue())
             );

      return builder.build();
   }

   @Override
   public ResultSet reportMostPopularRoutes(ReportContext context) {
      final int year = context.getYear();
      final int limit = context.getLimit();
      final ResultSetBuilder builder = 
            new ResultSetBuilder().addColumn("Route", Types.VARCHAR)
                                  .addColumn("TotalFlights", Types.INTEGER);

      context.getRepository()
             .getFlightStream(year)
             .collect(groupingBy(Flight::getRoute, counting()))
             .entrySet()
             .stream()
             .sorted(comparingByValue(reverseOrder()))
             .limit(limit)
             .forEachOrdered(entry -> 
                builder.addRow(entry.getKey(), entry.getValue())
             );

      return builder.build();
   }

   @Override
   public ResultSet reportWorstAverageDepartureDelayByOrigin(ReportContext context) {
      final int year = context.getYear();
      final int limit = context.getLimit();
      final ResultSetBuilder builder = 
            new ResultSetBuilder().addColumn("Origin", Types.VARCHAR)
                                  .addColumn("Delay", Types.FLOAT);

      context.getRepository()
             .getFlightStream(year)
             .filter(f -> f.notCancelled())
             .collect(groupingBy(Flight::getOrigin, 
                                 averagingDouble(f -> f.getDepartureDelay())))
             .entrySet()
             .stream()
             .sorted(comparingByValue(reverseOrder()))
             .limit(limit)
             .forEachOrdered(entry -> 
                builder.addRow(entry.getKey().getIATA(), entry.getValue())
             );

      return builder.build();
   }

   @Override
   public ResultSet reportWorstAverageArrivalDelayByDestination(ReportContext context) {
      final int year = context.getYear();
      final int limit = context.getLimit();
      final ResultSetBuilder builder = 
            new ResultSetBuilder().addColumn("Destination", Types.VARCHAR)
                                  .addColumn("Delay", Types.FLOAT);

      context.getRepository()
             .getFlightStream(year)
             .filter(f -> f.notCancelled() && f.notDiverted())
             .collect(groupingBy(Flight::getDestination, 
                                 averagingDouble(f -> f.getArrivalDelay())))
             .entrySet()
             .stream()
             .sorted(comparingByValue(reverseOrder()))
             .limit(limit)
             .forEachOrdered(entry -> 
                builder.addRow(entry.getKey().getIATA(), entry.getValue())
             );

      return builder.build();
   }

   @Override
   public ResultSet reportMostCancelledFlightsByOrigin(ReportContext context) {
      final int year = context.getYear();
      final int limit = context.getLimit();
      final ResultSetBuilder builder = 
            new ResultSetBuilder().addColumn("Origin", Types.VARCHAR)
                                  .addColumn("TotalFlights", Types.INTEGER);

      context.getRepository()
             .getFlightStream(year)
             .filter(f -> f.cancelled())
             .collect(groupingBy(Flight::getOrigin, counting()))
             .entrySet()
             .stream()
             .sorted(comparingByValue(reverseOrder()))
             .limit(limit)
             .forEachOrdered(entry -> 
                builder.addRow(entry.getKey().getIATA(), entry.getValue())
             );

      return builder.build();
   }

   @Override
   public ResultSet reportTotalFlightsByOriginState(ReportContext context) {
      final int year = context.getYear();
      final int limit = context.getLimit();
      final ResultSetBuilder builder = 
            new ResultSetBuilder().addColumn("State", Types.VARCHAR)
                                  .addColumn("TotalFlights", Types.INTEGER);

      context.getRepository()
             .getFlightStream(year)
             .filter(f -> f.notCancelled())
             .map(f -> f.getOrigin())
             .collect(groupingBy(Airport::getState, counting()))
             .entrySet()
             .stream()
             .sorted(comparingByValue(reverseOrder()))
             .limit(limit)
             .forEachOrdered(entry -> 
                builder.addRow(entry.getKey(), entry.getValue())
             );

      return builder.build();
   }

   @Override
   public ResultSet reportTotalFlightsByDestinationState(ReportContext context) {
      final int year = context.getYear();
      final int limit = context.getLimit();
      final ResultSetBuilder builder = 
            new ResultSetBuilder().addColumn("State", Types.VARCHAR)
                                  .addColumn("TotalFlights", Types.INTEGER);

      context.getRepository()
             .getFlightStream(year)
             .parallel()
             .filter(f -> f.notCancelled() && f.notDiverted())
             .map(f -> f.getDestination())
             .collect(groupingBy(Airport::getState, counting()))
             .entrySet()
             .stream()
             .sorted(comparingByValue(reverseOrder()))
             .limit(limit)
             .forEachOrdered(entry -> 
                builder.addRow(entry.getKey(), entry.getValue())
             );

      return builder.build();
   }

   @Override
   public ResultSet reportLongestFlights(ReportContext context) {
      return byDistance(context, comparingInt(Flight::getDistance).reversed());
   }

   @Override
   public ResultSet reportShortestFlights(ReportContext context) {
      return byDistance(context, comparingInt(Flight::getDistance));
   }

   private ResultSet byDistance(ReportContext context, Comparator<Flight> comparator) {
      final int year = context.getYear();
      final int limit = context.getLimit();
      final ResultSetBuilder builder = 
            new ResultSetBuilder().addColumn("FlightNumber", Types.VARCHAR)
                                  .addColumn("Date", Types.DATE)
                                  .addColumn("Carrier", Types.VARCHAR)
                                  .addColumn("Origin", Types.VARCHAR)
                                  .addColumn("Destination", Types.VARCHAR)
                                  .addColumn("Distance", Types.INTEGER);

      context.getRepository()
             .getFlightStream(year)
             .filter(f -> f.notCancelled() && f.notDiverted())
             .sorted(comparator)
             .limit(limit)
             .forEach(flight -> 
                builder.addRow(flight.getFlightNumber(),
                               flight.getDate(),
                               flight.getCarrier().getCode(),
                               flight.getOrigin().getIATA(),
                               flight.getDestination().getIATA(),
                               flight.getDistance())
             );

      return builder.build();
   }

   @Override
   public ResultSet reportTotalFlightsByDistanceRange(ReportContext context) {
      final int year = context.getYear();
      final int limit = context.getLimit();
      final ResultSetBuilder builder = 
            new ResultSetBuilder().addColumn("Range", Types.VARCHAR)
                                  .addColumn("TotalFlights", Types.INTEGER);

      context.getRepository()
             .getFlightStream(year)
             .parallel()
             .filter(f -> f.notCancelled() && f.notDiverted())
             .collect(groupingBy(FlightDistanceRange.classifier(DISTANCE_RANGES),
                                 counting()))
             .entrySet()
             .stream()
             .sorted(comparingByKey())
             .limit(limit)
             .forEach(entry -> 
                builder.addRow(entry.getKey(), entry.getValue())
             );

      return builder.build();
   }

   @Override
   public ResultSet reportDaysWithLeastCancellations(ReportContext context) {
      return byDaysWithCancellations(context, comparingByValue());
   }

   @Override
   public ResultSet reportDaysWithMostCancellations(ReportContext context) {
      return byDaysWithCancellations(context, comparingByValue(reverseOrder()));
   }

   private ResultSet byDaysWithCancellations(ReportContext context, 
      Comparator<Entry<LocalDate, Long>> comparator) {
      final int year = context.getYear();
      final int limit = context.getLimit();
      final ResultSetBuilder builder = 
            new ResultSetBuilder().addColumn("Date", Types.DATE)
                                  .addColumn("TotalCancellations", Types.INTEGER);

      context.getRepository()
             .getFlightStream(year)
             .filter(f -> f.cancelled())
             .collect(groupingBy(Flight::getDate, counting()))
             .entrySet()
             .stream()
             .sorted(comparator)
             .limit(limit)
             .forEach(entry ->
                builder.addRow(entry.getKey(), entry.getValue())
             );

      return builder.build();
   }

   @Override
   public ResultSet reportTotalMonthlyFlights(ReportContext context) {
      final int year = context.getYear();
      final int limit = context.getLimit();
      final ResultSetBuilder builder = 
            new ResultSetBuilder().addColumn("YearMonth", Types.VARCHAR)
                                  .addColumn("TotalFlights", Types.INTEGER);

      context.getRepository()
             .getFlightStream(year)
             .filter(f -> f.notCancelled())
             .collect(groupingBy(Flight::getYearMonth, counting()))
             .entrySet()
             .stream()
             .sorted(comparingByKey())
             .limit(limit)
             .forEach(entry ->
                builder.addRow(YEAR_MONTH_FORMAT.format(entry.getKey()), 
                               entry.getValue())
             );

      return builder.build();
   }

   @Override
   public ResultSet reportTotalDailyFlights(ReportContext context) {
      final int year = context.getYear();
      final int limit = context.getLimit();
      final ResultSetBuilder builder = 
            new ResultSetBuilder().addColumn("Date", Types.VARCHAR)
                                  .addColumn("TotalFlights", Types.INTEGER);

      context.getRepository()
             .getFlightStream(year)
             .filter(f -> f.notCancelled())
             .collect(groupingBy(Flight::getDate, counting()))
             .entrySet()
             .stream()
             .sorted(comparingByKey())
             .limit(limit)
             .forEach(entry -> 
                builder.addRow(entry.getKey(), entry.getValue())
             );

      return builder.build();
   }

   @Override
   public ResultSet reportTotalFlightsByDayOfWeek(ReportContext context) {
      final int year = context.getYear();
      final int limit = context.getLimit();
      final ResultSetBuilder builder = 
            new ResultSetBuilder().addColumn("DayOfWeek", Types.VARCHAR)
                                  .addColumn("TotalFlights", Types.INTEGER);

      context.getRepository()
             .getFlightStream(year)
             .filter(f -> f.notCancelled())
             .map(f -> f.getDate())
             .collect(groupingBy(LocalDate::getDayOfWeek, counting()))
             .entrySet()
             .stream()
             .sorted(comparingByKey())
             .limit(limit)
             .forEach(entry -> 
                builder.addRow(entry.getKey(), entry.getValue())
             );

      return builder.build();
   }

   @Override
   public ResultSet reportMostFlightsByDay(ReportContext context) {
      return byDay(context, comparingByValue(reverseOrder()));
   }

   @Override
   public ResultSet reportLeastFlightsByDay(ReportContext context) {
      return byDay(context, comparingByValue());
   }

   private ResultSet byDay(ReportContext context, 
      Comparator<Entry<LocalDate, Long>> comparator) {
      final int year = context.getYear();
      final int limit = context.getLimit();
      final ResultSetBuilder builder = 
            new ResultSetBuilder().addColumn("Date", Types.VARCHAR)
                                  .addColumn("TotalFlights", Types.INTEGER);

      context.getRepository()
             .getFlightStream(year)
             .filter(f -> f.notCancelled())
             .collect(groupingBy(Flight::getDate, counting()))
             .entrySet()
             .stream()
             .sorted(comparator)
             .limit(limit)
             .forEach(entry ->
                builder.addRow(entry.getKey(), entry.getValue())
             );

      return builder.build();
   }

   @Override
   public ResultSet reportMostFlightsByOriginByDay(ReportContext context) {
      final int year = context.getYear();
      final int limit = context.getLimit();
      final ResultSetBuilder builder = 
            new ResultSetBuilder().addColumn("Origin", Types.VARCHAR)
                                  .addColumn("Date", Types.VARCHAR)
                                  .addColumn("TotalFlights", Types.INTEGER);

      context.getRepository()
             .getFlightStream(year)
             .filter(f -> f.notCancelled())
             .collect(groupingBy(f -> Pair.of(f.getOrigin(), f.getDate()), 
                                 counting()))
             .entrySet()
             .stream()
             .sorted(comparingByValue(reverseOrder()))
             .limit(limit)
             .forEach(entry -> {
                Pair<Airport, LocalDate> key = entry.getKey();
                builder.addRow(key.getLeft().getName(), 
                               key.getRight(), 
                               entry.getValue());
             });

      return builder.build();
   }

   @Override
   public ResultSet reportMostFlightsByCarrierByDay(ReportContext context) {
      final int year = context.getYear();
      final int limit = context.getLimit();
      final ResultSetBuilder builder = 
            new ResultSetBuilder().addColumn("Carrier", Types.VARCHAR)
                                  .addColumn("Date", Types.VARCHAR)
                                  .addColumn("TotalFlights", Types.INTEGER);

      context.getRepository()
             .getFlightStream(year)
             .filter(f -> f.notCancelled())
             .collect(groupingBy(f -> Pair.of(f.getCarrier(), f.getDate()),
                                 counting()))
             .entrySet()
             .stream()
             .sorted(comparingByValue(reverseOrder()))
             .limit(limit)
             .forEach(entry -> {
                Pair<Carrier, LocalDate> key = entry.getKey();
                builder.addRow(key.getLeft().getName(), 
                               key.getRight(), 
                               entry.getValue());
             });

      return builder.build();
   }
}