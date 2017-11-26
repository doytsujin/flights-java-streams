package airtraffic.iterator;

import static airtraffic.iterator.MapUtils.accumulateCount;
import static java.util.Collections.reverseOrder;
import static java.util.Map.Entry.comparingByValue;

import java.util.Iterator;
import java.util.Map.Entry;

import airtraffic.Airport;
import airtraffic.Flight;
import airtraffic.Repository;
import airtraffic.stream.AbstractReportsApp;

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
      accumulateCount(iterator, comparingByValue(reverseOrder()), limit, 
         new MapAccumulator<Flight, Airport, Long>() {
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
      int year = selectYear();
      Airport origin = readAirport("Origin");
      int limit = readLimit(10, 1, 100);

      printf("Top destinations from %s\n\n", origin.getName());
      println("Destination\t   Count");
      println(repeat("-", 30));

      Iterator<Flight> iterator = repository.getFlightIterator(year);
      accumulateCount(iterator, comparingByValue(reverseOrder()), limit, 
         new MapAccumulator<Flight, Airport, Long>() {
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
}