package airtraffic.iterator;

import java.util.Iterator;

import airtraffic.AbstractReportsProvider;
import airtraffic.Airport;
import airtraffic.AirportMetrics;
import airtraffic.Carrier;
import airtraffic.CarrierMetrics;
import airtraffic.Flight;
import airtraffic.Repository;
import airtraffic.LiveReports;

public class IteratorLiveReports extends AbstractReportsProvider implements LiveReports {

   @Override
   public void reportAirportMetrics(Repository repository) {
      final int year = selectYear(repository);
      final Airport airport = readAirport(repository, "Airport");

      clearScreen();
      printf("Airport metrics for %s\n\n", airport.getName());
      println("     Total\t Cancelled\t  Diverted\t   Origins\tDestinations");
      println(repeat("-", 77));

      final AirportMetrics metrics = new AirportMetrics(airport);
      Iterator<Flight> iterator = repository.getFlightIterator(year);
      while(iterator.hasNext()) {
         Flight flight = iterator.next();
         if(flight.getOrigin().equals(airport) || 
            flight.getDestination().equals(airport)) {
            metrics.addFlight(flight);
            printf("%,10d\t%,10d\t%,10d\t%,10d\t  %,10d", 
                   metrics.getTotalFlights(), 
                   metrics.getTotalCancelled(), 
                   metrics.getTotalDiverted(), 
                   metrics.getTotalOrigins(), 
                   metrics.getTotalDestinations()
            );
            moveLineToStart();
         }
      }

      println();
   }

   public void reportCarrierMetrics(Repository repository) {
      final int year = selectYear(repository);
      final Carrier carrier = readCarrier(repository);

      clearScreen();
      printf("Carrier metrics for %s\n\n", carrier.getName());
      println("     Total\t Cancelled\t  Diverted\t  Airports");
      println(repeat("-", 59));

      final CarrierMetrics metrics = new CarrierMetrics(carrier);
      Iterator<Flight> iterator = repository.getFlightIterator(year);
      while(iterator.hasNext()) {
         Flight flight = iterator.next();
         if(flight.getCarrier().equals(carrier)) {
            metrics.addFlight(flight);
            printf("%,10d\t%,10d\t%,10d\t%,10d", 
                   metrics.getTotalFlights(), 
                   metrics.getTotalCancelled(), 
                   metrics.getTotalDiverted(), 
                   metrics.getAirports().size()
            );
            moveLineToStart();
         }
      }

      println();
   }
}