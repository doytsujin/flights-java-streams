package airtraffic.stream;

import static airtraffic.FlightBasedMetrics.highestCancellationRateComparator;
import static airtraffic.GeoHelper.distanceFromReferenceComparator;
import static airtraffic.GeoHelper.getDistance;
import static airtraffic.GeoLocation.Units.MILES;
import static java.util.Comparator.comparing;

import java.util.HashMap;

import airtraffic.AbstractReportsApp;
import airtraffic.Airport;
import airtraffic.AirportMetrics;
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
      final String state = readString("State").toUpperCase();

      println("\nIATA\tAirport Name\t\t\t\t\tCity");
      println(repeat("-", 77));

      repository.getAirportStream()
                .filter(a -> a.getState().equals(state))
                .sorted()
                .forEach(a -> printf("%3s\t%-40s\t%-20s\n", 
                                     a.getIATA(), 
                                     a.getName(), 
                                     a.getCity())
                );
   }

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

      repository.getAirportStream()
                .filter(a -> getDistance(a, loc, MILES) <= distance)
                .sorted(distanceFromReferenceComparator(loc, MILES))
                .forEach(a -> printf("%3s\t%-40s\t %2s\t%-15s    %,4.0f\n", 
                                     a.getIATA(), 
                                     a.getName(), 
                                     a.getState(), 
                                     left(a.getCity(), 15),
                                     getDistance(a, loc, MILES)));
   }

   public void reportAirportMetrics(Repository repository) {
      final int year = selectYear();

      print("\nIATA    Airport Name                        ");
      println("Total        Cancelled %   Diverted %");
      println(repeat("-", 82));

      repository.getFlightStream(year)
                .collect(HashMap::new, 
                         AirportMetrics.accumulator(), 
                         AirportMetrics.combiner())
                .values()
                .stream()
                .sorted(comparing(AirportMetrics::getSubject))
                .forEach(metrics -> {
                   Airport airport = metrics.getSubject();
                   String name = airport.getName();
                   printf("%3s     %-30s     %,9d    %6.1f        %6.1f\n", 
                          airport.getIATA(),
                          name.substring(0, Math.min(name.length(), 29)),
                          metrics.getTotalFlights(),
                          metrics.getCancellationRate() * 100.0,
                          metrics.getDiversionRate() * 100.0);
                });
   }

   public void reportAirportsWithHighestCancellationRate(Repository repository) {
      final int year = selectYear();
      final int limit = readLimit(10, 1, 100);

      println("\nIATA\tName\t\t\t\tRate");
      println(repeat("-", 47));

      repository.getFlightStream(year)
                .collect(HashMap::new, 
                         AirportMetrics.accumulator(), 
                         AirportMetrics.combiner())
                .values()
                .stream()
                .filter(metrics -> metrics.getTotalCancelled() > 0)
                .sorted(highestCancellationRateComparator())
                .limit(limit)
                .forEach(metrics -> {
                   Airport airport = metrics.getSubject();
                   String name = airport.getName();
                   printf("%3s\t%-30s\t%6.1f\n", 
                         airport.getIATA(),
                         left(name, 30),
                         metrics.getCancellationRate() * 100.0
                   );
                });
   }
}