package airtraffic.iterator;

import static airtraffic.iterator.AccumulatorHelper.accumulate;
import static java.util.Comparator.reverseOrder;
import static java.util.Map.Entry.comparingByKey;
import static java.util.Map.Entry.comparingByValue;

import java.time.DayOfWeek;
import java.time.YearMonth;
import java.time.chrono.ChronoLocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import airtraffic.AbstractReportsApp;
import airtraffic.Airport;
import airtraffic.Flight;
import airtraffic.FlightDistanceRange;
import airtraffic.Repository;
import airtraffic.Route;

/**
 * Generate various flight statistics using Java iterators.
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
                    FlightDistanceRange.between(2501, 5000),
                    FlightDistanceRange.between(5001, 9999));

   public static void main(String[] args) throws Exception {
      new FlightReportsApp().executeSelectedReport();
   }

   public void reportTotalFlightsFromOrigin(Repository repository) {
      final int year = selectYear();
      final Airport origin = readAirport("Origin");

      long count = 0;
      Iterator<Flight> iterator = repository.getFlightIterator(year);
      while(iterator.hasNext()) {
         Flight flight = iterator.next();
         if(flight.notCancelled() && flight.getOrigin().equals(origin)) {
            ++count;
         }
      }

      printf("Total flights from %s is %,d\n", origin.getName().trim(), count);
   }

   public void reportTotalFlightsToDestination(Repository repository) {
      final int year = selectYear();
      final Airport destination = readAirport("Destination");

      long count = 0;
      Iterator<Flight> iterator = repository.getFlightIterator(year);
      while(iterator.hasNext()) {
         Flight flight = iterator.next();
         if(flight.notCancelled() && flight.notDiverted() &&  
            flight.getDestination().equals(destination)) {
            ++count;
         }
      }

      printf("Total flights to %s is %,d\n", destination.getName().trim(), count);
   }

   public void reportTotalFlightsFromOriginToDestination(Repository repository) {
      final int year = selectYear();
      final Airport origin = readAirport("Origin");
      final Airport destination = readAirport("Destination");

      long count = 0;
      Iterator<Flight> iterator = repository.getFlightIterator(year);
      while(iterator.hasNext()) {
         Flight flight = iterator.next();
         if(flight.notCancelled() && flight.notDiverted() &&  
            flight.getOrigin().equals(origin) &&
            flight.getDestination().equals(destination)) {
            ++count;
         }
      }

      printf("Total of %,d flights from %s (%s)\nto %s (%s)\n", 
             count,
             origin.getName().trim(), 
             origin.getIATA(), 
             destination.getName().trim(), 
             destination.getIATA()
      ); 
   }

   public void reportTopFlightsByOrigin(Repository repository) {
      final int year = selectYear();
      final int limit = readLimit(10, 1, 100);

      println("\nOrigin\t\tCount");
      println(repeat("-", 27));

      Iterator<Flight> iterator = repository.getFlightIterator(year);
      accumulate(iterator, comparingByValue(reverseOrder()), limit, 
         new CountingAccumulator<Flight, Airport>() {
            @Override public boolean filter(Flight source) {
               return source.notCancelled();
            }
            @Override public Airport getKey(Flight source) {
               return source.getOrigin();
            }
            @Override public void forEach(Entry<Airport, Long> entry) {
               printf("%3s\t\t%,10d\n", 
                      entry.getKey().getIATA(), 
                      entry.getValue());
            }
         }
      );
   }

   public void reportTopDestinationsFromOrigin(Repository repository) {
      final int year = selectYear();
      final Airport origin = readAirport("Origin");
      final int limit = readLimit(10, 1, 100);

      printf("Top destinations from %s\n\n", origin.getName());
      println("Destination\t   Count");
      println(repeat("-", 30));

      Iterator<Flight> iterator = repository.getFlightIterator(year);
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
               printf("%3s\t\t%,10d\n", 
                      entry.getKey().getIATA(), 
                      entry.getValue());
            }
         }
      );
   }

   public void reportMostPopularRoutes(Repository repository) {
      final int year = selectYear();
      final int limit = readLimit(10, 1, 100);

      println("Route\t\t    Count");
      println(repeat("-", 27));

      Iterator<Flight> iterator = repository.getFlightIterator(year);
      accumulate(iterator, comparingByValue(reverseOrder()), limit, 
         new CountingAccumulator<Flight, Route>() {
            @Override public boolean filter(Flight source) {
               return true;
            }
            @Override public Route getKey(Flight source) {
               return source.getRoute();
            }
            @Override public void forEach(Entry<Route, Long> entry) {
               printf("%s\t%,10d\n", 
                      entry.getKey(), 
                      entry.getValue().intValue());
            }
         }
      );
   }

   public void reportWorstAverageDepartureDelayByOrigin(Repository repository) {
      final int year = selectYear();
      final int limit = readLimit(10, 1, 100);

      println("Origin\tDelay (min)");
      println(repeat("-", 22));

      Iterator<Flight> iterator = repository.getFlightIterator(year);
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
               printf("%3s\t\t%.0f\n", 
                      entry.getKey().getIATA(), 
                      entry.getValue().getAverage());
            }
         }
      );
   }

   public void reportWorstAverageArrivalDelayByDestination(Repository repository) {
      final int year = selectYear();
      final int limit = readLimit(10, 1, 100);

      println("Destination\tDelay (min)");
      println(repeat("-", 28));

      Iterator<Flight> iterator = repository.getFlightIterator(year);
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
               printf("%3s\t\t%.0f\n", 
                      entry.getKey().getIATA(), 
                      entry.getValue().getAverage());
            }
         }
      );
   }

   public void reportMostCancelledFlightsByOrigin(Repository repository) {
      final int year = selectYear();
      final int limit = readLimit(10, 1, 100);

      println("Origin\t\t  Count");
      println(repeat("-", 27));

      Iterator<Flight> iterator = repository.getFlightIterator(year);
      accumulate(iterator, comparingByValue(reverseOrder()), limit, 
         new CountingAccumulator<Flight, Airport>() {
            @Override public boolean filter(Flight source) {
               return source.cancelled();
            }
            @Override public Airport getKey(Flight source) {
               return source.getOrigin();
            }
            @Override public void forEach(Entry<Airport, Long> entry) {
               printf("%3s\t\t%,8d\n", 
                      entry.getKey().getIATA(), 
                      entry.getValue());
            }
         }
      );
   }

   public void reportTotalFlightsByOriginState(Repository repository) {
      final int year = selectYear();
      final int limit = readLimit(10, 1, 100);

      println("State\t  Count");
      println(repeat("-", 19));

      Iterator<Flight> iterator = repository.getFlightIterator(year);
      accumulate(iterator, comparingByValue(reverseOrder()), limit, 
         new CountingAccumulator<Flight, String>() {
            @Override public boolean filter(Flight source) {
               return source.notCancelled();
            }
            @Override public String getKey(Flight source) {
               return source.getOrigin().getState();
            }
            @Override public void forEach(Entry<String, Long> entry) {
               printf("%2s\t%,10d\n", 
                      entry.getKey(), 
                      entry.getValue());
            }
         }
      );
   }

   public void reportTotalFlightsByDestinationState(Repository repository) {
      final int year = selectYear();
      final int limit = readLimit(10, 1, 100);

      println("State\tCount");
      println(repeat("-", 19));

      Iterator<Flight> iterator = repository.getFlightIterator(year);
      accumulate(iterator, comparingByValue(reverseOrder()), limit, 
         new CountingAccumulator<Flight, String>() {
            @Override public boolean filter(Flight source) {
               return source.notCancelled();
            }
            @Override public String getKey(Flight source) {
               return source.getDestination().getState();
            }
            @Override public void forEach(Entry<String, Long> entry) {
               printf("%2s\t%,10d\n", 
                      entry.getKey(), 
                      entry.getValue());
            }
         }
      );
   }

   public void reportLongestFlights(Repository repository) {
      byDistance(repository, new Comparator<Flight>() {
         @Override public int compare(Flight f1, Flight f2) {
            return f2.getDistance() - f1.getDistance();
         }
      });
   }

   public void reportShortestFlights(Repository repository) {
      byDistance(repository, new Comparator<Flight>() {
         @Override public int compare(Flight f1, Flight f2) {
            return f1.getDistance() - f2.getDistance();
         }
      });
   }

   private void byDistance(Repository repository, Comparator<Flight> comparator) {
      final int year = selectYear();
      final int limit = readLimit(10, 1, 100);

      println("Flight #     Date\tCarrier\tOrigin\tDestination\tDistance");
      println(repeat("-", 65));

      Iterator<Flight> iterator = repository.getFlightIterator(year);
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
         printf("%-8s  %10s\t  %2s\t %3s\t    %3s\t\t%6d\n", 
                flight.getFlightNumber(),
                flight.getDate(),
                flight.getCarrier().getCode(),
                flight.getOrigin().getIATA(),
                flight.getDestination().getIATA(),
                flight.getDistance()
         );
         if(++count >= limit) {
            break;
         }         
      }
   }

   public void reportTotalFlightsByDistanceRange(Repository repository) {
      final int year = selectYear();

      println("Range\t\tCount");
      println(repeat("-", 27));

      Iterator<Flight> iterator = repository.getFlightIterator(year);
      accumulate(iterator, comparingByKey(), MAX_LIMIT, 
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
               printf("%-10s\t%,10d\n", entry.getKey(), entry.getValue());
            }
         }
      );
   }

   public void reportDaysWithLeastCancellations(Repository repository) {
      byDaysWithCancellations(repository, comparingByValue());
   }

   public void reportDaysWithMostCancellations(Repository repository) {
      byDaysWithCancellations(repository, comparingByValue(reverseOrder()));
   }

   private void byDaysWithCancellations(Repository repository, 
      Comparator<Entry<ChronoLocalDate, Long>> comparator) {
      final int year = selectYear();
      final int limit = readLimit(10, 1, 100);

      println("Date\t\tCount");
      println(repeat("-", 24));

      Iterator<Flight> iterator = repository.getFlightIterator(year);
      accumulate(iterator, comparator, limit, 
         new CountingAccumulator<Flight, ChronoLocalDate>() {
            @Override public boolean filter(Flight source) {
               return source.cancelled();
            }
            @Override public ChronoLocalDate getKey(Flight source) {
               return source.getDate();
            }
            @Override public void forEach(Entry<ChronoLocalDate, Long> entry) {
               printf("%-10s       %,3d\n", 
                      entry.getKey(), 
                      entry.getValue());
            }
         }
      );
   }

   public void reportTotalMonthlyFlights(Repository repository) {
      final int year = selectYear();

      println("Month\t\tCount");
      println(repeat("-", 27));

      Iterator<Flight> iterator = repository.getFlightIterator(year);
      accumulate(iterator, comparingByKey(), MAX_LIMIT, 
         new CountingAccumulator<Flight, YearMonth>() {
            @Override public boolean filter(Flight source) {
               return source.notCancelled();
            }
            @Override public YearMonth getKey(Flight source) {
               return source.getYearMonth();
            }
            @Override public void forEach(Entry<YearMonth, Long> entry) {
               printf("%s\t%,10d\n", 
                      formatYearMonth(entry.getKey()), 
                      entry.getValue());
            }
         }
      );
   }

   public void reportTotalDailyFlights(Repository repository) {
      final int year = selectYear();

      println("Day\t\t   Count");
      println(repeat("-", 27));

      Iterator<Flight> iterator = repository.getFlightIterator(year);
      accumulate(iterator, comparingByKey(), MAX_LIMIT, 
         new CountingAccumulator<Flight, ChronoLocalDate>() {
            @Override public boolean filter(Flight source) {
               return source.notCancelled();
            }
            @Override public ChronoLocalDate getKey(Flight source) {
               return source.getDate();
            }
            @Override public void forEach(Entry<ChronoLocalDate, Long> entry) {
               printf("%s\t%,10d\n", entry.getKey(), entry.getValue());
            }
         }
      );
   }

   public void reportTotalFlightsByDayOfWeek(Repository repository) {
      final int year = selectYear();

      println("Day of Week\t   Count");
      println(repeat("-", 27));

      Iterator<Flight> iterator = repository.getFlightIterator(year);
      accumulate(iterator, comparingByKey(), MAX_LIMIT, 
         new CountingAccumulator<Flight, DayOfWeek>() {
            @Override public boolean filter(Flight source) {
               return source.notCancelled();
            }
            @Override public DayOfWeek getKey(Flight source) {
               return source.getDate().getDayOfWeek();
            }
            @Override public void forEach(Entry<DayOfWeek, Long> entry) {
               printf("%10s\t%,10d\n", entry.getKey(), entry.getValue());
            }
         }
      );
   }
}