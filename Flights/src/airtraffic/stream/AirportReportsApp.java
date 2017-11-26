package airtraffic.stream;

import static airtraffic.FlightBasedMetrics.highestCancellationRateComparator;
import static airtraffic.GeoHelper.distanceFromReferenceComparator;
import static airtraffic.GeoHelper.getDistance;
import static airtraffic.GeoLocation.Units.MILES;
import static java.util.Comparator.comparing;

import java.util.HashMap;
import java.util.stream.Stream;

import airtraffic.Airport;
import airtraffic.AirportMetrics;
import airtraffic.Flight;
import airtraffic.GeoLocation;
import airtraffic.Repository;

/**
 * Generate various airport statistics using Java 8 streams.
 * 
 * @author tony@piazzaconsulting.com
 */
public class AirportReportsApp extends AbstractReportsApp {
   public static void main(String[] args) throws Exception {
      new AirportReportsApp().executeSelectedReport();
   }

   public void reportAirportsForState(Repository repository) {
      Stream<Airport> source = repository.getAirportStream();
      String state = readString("State").toUpperCase();
      println("\nIATA\tAirport Name\t\t\t\t\tCity");
      println(repeat("-", 77));
      source.filter(a -> a.getState().equals(state))
            .sorted()
            .forEach(a -> printf(" %3s\t%-40s\t%-20s\n", 
                                 a.getIATA(), a.getName(), a.getCity()));
   }

   public void reportAirportsNearLocation(Repository repository) {
      Stream<Airport> source = repository.getAirportStream();
      double latitude = readDouble("Latitude", -90.0, 90.0);
      double longitude = readDouble("Longitude", -180.0, 180.0);
      GeoLocation loc = new GeoLocation() {
         @Override public double getLatitude()  { return latitude;  }
         @Override public double getLongitude() { return longitude; }
      };
      int distance = readInt("Distance (miles)", 1, 1000);
      println("\nIATA\tAirport Name\t\t\t\t\tState\tCity\t\tDistance");
      println(repeat("-", 89));
      source.filter(a -> getDistance(a, loc, MILES) <= distance)
            .sorted(distanceFromReferenceComparator(loc, MILES))
            .forEach(a -> printf(" %3s\t%-40s\t %2s\t%-15s    %,4.0f\n", 
                                 a.getIATA(), 
                                 a.getName(), 
                                 a.getState(), 
                                 left(a.getCity(), 15),
                                 getDistance(a, loc, MILES)));
   }

   public void reportAirportMetrics(Repository repository) {
      int year = selectYear();
      Stream<Flight> source = repository.getFlightStream(year);
      print("IATA    Airport Name                        ");
      println("Total        Cancelled %   Diverted %");
      println(repeat("-", 82));
      source.collect(HashMap::new, 
                     AirportMetrics.accumulator(), 
                     AirportMetrics.combiner())
            .values()
            .stream()
            .sorted(comparing(AirportMetrics::getSubject))
            .forEach(metrics -> {
               Airport airport = metrics.getSubject();
               String name = airport.getName();
               printf(" %3s    %-30s     %,9d    %6.1f        %6.1f\n", 
                      airport.getIATA(),
                      name.substring(0, Math.min(name.length(), 29)),
                      metrics.getTotalFlights(),
                      metrics.getCancellationRate() * 100.0,
                      metrics.getDiversionRate() * 100.0);
            });
   }

   public void reportAirportsWithHighestCancellationRate(Repository repository) {
      int year = selectYear();
      Stream<Flight> source = repository.getFlightStream(year);
      int limit = readLimit(10, 1, 100);
      println("IATA\tRate");
      println("---------------");
      source.collect(HashMap::new, 
                     AirportMetrics.accumulator(), 
                     AirportMetrics.combiner())
            .values()
            .stream()
            .filter(metrics -> metrics.getTotalCancelled() > 0)
            .sorted(highestCancellationRateComparator())
            .limit(limit)
            .forEach(m -> printf(" %3s\t%6.1f\n", 
                                 m.getSubject().getIATA(), 
                                 m.getCancellationRate() * 100.0)
            );
   }
}