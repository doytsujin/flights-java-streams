package airtraffic.app;

import static airtraffic.FlightBasedMetrics.highestCancellationRateComparator;
import static java.util.Comparator.comparing;
import static java.util.Comparator.reverseOrder;
import static java.util.Map.Entry.comparingByValue;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

import java.util.HashMap;
import java.util.stream.Stream;

import airtraffic.Carrier;
import airtraffic.CarrierMetrics;
import airtraffic.Flight;
import airtraffic.Repository;

public class CarrierReportsApp extends AbstractReportsApp {

   public static void main(String[] args) throws Exception {
      new CarrierReportsApp().executeSelectedReport();
   }

   public void reportMostCancelledFlightsByCarrier(Repository repository) {
      int year = selectYear();
      Stream<Flight> source = repository.getFlightStream(year);
      int limit = readLimit(10, 1, 100);
      println("Carrier\t\t\t\t Count");
      println("-----------------------------------------");
      source.filter(f -> f.cancelled())
            .map(f -> f.getCarrier())
            .collect(groupingBy(Carrier::getName, counting()))
            .entrySet()
            .stream()
            .sorted(comparingByValue(reverseOrder()))
            .limit(limit)
            .forEachOrdered(e -> printf("%-24s\t%,8d\n",
                                        left(e.getKey(), 24), 
                                        e.getValue()));
   }

   public void reportCarrierMetrics(Repository repository) {
      int year = selectYear();
      Stream<Flight> source = repository.getFlightStream(year);
      print("Code    Carrier Name                        ");
      println("Total        Cancelled %   Diverted %    Airports");
      println(repeat("-", 94));
      source.collect(HashMap::new, 
                     CarrierMetrics.accumulator(), 
                     CarrierMetrics.combiner())
            .values()
            .stream()
            .sorted(comparing(CarrierMetrics::getSubject))
            .forEach(metrics -> {
               Carrier carrier = metrics.getSubject();
               String name = carrier.getName();
               printf(" %2s     %-30s     %,9d    %6.1f        %6.1f         %,5d\n", 
                      carrier.getCode(),
                      name.substring(0, Math.min(name.length(), 29)),
                      metrics.getTotalFlights(),
                      metrics.getCancellationRate() * 100.0,
                      metrics.getDiversionRate() * 100.0,
                      metrics.getAirports().size()
               );
            });
   }

   public void reportCarriersWithHighestCancellationRate(Repository repository) {
      int year = selectYear();
      Stream<Flight> source = repository.getFlightStream(year);
      int limit = readLimit(10, 1, 100);
      println("Carrier                           Rate");
      println("---------------------------------------");
      source.collect(HashMap::new, 
                     CarrierMetrics.accumulator(), 
                     CarrierMetrics.combiner())
            .values()
            .stream()
            .filter(metrics -> metrics.getTotalCancelled() > 0)
            .sorted(highestCancellationRateComparator())
            .limit(limit)
            .forEach(m -> printf("%-30s\t%6.1f\n", 
                                 m.getSubject().getName(),
                                 m.getCancellationRate() * 100.0)
            );
   }
}