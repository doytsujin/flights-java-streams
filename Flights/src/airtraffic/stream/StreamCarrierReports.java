package airtraffic.stream;

import static airtraffic.FlightBasedMetrics.highestCancellationRateComparator;
import static java.util.Comparator.comparing;
import static java.util.Comparator.reverseOrder;
import static java.util.Map.Entry.comparingByValue;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static org.apache.commons.lang3.StringUtils.left;

import java.util.HashMap;

import airtraffic.Carrier;
import airtraffic.CarrierMetrics;
import airtraffic.CarrierReports;
import airtraffic.Flight;
import airtraffic.ReportContext;

public class StreamCarrierReports implements CarrierReports {

   @Override
   public void reportMostCancelledFlightsByCarrier(ReportContext context) {
      final int year = context.getYear();
      final int limit = context.getLimit();

      context.getRepository()
             .getFlightStream(year)
             .filter(f -> f.cancelled())
             .collect(groupingBy(Flight::getCarrier, counting()))
             .entrySet()
             .stream()
             .sorted(comparingByValue(reverseOrder()))
             .limit(limit)
             .forEachOrdered(e -> context.getTerminal()
                                         .printf("%-24s\t%,8d\n", 
                                                 left(e.getKey().getName(), 24), 
                                                 e.getValue())
             );
   }

   @Override
   public void reportCarrierMetrics(ReportContext context) {
      final int year = context.getYear();

      context.getRepository()
             .getFlightStream(year)
             .collect(HashMap::new, 
                      CarrierMetrics.accumulator(), 
                      CarrierMetrics.combiner())
             .values()
             .stream()
             .sorted(comparing(CarrierMetrics::getSubject))
             .forEach(metrics -> {
                Carrier carrier = metrics.getSubject();
                String name = carrier.getName();
                context.getTerminal()
                       .printf(" %2s     %-30s     %,9d    %6.1f        %6.1f         %,5d\n", 
                               carrier.getCode(),
                               left(name, 30),
                               metrics.getTotalFlights(),
                               metrics.getCancellationRate() * 100.0,
                               metrics.getDiversionRate() * 100.0,
                               metrics.getAirports().size()
                );
             });
   }

   @Override
   public void reportCarriersWithHighestCancellationRate(ReportContext context) {
      final int year = context.getYear();
      final int limit = context.getLimit();

      context.getRepository()
             .getFlightStream(year)
             .collect(HashMap::new, 
                      CarrierMetrics.accumulator(), 
                      CarrierMetrics.combiner())
             .values()
             .stream()
             .filter(metrics -> metrics.getTotalCancelled() > 0)
             .sorted(highestCancellationRateComparator())
             .limit(limit)
             .forEach(m -> context.getTerminal()
                                  .printf("%-30s\t%6.1f\n", 
                                          m.getSubject().getName(),
                                          m.getCancellationRate() * 100.0)
             );
   }
}