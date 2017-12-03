package airtraffic.stream;

import org.beryx.textio.TextTerminal;

import airtraffic.Airport;
import airtraffic.AirportMetrics;
import airtraffic.Carrier;
import airtraffic.CarrierMetrics;
import airtraffic.LiveReports;
import airtraffic.ReportContext;

public class StreamLiveReports implements LiveReports {

   @Override
   public void reportAirportMetrics(ReportContext context) {
      final int year = context.getYear();
      final Airport airport = context.getAirport();

      TextTerminal<?> terminal = context.getTerminal();
      final AirportMetrics metrics = new AirportMetrics(airport);
      context.getRepository()
             .getFlightStream(year)
             .filter(flight -> flight.getOrigin().equals(airport) || 
                               flight.getDestination().equals(airport))
             .forEach(flight -> {
                metrics.addFlight(flight);
                terminal.printf("%,10d\t%,10d\t%,10d\t%,10d\t  %,10d", 
                                metrics.getTotalFlights(), 
                                metrics.getTotalCancelled(), 
                                metrics.getTotalDiverted(), 
                                metrics.getTotalOrigins(), 
                                metrics.getTotalDestinations());
                terminal.moveToLineStart();
             });

      terminal.println();
   }

   @Override
   public void reportCarrierMetrics(ReportContext context) {
      final int year = context.getYear();
      final Carrier carrier = context.getCarrier();

      TextTerminal<?> terminal = context.getTerminal();
      final CarrierMetrics metrics = new CarrierMetrics(carrier);
      context.getRepository()
             .getFlightStream(year)
             .filter(flight -> flight.getCarrier().equals(carrier))
             .forEach(flight -> {
                metrics.addFlight(flight);
                terminal.printf("%,10d\t%,10d\t%,10d\t%,10d",
                                metrics.getTotalFlights(), 
                                metrics.getTotalCancelled(), 
                                metrics.getTotalDiverted(), 
                                metrics.getAirports().size());
                terminal.moveToLineStart();
             });

      terminal.println();
   }
}