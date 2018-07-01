package airtraffic.reports.iterator;

import static airtraffic.reports.iterator.AccumulatorHelper.accumulate;
import static java.util.Comparator.reverseOrder;
import static java.util.Map.Entry.comparingByKey;
import static java.util.Map.Entry.comparingByValue;
import java.sql.ResultSet;
import java.sql.Types;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.chrono.ChronoLocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import airtraffic.Airport;
import airtraffic.Carrier;
import airtraffic.Flight;
import airtraffic.FlightDistanceRange;
import airtraffic.PairGroup;
import airtraffic.ReportContext;
import airtraffic.Route;
import airtraffic.annotations.IteratorStyle;
import airtraffic.jdbc.ResultSetBuilder;
import airtraffic.reports.FlightReports;

/**
 * Generate various flight statistics using Java iterators.
 *
 * @author tony@piazzaconsulting.com
 */
@IteratorStyle
public class IteratorFlightReports implements FlightReports {
   private static final Comparator<Flight> FLIGHT_DISTANCE_COMPARATOR = 
      new Comparator<Flight>() {
         @Override public int compare(Flight f1, Flight f2) {
            return f1.getDistance() - f2.getDistance();
         }
      };
   private static final List<FlightDistanceRange> DISTANCE_RANGES =
      Arrays.asList(FlightDistanceRange.between(   0,  100), 
                    FlightDistanceRange.between( 101,  250),
                    FlightDistanceRange.between( 251,  500),
                    FlightDistanceRange.between( 501, 1000),
                    FlightDistanceRange.between(1001, 2500),
                    FlightDistanceRange.between(2501, 5000),
                    FlightDistanceRange.between(5001, 9999));

   public ResultSet reportTotalFlightsFromOrigin(ReportContext context) {
      final int year = context.getYear();
      final Airport origin = context.getOrigin();
      final ResultSetBuilder builder = 
          new ResultSetBuilder().addColumn("Origin", Types.VARCHAR)
                                .addColumn("TotalFlights", Types.INTEGER);

      long count = 0;
      Iterator<Flight> iterator = context.getRepository().getFlightIterator(year);
      while(iterator.hasNext()) {
         Flight flight = iterator.next();
         if(flight.notCancelled() && flight.getOrigin().equals(origin)) {
            ++count;
         }
      }

      return builder.addRow(origin.getName().trim(), count).build();
   }

   public ResultSet reportTotalFlightsToDestination(ReportContext context) {
      final int year = context.getYear();
      final Airport destination = context.getDestination();
      final ResultSetBuilder builder = 
          new ResultSetBuilder().addColumn("Destination", Types.VARCHAR)
                                .addColumn("TotalFlights", Types.INTEGER);

      long count = 0;
      Iterator<Flight> iterator = context.getRepository().getFlightIterator(year);
      while(iterator.hasNext()) {
         Flight flight = iterator.next();
         if(flight.notCancelled() && flight.notDiverted() &&  
            flight.getDestination().equals(destination)) {
            ++count;
         }
      }

      return builder.addRow(destination.getName().trim(), count).build();
   }

   public ResultSet reportTotalFlightsFromOriginToDestination(ReportContext context) {
      final int year = context.getYear();
      final Airport origin = context.getOrigin();
      final Airport destination = context.getDestination();
      final ResultSetBuilder builder = 
            new ResultSetBuilder().addColumn("Origin", Types.VARCHAR)
                                  .addColumn("Destination", Types.VARCHAR)
                                  .addColumn("TotalFlights", Types.INTEGER);

      long count = 0;
      Iterator<Flight> iterator = context.getRepository().getFlightIterator(year);
      while(iterator.hasNext()) {
         Flight flight = iterator.next();
         if(flight.notCancelled() && flight.notDiverted() &&  
            flight.getOrigin().equals(origin) &&
            flight.getDestination().equals(destination)) {
            ++count;
         }
      }

      return builder.addRow(origin.getName().trim(), 
                            origin.getIATA(), 
                            destination.getName().trim(), 
                            destination.getIATA(),
                            count)
                    .build();
   }

   public ResultSet reportTopFlightsByOrigin(ReportContext context) {
      final int year = context.getYear();
      final int limit = context.getLimit();
      final ResultSetBuilder builder = 
          new ResultSetBuilder().addColumn("Origin", Types.VARCHAR)
                                .addColumn("TotalFlights", Types.INTEGER);

      Iterator<Flight> iterator = context.getRepository().getFlightIterator(year);
      accumulate(iterator, comparingByValue(reverseOrder()), limit, 
         new CountingAccumulator<Flight, Airport>() {
            @Override public boolean filter(Flight source) {
               return source.notCancelled();
            }
            @Override public Airport getKey(Flight source) {
               return source.getOrigin();
            }
            @Override public void forEach(Entry<Airport, Long> entry) {
              builder.addRow(entry.getKey().getIATA(), entry.getValue());
            }
         }
      );

      return builder.build();
   }

   public ResultSet reportTopDestinationsFromOrigin(ReportContext context) {
      final int year = context.getYear();
      final Airport origin = context.getOrigin();
      final int limit = context.getLimit();
      final ResultSetBuilder builder = 
            new ResultSetBuilder().addColumn("Origin", Types.VARCHAR)
                                  .addColumn("TotalFlights", Types.INTEGER);

      Iterator<Flight> iterator = context.getRepository().getFlightIterator(year);
      accumulate(iterator, comparingByValue(reverseOrder()), limit, 
         new CountingAccumulator<Flight, Airport>() {
            @Override public boolean filter(Flight source) {
               return source.notCancelled() && 
                      source.getOrigin().equals(origin);
            }
            @Override public Airport getKey(Flight source) {
               return source.getDestination();
            }
            @Override public void forEach(Entry<Airport, Long> entry) {
               builder.addRow(entry.getKey().getIATA(), entry.getValue());
            }
         }
      );

      return builder.build();
   }

   public ResultSet reportMostPopularRoutes(ReportContext context) {
      final int year = context.getYear();
      final int limit = context.getLimit();
      final ResultSetBuilder builder = 
            new ResultSetBuilder().addColumn("Route", Types.VARCHAR)
                                  .addColumn("TotalFlights", Types.INTEGER);

      Iterator<Flight> iterator = context.getRepository().getFlightIterator(year);
      accumulate(iterator, comparingByValue(reverseOrder()), limit, 
         new CountingAccumulator<Flight, Route>() {
            @Override public boolean filter(Flight source) {
               return true;
            }
            @Override public Route getKey(Flight source) {
               return source.getRoute();
            }
            @Override public void forEach(Entry<Route, Long> entry) {
               builder.addRow(entry.getKey(), entry.getValue());
            }
         }
      );

      return builder.build();
   }

   public ResultSet reportWorstAverageDepartureDelayByOrigin(ReportContext context) {
      final int year = context.getYear();
      final int limit = context.getLimit();
      final ResultSetBuilder builder = 
            new ResultSetBuilder().addColumn("Origin", Types.VARCHAR)
                                  .addColumn("Delay", Types.FLOAT);

      Iterator<Flight> iterator = context.getRepository().getFlightIterator(year);
      accumulate(iterator, comparingByValue(reverseOrder()), limit, 
         new MapAccumulator<Flight, Airport, AverageValue>() {
            @Override public boolean filter(Flight source) {
               return source.notCancelled();
            }
            @Override public Airport getKey(Flight source) {
               return source.getOrigin();
            }
            @Override public AverageValue initializeValue(Flight source) {
               return new AverageValue(source.getDepartureDelay());
            }
            @Override public AverageValue updateValue(Flight source, AverageValue value) {
               return value.add(source.getDepartureDelay());
            }
            @Override public void forEach(Entry<Airport, AverageValue> entry) {
               builder.addRow(entry.getKey().getIATA(), 
                              entry.getValue().getAverage());
            }
         }
      );

      return builder.build();
   }

   public ResultSet reportWorstAverageArrivalDelayByDestination(ReportContext context) {
      final int year = context.getYear();
      final int limit = context.getLimit();
      final ResultSetBuilder builder = 
            new ResultSetBuilder().addColumn("Destination", Types.VARCHAR)
                                  .addColumn("Delay", Types.FLOAT);

      Iterator<Flight> iterator = context.getRepository().getFlightIterator(year);
      accumulate(iterator, comparingByValue(reverseOrder()), limit, 
         new MapAccumulator<Flight, Airport, AverageValue>() {
            @Override public boolean filter(Flight source) {
               return source.notCancelled();
            }
            @Override public Airport getKey(Flight source) {
               return source.getDestination();
            }
            @Override public AverageValue initializeValue(Flight source) {
               return new AverageValue(source.getArrivalDelay());
            }
            @Override public AverageValue updateValue(Flight source, AverageValue value) {
               return value.add(source.getArrivalDelay());
            }
            @Override public void forEach(Entry<Airport, AverageValue> entry) {
               builder.addRow(entry.getKey().getIATA(), 
                              entry.getValue().getAverage());
            }
         }
      );

      return builder.build();
   }

   public ResultSet reportMostCancelledFlightsByOrigin(ReportContext context) {
      final int year = context.getYear();
      final int limit = context.getLimit();
      final ResultSetBuilder builder = 
            new ResultSetBuilder().addColumn("Origin", Types.VARCHAR)
                                  .addColumn("TotalFlights", Types.INTEGER);

      Iterator<Flight> iterator = context.getRepository().getFlightIterator(year);
      accumulate(iterator, comparingByValue(reverseOrder()), limit, 
         new CountingAccumulator<Flight, Airport>() {
            @Override public boolean filter(Flight source) {
               return source.cancelled();
            }
            @Override public Airport getKey(Flight source) {
               return source.getOrigin();
            }
            @Override public void forEach(Entry<Airport, Long> entry) {
               builder.addRow(entry.getKey().getIATA(), entry.getValue());
            }
         }
      );

      return builder.build();
   }

   public ResultSet reportTotalFlightsByOriginState(ReportContext context) {
      final int year = context.getYear();
      final int limit = context.getLimit();
      final ResultSetBuilder builder = 
            new ResultSetBuilder().addColumn("State", Types.VARCHAR)
                                  .addColumn("TotalFlights", Types.INTEGER);

      Iterator<Flight> iterator = context.getRepository().getFlightIterator(year);
      accumulate(iterator, comparingByValue(reverseOrder()), limit, 
         new CountingAccumulator<Flight, String>() {
            @Override public boolean filter(Flight source) {
               return source.notCancelled();
            }
            @Override public String getKey(Flight source) {
               return source.getOrigin().getState();
            }
            @Override public void forEach(Entry<String, Long> entry) {
               builder.addRow(entry.getKey(), entry.getValue());
            }
         }
      );

      return builder.build();
   }

   public ResultSet reportTotalFlightsByDestinationState(ReportContext context) {
      final int year = context.getYear();
      final int limit = context.getLimit();
      final ResultSetBuilder builder = 
            new ResultSetBuilder().addColumn("State", Types.VARCHAR)
                                  .addColumn("TotalFlights", Types.INTEGER);

      Iterator<Flight> iterator = context.getRepository().getFlightIterator(year);
      accumulate(iterator, comparingByValue(reverseOrder()), limit, 
         new CountingAccumulator<Flight, String>() {
            @Override public boolean filter(Flight source) {
               return source.notCancelled();
            }
            @Override public String getKey(Flight source) {
               return source.getDestination().getState();
            }
            @Override public void forEach(Entry<String, Long> entry) {
               builder.addRow(entry.getKey(), entry.getValue());
            }
         }
      );

      return builder.build();
   }

   public ResultSet reportLongestFlights(ReportContext context) {
      return byDistance(context, FLIGHT_DISTANCE_COMPARATOR.reversed());
   }

   public ResultSet reportShortestFlights(ReportContext context) {
      return byDistance(context, FLIGHT_DISTANCE_COMPARATOR);
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

      Iterator<Flight> iterator = context.getRepository().getFlightIterator(year);
      List<Flight> flights = new ArrayList<>();
      while(iterator.hasNext()) {
         Flight flight = iterator.next();
         if(flight.notCancelled() && flight.notDiverted()) {
            flights.add(flight);
         }
      }
      Collections.sort(flights, comparator);
      int count = 0;
      for(Flight flight : flights) {
         builder.addRow(flight.getFlightNumber(),
                        flight.getDate(),
                        flight.getCarrier().getCode(),
                        flight.getOrigin().getIATA(),
                        flight.getDestination().getIATA(),
                        flight.getDistance());
         if(++count >= limit) {
            break;
         }         
      }

      return builder.build();
   }

   public ResultSet reportTotalFlightsByDistanceRange(ReportContext context) {
      final int year = context.getYear();
      final int limit = context.getLimit();
      final ResultSetBuilder builder = 
            new ResultSetBuilder().addColumn("Range", Types.VARCHAR)
                                  .addColumn("TotalFlights", Types.INTEGER);

      Iterator<Flight> iterator = context.getRepository().getFlightIterator(year);
      accumulate(iterator, comparingByKey(), limit, 
         new MapAccumulator<Flight, FlightDistanceRange, Long>() {
            @Override public boolean filter(Flight source) {
               return source.notCancelled() && source.notDiverted();
            }
            @Override public FlightDistanceRange getKey(Flight source) {
               int distance = source.getDistance();
               for(FlightDistanceRange range : DISTANCE_RANGES) {
                  if(range.contains(distance)) {
                     return range;
                  }
               }
               throw new IllegalStateException("No range for distance of " + distance);
            }
            @Override public Long initializeValue(Flight source) {
               return Long.valueOf(1);
            }
            @Override public Long updateValue(Flight source, Long value) {
               return Long.valueOf(value.longValue() + 1);
            }
            @Override public void forEach(Entry<FlightDistanceRange, Long> entry) {
               builder.addRow(entry.getKey(), entry.getValue());
            }
         }
      );

      return builder.build();
   }

   public ResultSet reportDaysWithLeastCancellations(ReportContext context) {
      return byDaysWithCancellations(context, comparingByValue());
   }

   public ResultSet reportDaysWithMostCancellations(ReportContext context) {
      return byDaysWithCancellations(context, comparingByValue(reverseOrder()));
   }

   private ResultSet byDaysWithCancellations(ReportContext context, 
      Comparator<Entry<ChronoLocalDate, Long>> comparator) {
      final int year = context.getYear();
      final int limit = context.getLimit();
      final ResultSetBuilder builder = 
            new ResultSetBuilder().addColumn("Date", Types.DATE)
                                  .addColumn("TotalCancellations", Types.INTEGER);

      Iterator<Flight> iterator = context.getRepository().getFlightIterator(year);
      accumulate(iterator, comparator, limit, 
         new CountingAccumulator<Flight, ChronoLocalDate>() {
            @Override public boolean filter(Flight source) {
               return source.cancelled();
            }
            @Override public ChronoLocalDate getKey(Flight source) {
               return source.getDate();
            }
            @Override public void forEach(Entry<ChronoLocalDate, Long> entry) {
               builder.addRow(entry.getKey(), entry.getValue());
            }
         }
      );

      return builder.build();
   }

   public ResultSet reportTotalMonthlyFlights(ReportContext context) {
      final int year = context.getYear();
      final int limit = context.getLimit();
      final ResultSetBuilder builder = 
            new ResultSetBuilder().addColumn("YearMonth", Types.VARCHAR)
                                  .addColumn("TotalFlights", Types.INTEGER);

      Iterator<Flight> iterator = context.getRepository().getFlightIterator(year);
      accumulate(iterator, comparingByKey(), limit, 
         new CountingAccumulator<Flight, YearMonth>() {
            @Override public boolean filter(Flight source) {
               return source.notCancelled();
            }
            @Override public YearMonth getKey(Flight source) {
               return source.getYearMonth();
            }
            @Override public void forEach(Entry<YearMonth, Long> entry) {
               builder.addRow(YEAR_MONTH_FORMAT.format(entry.getKey()), 
                              entry.getValue());
            }
         }
      );

      return builder.build();
   }

   public ResultSet reportTotalDailyFlights(ReportContext context) {
      final int year = context.getYear();
      final int limit = context.getLimit();
      final ResultSetBuilder builder = 
            new ResultSetBuilder().addColumn("Date", Types.VARCHAR)
                                  .addColumn("TotalFlights", Types.INTEGER);

      Iterator<Flight> iterator = context.getRepository().getFlightIterator(year);
      accumulate(iterator, comparingByKey(), limit, 
         new CountingAccumulator<Flight, ChronoLocalDate>() {
            @Override public boolean filter(Flight source) {
               return source.notCancelled();
            }
            @Override public ChronoLocalDate getKey(Flight source) {
               return source.getDate();
            }
            @Override public void forEach(Entry<ChronoLocalDate, Long> entry) {
               builder.addRow(entry.getKey(), entry.getValue());
            }
         }
      );

      return builder.build();
   }

   public ResultSet reportTotalFlightsByDayOfWeek(ReportContext context) {
      final int year = context.getYear();
      final int limit = context.getLimit();
      final ResultSetBuilder builder = 
            new ResultSetBuilder().addColumn("DayOfWeek", Types.VARCHAR)
                                  .addColumn("TotalFlights", Types.INTEGER);

      Iterator<Flight> iterator = context.getRepository().getFlightIterator(year);
      accumulate(iterator, comparingByKey(), limit, 
         new CountingAccumulator<Flight, DayOfWeek>() {
            @Override public boolean filter(Flight source) {
               return source.notCancelled();
            }
            @Override public DayOfWeek getKey(Flight source) {
               return source.getDate().getDayOfWeek();
            }
            @Override public void forEach(Entry<DayOfWeek, Long> entry) {
               builder.addRow(entry.getKey(), entry.getValue());
            }
         }
      );

      return builder.build();
   }

   public ResultSet reportMostFlightsByDay(ReportContext context) {
      return byDay(context, comparingByValue(reverseOrder()));
   }

   public ResultSet reportLeastFlightsByDay(ReportContext context) {
      return byDay(context, comparingByValue(reverseOrder()));
   }

   private ResultSet byDay(ReportContext context, 
      Comparator<Entry<ChronoLocalDate, Long>> comparator) {
      final int year = context.getYear();
      final int limit = context.getLimit();
      final ResultSetBuilder builder = 
            new ResultSetBuilder().addColumn("Date", Types.VARCHAR)
                                  .addColumn("TotalFlights", Types.INTEGER);

      Iterator<Flight> iterator = context.getRepository().getFlightIterator(year);
      accumulate(iterator, comparator, limit, 
         new CountingAccumulator<Flight, ChronoLocalDate>() {
            @Override public boolean filter(Flight source) {
               return source.notCancelled();
            }
            @Override public ChronoLocalDate getKey(Flight source) {
               return source.getDate();
            }
            @Override public void forEach(Entry<ChronoLocalDate, Long> entry) {
               builder.addRow(entry.getKey(), entry.getValue());
            }
         }
      );

      return builder.build();
   }

   public ResultSet reportMostFlightsByOriginByDay(ReportContext context) {
      final int year = context.getYear();
      final int limit = context.getLimit();
      final ResultSetBuilder builder = 
            new ResultSetBuilder().addColumn("Origin", Types.VARCHAR)
                                  .addColumn("Date", Types.VARCHAR)
                                  .addColumn("TotalFlights", Types.INTEGER);

      Iterator<Flight> iterator = context.getRepository().getFlightIterator(year);
      accumulate(iterator, comparingByValue(reverseOrder()), limit, 
         new CountingAccumulator<Flight, PairGroup<Airport, LocalDate>>() {
            @Override public boolean filter(Flight flight) {
               return flight.notCancelled();
            }
            @Override public PairGroup<Airport, LocalDate> getKey(Flight flight) {
               return new PairGroup<Airport, LocalDate>(flight.getOrigin(), 
                                                        flight.getDate());
            }
            @Override public void forEach(Entry<PairGroup<Airport, LocalDate>, Long> entry) {
               PairGroup<Airport, LocalDate> key = entry.getKey();
               builder.addRow(key.getFirst().getName(), 
                              key.getSecond(), 
                              entry.getValue());
            }
         }
      );

      return builder.build();
   }

   public ResultSet reportMostFlightsByCarrierByDay(ReportContext context) {
      final int year = context.getYear();
      final int limit = context.getLimit();
      final ResultSetBuilder builder = 
            new ResultSetBuilder().addColumn("Carrier", Types.VARCHAR)
                                  .addColumn("Date", Types.VARCHAR)
                                  .addColumn("TotalFlights", Types.INTEGER);

      Iterator<Flight> iterator = context.getRepository().getFlightIterator(year);
      accumulate(iterator, comparingByValue(reverseOrder()), limit, 
         new CountingAccumulator<Flight, PairGroup<Carrier, LocalDate>>() {
            @Override public boolean filter(Flight flight) {
               return flight.notCancelled();
            }
            @Override public PairGroup<Carrier, LocalDate> getKey(Flight flight) {
               return new PairGroup<Carrier, LocalDate>(flight.getCarrier(), 
                                                        flight.getDate());
            }
            @Override public void forEach(Entry<PairGroup<Carrier, LocalDate>, Long> entry) {
               PairGroup<Carrier, LocalDate> key = entry.getKey();
               builder.addRow(key.getFirst().getName(), 
                              key.getSecond(), 
                              entry.getValue());
            }
         }
      );

      return builder.build();
   }
}