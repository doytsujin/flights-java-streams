package airtraffic.stream;

import static airtraffic.FlightBasedMetrics.highestCancellationRateComparator;
import static airtraffic.GeoHelper.distanceFromReferenceComparator;
import static airtraffic.GeoHelper.getDistance;
import static airtraffic.GeoLocation.Units.MILES;
import static java.util.Comparator.comparing;
import static org.apache.commons.lang3.StringUtils.left;

import java.util.HashMap;

import airtraffic.Airport;
import airtraffic.AirportMetrics;
import airtraffic.AirportReports;
import airtraffic.GeoLocation;
import airtraffic.ReportContext;

/**
 * Generate various airport statistics using Java 8 streams.
 * 
 * @author tony@piazzaconsulting.com
 */
public class StreamAirportReports implements AirportReports {

   @Override
   public void reportAirportsForState(ReportContext context) {
      final String state = context.getState();

      context.getRepository()
             .getAirportStream()
             .filter(a -> a.getState().equals(state))
             .sorted()
             .forEach(a -> context.getTerminal()
                                  .printf("%3s\t%-40s\t%-20s\n", 
                                          a.getIATA(), 
                                          a.getName(), 
                                          a.getCity())
             );
   }

   @Override
   public void reportAirportsNearLocation(ReportContext context) {
      final GeoLocation loc = context.getLocation();
      final int distance = context.getDistance();

      context.getRepository()
             .getAirportStream()
             .filter(a -> getDistance(a, loc, MILES) <= distance)
             .sorted(distanceFromReferenceComparator(loc, MILES))
             .forEach(a -> context.getTerminal()
                                  .printf("%3s\t%-40s\t %2s\t%-15s    %,4.0f\n", 
                                          a.getIATA(), 
                                          a.getName(), 
                                          a.getState(), 
                                          left(a.getCity(), 15),
                                          getDistance(a, loc, MILES)));
   }

   @Override
   public void reportAirportMetrics(ReportContext context) {
      final int year = context.getYear();

      context.getRepository()
             .getFlightStream(year)
             .collect(HashMap::new, 
                      AirportMetrics.accumulator(), 
                      AirportMetrics.combiner())
             .values()
             .stream()
             .sorted(comparing(AirportMetrics::getSubject))
             .forEach(metrics -> {
                Airport airport = metrics.getSubject();
                String name = airport.getName();
                context.getTerminal()
                       .printf("%3s     %-30s     %,9d    %6.1f        %6.1f\n", 
                               airport.getIATA(),
                               name.substring(0, Math.min(name.length(), 29)),
                               metrics.getTotalFlights(),
                               metrics.getCancellationRate() * 100.0,
                               metrics.getDiversionRate() * 100.0);
             });
   }

   @Override
   public void reportAirportsWithHighestCancellationRate(ReportContext context) {
      final int year = context.getYear();
      final int limit = context.getLimit();

      context.getRepository()
             .getFlightStream(year)
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
                context.getTerminal()
                       .printf("%3s\t%-30s\t%6.1f\n", 
                               airport.getIATA(),
                               left(name, 30),
                               metrics.getCancellationRate() * 100.0
                );
             });
   }
}