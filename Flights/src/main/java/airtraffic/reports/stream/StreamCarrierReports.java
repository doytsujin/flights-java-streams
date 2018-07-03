package airtraffic.reports.stream;

import static airtraffic.metrics.FlightBasedMetrics.highestCancellationRateComparator;
import static java.util.Comparator.comparing;
import static java.util.Comparator.reverseOrder;
import static java.util.Map.Entry.comparingByValue;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.HashMap;
import airtraffic.Flight;
import airtraffic.ReportContext;
import airtraffic.annotations.StreamStyle;
import airtraffic.jdbc.ResultSetBuilder;
import airtraffic.metrics.CarrierMetrics;
import airtraffic.reports.CarrierReports;


/**
 * Implementation of carrier reports using streams style that was introduced
 * in Java 8.
 *
 * @author Tony Piazza <tony@piazzaconsulting.com>
 */
@StreamStyle
public class StreamCarrierReports implements CarrierReports {
   @Override
   public ResultSet reportMostCancelledFlightsByCarrier(ReportContext context) {
      final int year = context.getYear();
      final int limit = context.getLimit();
      final ResultSetBuilder builder = 
         new ResultSetBuilder().addColumn("Name", Types.VARCHAR)
                               .addColumn("TotalFlights", Types.INTEGER);

      context.getRepository()
             .getFlightStream(year)
             .filter(flight -> flight.cancelled())
             .collect(groupingBy(Flight::getCarrier, counting()))
             .entrySet()
             .stream()
             .sorted(comparingByValue(reverseOrder()))
             .limit(limit)
             .forEachOrdered(entry -> 
                builder.addRow(entry.getKey().getName(), entry.getValue())
             );

      return builder.build();
   }

   @Override
   public ResultSet reportCarrierMetrics(ReportContext context) {
      final int year = context.getYear();
      final ResultSetBuilder builder = 
         new ResultSetBuilder().addColumn("Code", Types.VARCHAR)
                               .addColumn("Name", Types.VARCHAR)
                               .addColumn("TotalFlights", Types.INTEGER)
                               .addColumn("CancellationRate", Types.DOUBLE)
                               .addColumn("DiversionRate", Types.DOUBLE)
                               .addColumn("TotalAirports", Types.INTEGER);

      context.getRepository()
             .getFlightStream(year)
             .collect(HashMap::new, 
                      CarrierMetrics.accumulator(), 
                      CarrierMetrics.combiner())
             .values()
             .stream()
             .sorted(comparing(CarrierMetrics::getSubject))
             .forEach(metrics -> {
                builder.addRow(metrics.getSubject().getCode(),
                               metrics.getSubject().getName(),
                               metrics.getTotalFlights(),
                               metrics.getCancellationRate(),
                               metrics.getDiversionRate(),
                               metrics.getAirports().size());
             });

      return builder.build();
   }

   @Override
   public ResultSet reportCarriersWithHighestCancellationRate(ReportContext context) {
      final int year = context.getYear();
      final int limit = context.getLimit();
      final ResultSetBuilder builder = 
         new ResultSetBuilder().addColumn("Name", Types.VARCHAR)
                               .addColumn("CancellationRate", Types.DOUBLE);

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
             .forEach(metrics -> builder.addRow(metrics.getSubject().getName(),  
                                                metrics.getCancellationRate())
             );

      return builder.build();
   }
}