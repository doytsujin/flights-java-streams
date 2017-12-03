package airtraffic.iterator;

import static airtraffic.FlightBasedMetrics.highestCancellationRateComparator;
import static airtraffic.iterator.AccumulatorHelper.accumulate;
import static java.util.Comparator.reverseOrder;
import static java.util.Map.Entry.comparingByKey;
import static java.util.Map.Entry.comparingByValue;

import java.util.Iterator;
import java.util.Map.Entry;

import airtraffic.AbstractReportsProvider;
import airtraffic.Carrier;
import airtraffic.CarrierMetrics;
import airtraffic.CarrierReports;
import airtraffic.Flight;
import airtraffic.Repository;

public class IteratorCarrierReports extends AbstractReportsProvider implements CarrierReports {

   @Override
   public void reportMostCancelledFlightsByCarrier(Repository repository) {
      final int year = selectYear(repository);
      final int limit = readLimit(10, 1, 100);

      println("Carrier\t\t\t\t Count");
      println("-----------------------------------------");

      Iterator<Flight> iterator = repository.getFlightIterator(year);
      accumulate(iterator, comparingByValue(reverseOrder()), limit, 
         new CountingAccumulator<Flight, Carrier>() {
            @Override public boolean filter(Flight source) {
               return source.cancelled(); 
            }
            @Override public Carrier getKey(Flight source) {
               return source.getCarrier();
            }
            @Override public void forEach(Entry<Carrier, Long> entry) {
               printf("%-24s\t%,8d\n", 
                      left(entry.getKey().getName(), 24), 
                      entry.getValue());
            }
         }
      );
   }

   @Override
   public void reportCarrierMetrics(Repository repository) {
      final int year = selectYear(repository);

      print("Code    Carrier Name                        ");
      println("Total        Cancelled %   Diverted %    Airports");
      println(repeat("-", 94));

      Iterator<Flight> iterator = repository.getFlightIterator(year);
      accumulate(iterator, comparingByKey(), MAX_LIMIT, 
         new MapAccumulator<Flight, String, CarrierMetrics>() {
            @Override public boolean filter(Flight source) {
               return true;
            }
            @Override public String getKey(Flight flight) {
               return flight.getCarrier().getCode();
            }
            @Override public CarrierMetrics initializeValue(Flight flight) {
               CarrierMetrics metrics = new CarrierMetrics(flight.getCarrier());
               metrics.addFlight(flight);
               return metrics;
            }
            @Override public CarrierMetrics updateValue(Flight flight, CarrierMetrics metrics) {
               return metrics.addFlight(flight);
            }
            @Override public void forEach(Entry<String, CarrierMetrics> entry) {
               CarrierMetrics metrics = entry.getValue();
               String name = metrics.getSubject().getName();
               printf(" %2s     %-30s     %,9d    %6.1f        %6.1f         %,5d\n", 
                      entry.getKey(),
                      left(name, 30),
                      metrics.getTotalFlights(),
                      metrics.getCancellationRate() * 100.0,
                      metrics.getDiversionRate() * 100.0,
                      metrics.getAirports().size()
               );
            }
         }
      );
   }

   @Override
   public void reportCarriersWithHighestCancellationRate(Repository repository) {
      final int year = selectYear(repository);
      final int limit = readLimit(10, 1, 100);

      println("Carrier                           Rate");
      println("---------------------------------------");

      Iterator<Flight> iterator = repository.getFlightIterator(year);
      accumulate(iterator, comparingByValue(highestCancellationRateComparator()), limit, 
         new MapAccumulator<Flight, String, CarrierMetrics>() {
            @Override public boolean filter(Flight source) {
               return true;
            }
            @Override public String getKey(Flight flight) {
               return flight.getCarrier().getCode();
            }
            @Override public CarrierMetrics initializeValue(Flight flight) {
               CarrierMetrics metrics = new CarrierMetrics(flight.getCarrier());
               metrics.addFlight(flight);
               return metrics;
            }
            @Override public CarrierMetrics updateValue(Flight flight, CarrierMetrics metrics) {
               return metrics.addFlight(flight);
            }
            @Override public void forEach(Entry<String, CarrierMetrics> entry) {
               CarrierMetrics metrics = entry.getValue();
               printf("%-30s\t%6.1f\n", 
                      left(metrics.getSubject().getName(), 30), 
                      metrics.getCancellationRate() * 100.0
               );
            }
         }
      );
   }
}