package airtraffic.iterator;

import static airtraffic.iterator.AccumulatorHelper.accumulate;
import static java.util.Comparator.reverseOrder;
import static java.util.Map.Entry.comparingByValue;

import java.util.Iterator;
import java.util.Map.Entry;

import airtraffic.Airport;
import airtraffic.Flight;
import airtraffic.Repository;
import airtraffic.Route;
import airtraffic.stream.AbstractReportsApp;

/**
 * Generate various flight statistics using Java iterators.
 *
 * @author tony@piazzaconsulting.com
 */
public class FlightReportsApp extends AbstractReportsApp {
   public static void main(String[] args) throws Exception {
      new FlightReportsApp().executeSelectedReport();
   }

   public void reportTotalFlightsFromOrigin(Repository repository) {
      int year = selectYear();
      Airport origin = readAirport("Origin");

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
      int year = selectYear();
      Airport destination = readAirport("Destination");

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
      int year = selectYear();
      Airport origin = readAirport("Origin");
      Airport destination = readAirport("Destination");

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
      int year = selectYear();
      int limit = readLimit(10, 1, 100);

      println("\nOrigin\t\tCount");
      println(repeat("-", 27));

      Iterator<Flight> iterator = repository.getFlightIterator(year);
      accumulate(iterator, comparingByValue(reverseOrder()), limit, 
         new MapAccumulator<Flight, Airport, Long>() {
            @Override public boolean filter(Flight source) {
               return source.notCancelled();
            }
            @Override public Airport getKey(Flight source) {
               return source.getOrigin();
            }
            @Override public Long initializeValue(Flight source) {
               return Long.valueOf(1);
            }
            @Override public Long updateValue(Flight source, Long value) {
               return Long.valueOf(value.longValue() + 1);
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
      int year = selectYear();
      Airport origin = readAirport("Origin");
      int limit = readLimit(10, 1, 100);

      printf("Top destinations from %s\n\n", origin.getName());
      println("Destination\t   Count");
      println(repeat("-", 30));

      Iterator<Flight> iterator = repository.getFlightIterator(year);
      accumulate(iterator, comparingByValue(reverseOrder()), limit, 
         new MapAccumulator<Flight, Airport, Long>() {
            @Override public boolean filter(Flight source) {
               return source.notCancelled() && 
                      source.getOrigin().equals(origin);
            }
            @Override public Airport getKey(Flight source) {
               return source.getDestination();
            }
            @Override public Long initializeValue(Flight source) {
               return Long.valueOf(1);
            }
            @Override public Long updateValue(Flight source, Long value) {
               return Long.valueOf(value.longValue() + 1);
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
      int year = selectYear();
      int limit = readLimit(10, 1, 100);

      println("Route\t\t    Count");
      println(repeat("-", 27));

      Iterator<Flight> iterator = repository.getFlightIterator(year);
      accumulate(iterator, comparingByValue(reverseOrder()), limit, 
         new MapAccumulator<Flight, Route, Long>() {
            @Override public boolean filter(Flight source) {
               return true;
            }
            @Override public Route getKey(Flight source) {
               return source.getRoute();
            }
            @Override public Long initializeValue(Flight source) {
               return Long.valueOf(1);
            }
            @Override public Long updateValue(Flight source, Long value) {
               return Long.valueOf(value.longValue() + 1);
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
      int year = selectYear();
      int limit = readLimit(10, 1, 100);

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
}