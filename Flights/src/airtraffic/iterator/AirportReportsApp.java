package airtraffic.iterator;

import static airtraffic.GeoHelper.distanceFromReferenceComparator;
import static airtraffic.GeoHelper.getDistance;
import static airtraffic.GeoLocation.Units.MILES;
import static airtraffic.iterator.AccumulatorHelper.accumulate;
import static java.util.Comparator.naturalOrder;

import java.util.Iterator;

import airtraffic.AbstractReportsApp;
import airtraffic.Airport;
import airtraffic.GeoLocation;
import airtraffic.Repository;

public class AirportReportsApp extends AbstractReportsApp {
   public static void main(String[] args) throws Exception {
      new AirportReportsApp().executeSelectedReport();
   }

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
}