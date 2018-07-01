package airtraffic.reports.iterator;

import static airtraffic.GeoHelper.distanceFromReferenceComparator;
import static airtraffic.GeoHelper.getDistance;
import static airtraffic.GeoLocation.Units.MILES;
import static airtraffic.metrics.FlightBasedMetrics.highestCancellationRateComparator;
import static airtraffic.reports.iterator.AccumulatorHelper.accumulate;
import static java.util.Comparator.naturalOrder;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import airtraffic.Airport;
import airtraffic.Flight;
import airtraffic.GeoLocation;
import airtraffic.ReportContext;
import airtraffic.annotations.IteratorStyle;
import airtraffic.jdbc.ResultSetBuilder;
import airtraffic.metrics.AirportMetrics;
import airtraffic.reports.AirportReports;


@IteratorStyle
public class IteratorAirportReports implements AirportReports {
   @Override
   public ResultSet reportAirportsForState(ReportContext context) {
      final String state = context.getState();
      final int limit = context.getLimit();
      final ResultSetBuilder builder = 
         new ResultSetBuilder().addColumn("IATA", Types.VARCHAR)
                               .addColumn("Name", Types.VARCHAR)
                               .addColumn("City", Types.VARCHAR);

      Iterator<Airport> iterator = context.getRepository().getAirportIterator();
      accumulate(iterator, naturalOrder(), limit, 
         new ListAccumulator<Airport>() {
            @Override public boolean filter(Airport airport) {
               return airport.getState().equals(state);
            }
            @Override public void forEach(Airport airport) {
               builder.addRow(airport.getIATA(), 
                              airport.getName(), 
                              airport.getCity());
            }
         }
      );

      return builder.build();
   }

   @Override
   public ResultSet reportAirportsNearLocation(ReportContext context) {
      final GeoLocation loc = context.getLocation();
      final int distance = context.getDistance();
      final int limit = context.getLimit();
      final ResultSetBuilder builder = 
         new ResultSetBuilder().addColumn("IATA", Types.VARCHAR)
                               .addColumn("Name", Types.VARCHAR)
                               .addColumn("State", Types.VARCHAR)
                               .addColumn("City", Types.VARCHAR)
                               .addColumn("Distance", Types.VARCHAR);

      Iterator<Airport> iterator = context.getRepository().getAirportIterator();
      accumulate(iterator, distanceFromReferenceComparator(loc, MILES), limit, 
         new ListAccumulator<Airport>() {
            @Override public boolean filter(Airport airport) {
               return getDistance(airport, loc, MILES) <= distance;
            }
            @Override public void forEach(Airport airport) {
               builder.addRow(airport.getIATA(), 
                              airport.getName(), 
                              airport.getState(), 
                              airport.getCity(), 
                              getDistance(airport, loc, MILES));
            }
         }
      );

      return builder.build();
   }

   @Override
   public ResultSet reportAirportMetrics(ReportContext context) {
      final int year = context.getYear();
      final ResultSetBuilder builder = 
         new ResultSetBuilder().addColumn("IATA", Types.VARCHAR)
                               .addColumn("Name", Types.VARCHAR)
                               .addColumn("TotalFlights", Types.INTEGER)
                               .addColumn("CancellationRate", Types.DOUBLE)
                               .addColumn("DiversionRate", Types.DOUBLE);

      Iterator<Flight> iterator = context.getRepository().getFlightIterator(year);
      Map<Airport, AirportMetrics> map = new HashMap<>();
      while(iterator.hasNext()) {
         Flight flight = iterator.next();
         Airport origin = flight.getOrigin();
         AirportMetrics metrics1 = map.get(origin);
         if(metrics1 == null) {
            metrics1 = new AirportMetrics(origin);
            map.put(origin, metrics1);
         }
         metrics1.addFlight(flight);
         Airport destination = flight.getDestination();
         AirportMetrics metrics2 = map.get(destination);
         if(metrics2 == null) {
            metrics2 = new AirportMetrics(destination);
            map.put(destination, metrics2);
         }
         metrics2.addFlight(flight);
      }
      SortedSet<AirportMetrics> set = 
         new TreeSet<>(new Comparator<AirportMetrics>() {
            @Override public int compare(AirportMetrics m1, AirportMetrics m2) {
               return m1.getSubject().compareTo(m2.getSubject());
            }
         });
      set.addAll(map.values());
      for(AirportMetrics metrics : set) {
         Airport airport = metrics.getSubject();
         builder.addRow(airport.getIATA(),
                        airport.getName(),
                        metrics.getTotalFlights(),
                        metrics.getCancellationRate(),
                        metrics.getDiversionRate());
      }

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

      Iterator<Flight> iterator = context.getRepository().getFlightIterator(year);
      Map<Airport, AirportMetrics> map = new HashMap<>();
      while(iterator.hasNext()) {
         Flight flight = iterator.next();
         Airport origin = flight.getOrigin();
         AirportMetrics metrics1 = map.get(origin);
         if(metrics1 == null) {
            metrics1 = new AirportMetrics(origin);
            map.put(origin, metrics1);
         }
         metrics1.addFlight(flight);
         Airport destination = flight.getDestination();
         AirportMetrics metrics2 = map.get(destination);
         if(metrics2 == null) {
            metrics2 = new AirportMetrics(destination);
            map.put(destination, metrics2);
         }
         metrics2.addFlight(flight);
      }
      SortedSet<AirportMetrics> set = 
         new TreeSet<>(highestCancellationRateComparator());
      set.addAll(map.values());
      int count = 0;
      for(AirportMetrics metrics : set) {
         Airport airport = metrics.getSubject();
         builder.addRow(airport.getIATA(),
                        airport.getName(),
                        metrics.getCancellationRate());
         if(++count >= limit) {
            break;
         }
      }

      return builder.build();
   }
}