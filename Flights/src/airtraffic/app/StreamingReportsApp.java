package airtraffic.app;

import java.util.stream.Stream;

import org.beryx.textio.jline.JLineTextTerminal;

import airtraffic.Airport;
import airtraffic.AirportMetrics;
import airtraffic.Carrier;
import airtraffic.CarrierMetrics;
import airtraffic.Flight;
import airtraffic.Repository;
import airtraffic.TerminalType;

public class StreamingReportsApp extends AbstractReportsApp {
   public static void main(String[] args) throws Exception {
      new StreamingReportsApp().executeSelectedReport();
   }

   @TerminalType(JLineTextTerminal.class)
   public void reportStreamingAirportMetrics(Repository repository) {
      int year = selectYear();
      Stream<Flight> source = repository.getFlightStream(year);
      Airport airport = readAirport("Airport");
      final AirportMetrics metrics = new AirportMetrics(airport);
      clearScreen();
      printf("Airport metrics for %s\n\n", airport.getName());
      println("     Total\t Cancelled\t  Diverted\t   Origins\tDestinations");
      println(repeat("-", 77));
      source.filter(flight -> flight.getOrigin().equals(airport) || 
                              flight.getDestination().equals(airport))
            .peek(flight -> {
               metrics.addFlight(flight);
               rawPrintf("%,10d\t%,10d\t%,10d\t%,10d\t  %,10d\r", 
                         metrics.getTotalFlights(), 
                         metrics.getTotalCancelled(), 
                         metrics.getTotalDiverted(), 
                         metrics.getTotalOrigins(), 
                         metrics.getTotalDestinations()
               );
            }).allMatch(flight -> true);
      println();
   }

   @TerminalType(JLineTextTerminal.class)
   public void reportStreamingCarrierMetrics(Repository repository) {
      int year = selectYear();
      Stream<Flight> source = repository.getFlightStream(year);
      Carrier carrier = readCarrier();
      CarrierMetrics metrics = new CarrierMetrics(carrier);
      clearScreen();
      printf("Carrier metrics for %s\n\n", carrier.getName());
      println("     Total\t Cancelled\t  Diverted\t  Airports");
      println(repeat("-", 59));
      source.filter(flight -> flight.getCarrier().equals(carrier))
            .peek(flight -> {
               metrics.addFlight(flight);
               rawPrintf("%,10d\t%,10d\t%,10d\t%,10d\r",
                         metrics.getTotalFlights(), 
                         metrics.getTotalCancelled(), 
                         metrics.getTotalDiverted(), 
                         metrics.getAirports().size()
               );
            }).allMatch(flight -> true);
      println();
   }
}