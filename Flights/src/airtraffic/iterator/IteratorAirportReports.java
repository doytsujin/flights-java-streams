package airtraffic.iterator;

import static airtraffic.FlightBasedMetrics.highestCancellationRateComparator;
import static airtraffic.GeoHelper.distanceFromReferenceComparator;
import static airtraffic.GeoHelper.getDistance;
import static airtraffic.GeoLocation.Units.MILES;
import static airtraffic.iterator.AccumulatorHelper.accumulate;
import static java.util.Comparator.naturalOrder;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import airtraffic.AbstractReportsProvider;
import airtraffic.Airport;
import airtraffic.AirportMetrics;
import airtraffic.AirportReports;
import airtraffic.Flight;
import airtraffic.GeoLocation;
import airtraffic.Repository;

public class IteratorAirportReports extends AbstractReportsProvider implements AirportReports {

   @Override
   public void reportAirportsForState(Repository repository) {
      final String state = readString("State").toUpperCase();

      println("\nIATA\tAirport Name\t\t\t\t\tCity");
      println(repeat("-", 77));

      Iterator<Airport> iterator = repository.getAirportIterator();
      accumulate(iterator, naturalOrder(), MAX_LIMIT, 
         new ListAccumulator<Airport>() {
            @Override public boolean filter(Airport airport) {
               return airport.getState().equals(state);
            }
            @Override public void forEach(Airport airport) {
               printf("%3s\t%-40s\t%-20s\n", 
                      airport.getIATA(), 
                      airport.getName(), 
                      airport.getCity()
              );               
            }
         }
      );
   }

   @Override
   public void reportAirportsNearLocation(Repository repository) {
      final double latitude = readDouble("Latitude", -90.0, 90.0);
      final double longitude = readDouble("Longitude", -180.0, 180.0);
      final GeoLocation loc = new GeoLocation() {
         @Override public double getLatitude()  { return latitude;  }
         @Override public double getLongitude() { return longitude; }
      };
      final int distance = readInt("Distance (miles)", 1, 1000);

      println("\nIATA\tAirport Name\t\t\t\t\tState\tCity\t\tDistance");
      println(repeat("-", 89));

      Iterator<Airport> iterator = repository.getAirportIterator();
      accumulate(iterator, distanceFromReferenceComparator(loc, MILES), MAX_LIMIT, 
         new ListAccumulator<Airport>() {
            @Override public boolean filter(Airport airport) {
               return getDistance(airport, loc, MILES) <= distance;
            }
            @Override public void forEach(Airport airport) {
               printf("%3s\t%-40s\t %2s\t%-15s    %,4.0f\n", 
                     airport.getIATA(), 
                     airport.getName(), 
                     airport.getState(), 
                     left(airport.getCity(), 15),
                     getDistance(airport, loc, MILES)
               );
            }
         }
      );
   }

   @Override
   public void reportAirportMetrics(Repository repository) {
      final int year = selectYear(repository);

      print("\nIATA    Airport Name                        ");
      println("Total        Cancelled %   Diverted %");
      println(repeat("-", 82));

      Iterator<Flight> iterator = repository.getFlightIterator(year);
      Map<Airport, AirportMetrics> map = new HashMap<>();
      while(iterator.hasNext()) {
         Flight flight = iterator.next();
         Airport origin = flight.getOrigin();
         AirportMetrics metrics1 = map.get(origin);
         if(metrics1 == null) {
            metrics1 = new AirportMetrics(origin);
            map.put(origin, metrics1);
         }
         metrics1.addFlight(flight);
         Airport destination = flight.getDestination();
         AirportMetrics metrics2 = map.get(destination);
         if(metrics2 == null) {
            metrics2 = new AirportMetrics(destination);
            map.put(destination, metrics2);
         }
         metrics2.addFlight(flight);
      }
      SortedSet<AirportMetrics> set = 
         new TreeSet<>(new Comparator<AirportMetrics>() {
            @Override public int compare(AirportMetrics m1, AirportMetrics m2) {
               return m1.getSubject().compareTo(m2.getSubject());
            }
         });
      set.addAll(map.values());
      for(AirportMetrics metrics : set) {
         Airport airport = metrics.getSubject();
         String name = airport.getName();
         printf("%3s     %-30s     %,9d    %6.1f        %6.1f\n", 
                airport.getIATA(),
                name.substring(0, Math.min(name.length(), 29)),
                metrics.getTotalFlights(),
                metrics.getCancellationRate() * 100.0,
                metrics.getDiversionRate() * 100.0
         );
      }
   }

   @Override
   public void reportAirportsWithHighestCancellationRate(Repository repository) {
      final int year = selectYear(repository);
      final int limit = readLimit(10, 1, 100);

      println("\nIATA\tName\t\t\t\tRate");
      println(repeat("-", 47));

      Iterator<Flight> iterator = repository.getFlightIterator(year);
      Map<Airport, AirportMetrics> map = new HashMap<>();
      while(iterator.hasNext()) {
         Flight flight = iterator.next();
         Airport origin = flight.getOrigin();
         AirportMetrics metrics1 = map.get(origin);
         if(metrics1 == null) {
            metrics1 = new AirportMetrics(origin);
            map.put(origin, metrics1);
         }
         metrics1.addFlight(flight);
         Airport destination = flight.getDestination();
         AirportMetrics metrics2 = map.get(destination);
         if(metrics2 == null) {
            metrics2 = new AirportMetrics(destination);
            map.put(destination, metrics2);
         }
         metrics2.addFlight(flight);
      }
      SortedSet<AirportMetrics> set = 
         new TreeSet<>(highestCancellationRateComparator());
      set.addAll(map.values());
      int count = 0;
      for(AirportMetrics metrics : set) {
         Airport airport = metrics.getSubject();
         String name = airport.getName();
         printf("%3s\t%-30s\t%6.1f\n", 
                airport.getIATA(),
                left(name, 30),
                metrics.getCancellationRate() * 100.0
         );
         if(++count >= limit) {
            break;
         }
      }
   }
}