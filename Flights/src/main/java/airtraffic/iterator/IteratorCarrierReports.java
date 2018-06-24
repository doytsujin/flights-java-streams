package airtraffic.iterator;

import static airtraffic.iterator.AccumulatorHelper.accumulate;
import static airtraffic.metrics.FlightBasedMetrics.highestCancellationRateComparator;
import static java.util.Comparator.reverseOrder;
import static java.util.Map.Entry.comparingByKey;
import static java.util.Map.Entry.comparingByValue;
import static org.apache.commons.lang3.StringUtils.left;

import java.util.Iterator;
import java.util.Map.Entry;

import airtraffic.Carrier;
import airtraffic.Flight;
import airtraffic.ReportContext;
import airtraffic.metrics.CarrierMetrics;
import airtraffic.reports.CarrierReports;

public class IteratorCarrierReports implements CarrierReports {

   @Override
   public void reportMostCancelledFlightsByCarrier(ReportContext context) {
      final int year = context.getYear();
      final int limit = context.getLimit();

      Iterator<Flight> iterator = context.getRepository().getFlightIterator(year);
      accumulate(iterator, comparingByValue(reverseOrder()), limit, 
         new CountingAccumulator<Flight, Carrier>() {
            @Override public boolean filter(Flight source) {
               return source.cancelled(); 
            }
            @Override public Carrier getKey(Flight source) {
               return source.getCarrier();
            }
            @Override public void forEach(Entry<Carrier, Long> entry) {
               context.getTerminal()
                      .printf("%-24s\t%,8d\n", 
                              left(entry.getKey().getName(), 24), 
                              entry.getValue());
            }
         }
      );
   }

   @Override
   public void reportCarrierMetrics(ReportContext context) {
      final int year = context.getYear();
      final int limit = context.getLimit();

      Iterator<Flight> iterator = context.getRepository().getFlightIterator(year);
      accumulate(iterator, comparingByKey(), limit, 
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
               context.getTerminal()
                      .printf(" %2s     %-30s     %,9d    %6.1f        %6.1f         %,5d\n", 
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
   public void reportCarriersWithHighestCancellationRate(ReportContext context) {
      final int year = context.getYear();
      final int limit = context.getLimit();

      Iterator<Flight> iterator = context.getRepository().getFlightIterator(year);
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
               context.getTerminal()
                      .printf("%-30s\t%6.1f\n", 
                              left(metrics.getSubject().getName(), 30), 
                              metrics.getCancellationRate() * 100.0
               );
            }
         }
      );
   }
}