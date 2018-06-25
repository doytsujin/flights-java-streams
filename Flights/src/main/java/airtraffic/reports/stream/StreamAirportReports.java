package airtraffic.reports.stream;

import static airtraffic.GeoHelper.distanceFromReferenceComparator;
import static airtraffic.GeoHelper.getDistance;
import static airtraffic.GeoLocation.Units.MILES;
import static airtraffic.metrics.FlightBasedMetrics.highestCancellationRateComparator;
import static java.util.Comparator.comparing;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.HashMap;
import airtraffic.Airport;
import airtraffic.GeoLocation;
import airtraffic.ReportContext;
import airtraffic.jdbc.ResultSetBuilder;
import airtraffic.metrics.AirportMetrics;
import airtraffic.reports.AirportReports;


/**
 * Generate various airport statistics using Java 8 streams.
 * 
 * @author tony@piazzaconsulting.com
 */
public class StreamAirportReports implements AirportReports {
   @Override
   public ResultSet reportAirportsForState(ReportContext context) {
      final String state = context.getState();
      final ResultSetBuilder builder = 
         new ResultSetBuilder().addColumn("IATA", Types.VARCHAR)
                               .addColumn("Name", Types.VARCHAR)
                               .addColumn("City", Types.VARCHAR);

      context.getRepository()
             .getAirportStream()
             .filter(airport -> airport.getState().equals(state))
             .sorted()
             .forEach(airport -> builder.addRow(airport.getIATA(), 
                                                airport.getName(), 
                                                airport.getCity())
             );

      return builder.build();
   }

   @Override
   public ResultSet reportAirportsNearLocation(ReportContext context) {
      final GeoLocation loc = context.getLocation();
      final int distance = context.getDistance();
      final ResultSetBuilder builder = 
         new ResultSetBuilder().addColumn("IATA", Types.VARCHAR)
                               .addColumn("Name", Types.VARCHAR)
                               .addColumn("State", Types.VARCHAR)
                               .addColumn("City", Types.VARCHAR)
                               .addColumn("Distance", Types.VARCHAR);

      context.getRepository()
             .getAirportStream()
             .filter(airport -> getDistance(airport, loc, MILES) <= distance)
             .sorted(distanceFromReferenceComparator(loc, MILES))
             .forEach(airport -> builder.addRow(airport.getIATA(), 
                                                airport.getName(), 
                                                airport.getState(), 
                                                airport.getCity(),
                                                getDistance(airport, loc, MILES))
             );

      return builder.build();
   }

   @Override
   public ResultSet reportAirportMetrics(ReportContext context) {
      final int year = context.getYear();
      final ResultSetBuilder builder = 
         new ResultSetBuilder().addColumn("IATA", Types.VARCHAR)
                               .addColumn("Name", Types.VARCHAR)
                               .addColumn("Total", Types.INTEGER)
                               .addColumn("CancellationRate", Types.DOUBLE)
                               .addColumn("DiversionRate", Types.DOUBLE);

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
                builder.addRow(airport.getIATA(),
                               airport.getName(),
                               metrics.getTotalFlights(),
                               metrics.getCancellationRate(),
                               metrics.getDiversionRate());
             });

      return builder.build();
   }

   @Override
   public ResultSet reportAirportsWithHighestCancellationRate(ReportContext context) {
      final int year = context.getYear();
      final int limit = context.getLimit();
      final ResultSetBuilder builder = 
         new ResultSetBuilder().addColumn("IATA", Types.VARCHAR)
                               .addColumn("Name", Types.VARCHAR)
                               .addColumn("CancellationRate", Types.DOUBLE);

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
                builder.addRow(airport.getIATA(),
                               airport.getName(),
                               metrics.getCancellationRate());
             });
      return builder.build();
   }
}