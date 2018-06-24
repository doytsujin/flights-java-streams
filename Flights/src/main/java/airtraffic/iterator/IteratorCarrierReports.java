package airtraffic.iterator;

import static airtraffic.iterator.AccumulatorHelper.accumulate;
import static airtraffic.metrics.FlightBasedMetrics.highestCancellationRateComparator;
import static java.util.Comparator.reverseOrder;
import static java.util.Map.Entry.comparingByKey;
import static java.util.Map.Entry.comparingByValue;
import static org.apache.commons.lang3.StringUtils.left;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.Iterator;
import java.util.Map.Entry;
import airtraffic.Carrier;
import airtraffic.Flight;
import airtraffic.ReportContext;
import airtraffic.jdbc.ResultSetBuilder;
import airtraffic.metrics.CarrierMetrics;
import airtraffic.reports.CarrierReports;


public class IteratorCarrierReports implements CarrierReports {
   @Override
   public ResultSet reportMostCancelledFlightsByCarrier(ReportContext context) {
      final int year = context.getYear();
      final int limit = context.getLimit();
      final ResultSetBuilder builder = 
         new ResultSetBuilder().addColumn("Name", Types.VARCHAR)
                               .addColumn("TotalFlights", Types.INTEGER);

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
               builder.addRow(entry.getKey().getName(), entry.getValue());
            }
         }
      );

      return builder.build();
   }

   @Override
   public ResultSet reportCarrierMetrics(ReportContext context) {
      final int year = context.getYear();
      final int limit = context.getLimit();
      final ResultSetBuilder builder = 
         new ResultSetBuilder().addColumn("Code", Types.VARCHAR)
                               .addColumn("Name", Types.VARCHAR)
                               .addColumn("TotalFlights", Types.INTEGER)
                               .addColumn("CancellationRate", Types.DOUBLE)
                               .addColumn("DiversionRate", Types.DOUBLE)
                               .addColumn("TotalAirports", Types.INTEGER);

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
               builder.addRow(entry.getKey(),
                              metrics.getSubject().getName(),
                              metrics.getTotalFlights(),
                              metrics.getCancellationRate(),
                              metrics.getDiversionRate(),
                              metrics.getAirports().size());
            }
         }
      );

      return builder.build();
   }

   @Override
   public ResultSet reportCarriersWithHighestCancellationRate(ReportContext context) {
      final int year = context.getYear();
      final int limit = context.getLimit();
      final ResultSetBuilder builder = 
         new ResultSetBuilder().addColumn("Name", Types.VARCHAR)
                               .addColumn("CancellationRate", Types.DOUBLE);

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
               builder.addRow(metrics.getSubject().getName(), 
                              metrics.getCancellationRate());
            }
         }
      );

      return builder.build();
   }
}