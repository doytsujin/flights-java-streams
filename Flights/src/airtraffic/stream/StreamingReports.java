package airtraffic.stream;

import airtraffic.AbstractReportsProvider;
import airtraffic.Airport;
import airtraffic.AirportMetrics;
import airtraffic.Carrier;
import airtraffic.CarrierMetrics;
import airtraffic.Repository;

public class StreamingReports extends AbstractReportsProvider {

   public void reportStreamingAirportMetrics(Repository repository) {
      final int year = selectYear(repository);
      final Airport airport = readAirport(repository, "Airport");

      clearScreen();
      printf("Airport metrics for %s\n\n", airport.getName());
      println("     Total\t Cancelled\t  Diverted\t   Origins\tDestinations");
      println(repeat("-", 77));

      final AirportMetrics metrics = new AirportMetrics(airport);
      repository.getFlightStream(year)
                .filter(flight -> flight.getOrigin().equals(airport) || 
                                  flight.getDestination().equals(airport))
                .forEach(flight -> {
                   metrics.addFlight(flight);
                   printf("%,10d\t%,10d\t%,10d\t%,10d\t  %,10d", 
                          metrics.getTotalFlights(), 
                          metrics.getTotalCancelled(), 
                          metrics.getTotalDiverted(), 
                          metrics.getTotalOrigins(), 
                          metrics.getTotalDestinations()
                   );
                   moveLineToStart();
                });

      println();
   }

   public void reportStreamingCarrierMetrics(Repository repository) {
      final int year = selectYear(repository);
      final Carrier carrier = readCarrier(repository);

      clearScreen();
      printf("Carrier metrics for %s\n\n", carrier.getName());
      println("     Total\t Cancelled\t  Diverted\t  Airports");
      println(repeat("-", 59));

      final CarrierMetrics metrics = new CarrierMetrics(carrier);
      repository.getFlightStream(year)
                .filter(flight -> flight.getCarrier().equals(carrier))
                .forEach(flight -> {
                   metrics.addFlight(flight);
                   printf("%,10d\t%,10d\t%,10d\t%,10d",
                          metrics.getTotalFlights(), 
                          metrics.getTotalCancelled(), 
                          metrics.getTotalDiverted(), 
                          metrics.getAirports().size()
                   );
                   moveLineToStart();
                });

      println();
   }
}