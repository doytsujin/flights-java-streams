package airtraffic.iterator;

import java.util.Iterator;

import org.beryx.textio.TextTerminal;

import airtraffic.Airport;
import airtraffic.AirportMetrics;
import airtraffic.Carrier;
import airtraffic.CarrierMetrics;
import airtraffic.Flight;
import airtraffic.LiveReports;
import airtraffic.ReportContext;

public class IteratorLiveReports implements LiveReports {

   @Override
   public void reportAirportMetrics(ReportContext context) {
      final int year = context.getYear();
      final Airport airport = context.getAirport();

      TextTerminal<?> terminal = context.getTerminal();
      final AirportMetrics metrics = new AirportMetrics(airport);
      Iterator<Flight> iterator = context.getRepository()
                                         .getFlightIterator(year);
      while(iterator.hasNext()) {
         Flight flight = iterator.next();
         if(flight.getOrigin().equals(airport) || 
            flight.getDestination().equals(airport)) {
            metrics.addFlight(flight);
            terminal.printf("%,10d\t%,10d\t%,10d\t%,10d\t  %,10d", 
                            metrics.getTotalFlights(), 
                            metrics.getTotalCancelled(), 
                            metrics.getTotalDiverted(), 
                            metrics.getTotalOrigins(), 
                            metrics.getTotalDestinations());
            terminal.moveToLineStart();
         }
      }

      terminal.println();
   }

   public void reportCarrierMetrics(ReportContext context) {
      final int year = context.getYear();
      final Carrier carrier = context.getCarrier();

      TextTerminal<?> terminal = context.getTerminal();
      final CarrierMetrics metrics = new CarrierMetrics(carrier);
      Iterator<Flight> iterator = context.getRepository()
                                         .getFlightIterator(year);
      while(iterator.hasNext()) {
         Flight flight = iterator.next();
         if(flight.getCarrier().equals(carrier)) {
            metrics.addFlight(flight);
            terminal.printf("%,10d\t%,10d\t%,10d\t%,10d", 
                            metrics.getTotalFlights(), 
                            metrics.getTotalCancelled(), 
                            metrics.getTotalDiverted(), 
                            metrics.getAirports().size());
            terminal.moveToLineStart();
         }
      }

      terminal.println();
   }
}