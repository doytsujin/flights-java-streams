package airtraffic.reports.iterator;

import static airtraffic.reports.iterator.AccumulatorHelper.accumulate;
import static java.util.Comparator.reverseOrder;
import static java.util.Map.Entry.comparingByKey;
import static java.util.Map.Entry.comparingByValue;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import airtraffic.Flight;
import airtraffic.Plane;
import airtraffic.Plane.AircraftType;
import airtraffic.Plane.EngineType;
import airtraffic.annotations.IteratorStyle;
import airtraffic.PlaneAgeRange;
import airtraffic.PlaneModel;
import airtraffic.ReportContext;
import airtraffic.jdbc.ResultSetBuilder;
import airtraffic.reports.PlaneReports;

/**
 * Implementation of plane reports using iterator style that was common in
 * Java 7 and earlier versions.
 *
 * @author Tony Piazza <tony@piazzaconsulting.com>
 */
@IteratorStyle
public class IteratorPlaneReports implements PlaneReports {
   private static final List<PlaneAgeRange> AGE_RANGES =
      Arrays.asList(PlaneAgeRange.between( 0,  5),
                    PlaneAgeRange.between( 6,  10),
                    PlaneAgeRange.between(11,  20),
                    PlaneAgeRange.between(21,  30),
                    PlaneAgeRange.between(31,  40),
                    PlaneAgeRange.between(41,  50),
                    PlaneAgeRange.between(51, 100));

   public ResultSet reportTotalPlanesByManfacturer(ReportContext context) {
      final int limit = context.getLimit();
      final ResultSetBuilder builder = 
            new ResultSetBuilder().addColumn("Manufacturer", Types.VARCHAR)
                                  .addColumn("TotalPlanes", Types.INTEGER);

      Iterator<Plane> iterator = context.getRepository().getPlaneIterator();
      accumulate(iterator, comparingByValue(reverseOrder()), limit, 
         new CountingAccumulator<Plane, String>() {
            @Override public boolean filter(Plane plane) {
               return true;
            }
            @Override public String getKey(Plane plane) {
               return plane.getManufacturer();
            }
            @Override public void forEach(Entry<String, Long> entry) {
               builder.addRow(entry.getKey(), entry.getValue());
            }
         }
      );

      return builder.build();
   }

   public ResultSet reportTotalPlanesByYear(ReportContext context) {
      final int limit = context.getLimit();
      final ResultSetBuilder builder = 
            new ResultSetBuilder().addColumn("Year", Types.INTEGER)
                                  .addColumn("TotalPlanes", Types.INTEGER);

      Iterator<Plane> iterator = context.getRepository().getPlaneIterator();
      accumulate(iterator, comparingByValue(reverseOrder()), limit, 
         new CountingAccumulator<Plane, Integer>() {
            @Override public boolean filter(Plane plane) {
               return plane.getYear() > 0;
            }
            @Override public Integer getKey(Plane plane) {
               return plane.getYear();
            }
            @Override public void forEach(Entry<Integer, Long> entry) {
               builder.addRow(entry.getKey(), entry.getValue());
            }
         }
      );

      return builder.build();
   }

   public ResultSet reportTotalPlanesByAircraftType(ReportContext context) {
      final int limit = context.getLimit();
      final ResultSetBuilder builder = 
            new ResultSetBuilder().addColumn("Type", Types.JAVA_OBJECT)
                                  .addColumn("TotalPlanes", Types.INTEGER);

      Iterator<Plane> iterator = context.getRepository().getPlaneIterator();
      accumulate(iterator, comparingByValue(reverseOrder()), limit, 
         new CountingAccumulator<Plane, AircraftType>() {
            @Override public boolean filter(Plane plane) {
               return true;
            }
            @Override public AircraftType getKey(Plane plane) {
               return plane.getAircraftType();
            }
            @Override public void forEach(Entry<AircraftType, Long> entry) {
               builder.addRow(entry.getKey(), entry.getValue());
            }
         }
      );

      return builder.build();
   }

   public ResultSet reportTotalPlanesByEngineType(ReportContext context) {
      final int limit = context.getLimit();
      final ResultSetBuilder builder = 
            new ResultSetBuilder().addColumn("Type", Types.JAVA_OBJECT)
                                  .addColumn("TotalPlanes", Types.INTEGER);

      Iterator<Plane> iterator = context.getRepository().getPlaneIterator();
      accumulate(iterator, comparingByValue(reverseOrder()), limit, 
         new CountingAccumulator<Plane, EngineType>() {
            @Override public boolean filter(Plane plane) {
               return true;
            }
            @Override public EngineType getKey(Plane plane) {
               return plane.getEngineType();
            }
            @Override public void forEach(Entry<EngineType, Long> entry) {
               builder.addRow(entry.getKey(), entry.getValue());
            }
         }
      );

      return builder.build();
   }

   public ResultSet reportPlanesWithMostCancellations(ReportContext context) {
      final int year = context.getYear();
      final int limit = context.getLimit();
      final ResultSetBuilder builder = 
         new ResultSetBuilder().addColumn("TailNumber", Types.VARCHAR)
                               .addColumn("TotalCancellations", Types.INTEGER);

      Iterator<Flight> iterator = context.getRepository().getFlightIterator(year);
      accumulate(iterator, comparingByValue(reverseOrder()), limit, 
         new CountingAccumulator<Flight, String>() {
            @Override public boolean filter(Flight flight) {
               return flight.cancelled() && flight.validTailNumber();
            }
            @Override public String getKey(Flight flight) {
               return flight.getTailNumber();
            }
            @Override public void forEach(Entry<String, Long> entry) {
               builder.addRow(entry.getKey(), entry.getValue());
            }
         }
      );

      return builder.build();
   }

   public ResultSet reportMostFlightsByPlane(ReportContext context) {
      final int year = context.getYear();
      final int limit = context.getLimit();
      final ResultSetBuilder builder = 
         new ResultSetBuilder().addColumn("TailNumber", Types.VARCHAR)
                               .addColumn("Manufacturer", Types.VARCHAR)
                               .addColumn("ModelNumber", Types.VARCHAR)
                               .addColumn("TotalFlights", Types.INTEGER);

      Iterator<Flight> iterator = context.getRepository().getFlightIterator(year);
      accumulate(iterator, comparingByValue(reverseOrder()), limit, 
         new CountingAccumulator<Flight, Plane>() {
            @Override public boolean filter(Flight flight) {
               return flight.notCancelled() && flight.validTailNumber();
            }
            @Override public Plane getKey(Flight flight) {
               return flight.getPlane();
            }
            @Override public void forEach(Entry<Plane, Long> entry) {
               Plane plane = entry.getKey();
               builder.addRow(plane.getTailNumber(), 
                              plane.getManufacturer(),
                              plane.getModel().getModelNumber(),
                              entry.getValue());
            }
         }
      );

      return builder.build();
   }

   public ResultSet reportMostFlightsByPlaneModel(ReportContext context) {
      final int year = context.getYear();
      final int limit = context.getLimit();
      final ResultSetBuilder builder = 
         new ResultSetBuilder().addColumn("Manufacturer", Types.VARCHAR)
                               .addColumn("ModelNumber", Types.VARCHAR)
                               .addColumn("TotalFlights", Types.INTEGER)
                               .addColumn("DailyAverage", Types.FLOAT);

      Iterator<Flight> iterator = context.getRepository().getFlightIterator(year);
      accumulate(iterator, comparingByValue(reverseOrder()), limit, 
         new CountingAccumulator<Flight, PlaneModel>() {
            @Override public boolean filter(Flight flight) {
               return flight.notCancelled() && 
                      ! "UNKNOWN".equals(flight.getPlane().getManufacturer()); 
            }
            @Override public PlaneModel getKey(Flight flight) {
               return flight.getPlane().getModel();
            }
            @Override public void forEach(Entry<PlaneModel, Long> entry) {
               PlaneModel model = entry.getKey();
               Long count = entry.getValue();
               builder.addRow(model.getManufacturer(),
                              model.getModelNumber(),
                              count,
                              count.floatValue() / 365);
            }
         }
      );

      return builder.build();
   }

   public ResultSet reportTotalFlightsByPlaneManufacturer(ReportContext context) {
      final int year = context.getYear();
      final int limit = context.getLimit();
      final ResultSetBuilder builder = 
         new ResultSetBuilder().addColumn("Manufacturer", Types.VARCHAR)
                               .addColumn("TotalFlights", Types.INTEGER);

      Iterator<Flight> iterator = context.getRepository().getFlightIterator(year);
      accumulate(iterator, comparingByValue(reverseOrder()), limit, 
         new CountingAccumulator<Flight, String>() {
            @Override public boolean filter(Flight flight) {
               return flight.notCancelled();
            }
            @Override public String getKey(Flight flight) {
               return flight.getPlane().getManufacturer();
            }
            @Override public void forEach(Entry<String, Long> entry) {
               builder.addRow(entry.getKey(), entry.getValue());
            }
         }
      );

      return builder.build();
   }

   public ResultSet reportTotalFlightsByPlaneAgeRange(ReportContext context) {
      final int year = context.getYear();
      final int limit = context.getLimit();
      final ResultSetBuilder builder = 
            new ResultSetBuilder().addColumn("Range", Types.VARCHAR)
                                  .addColumn("TotalFlights", Types.INTEGER);

      Iterator<Flight> iterator = context.getRepository().getFlightIterator(year);
      accumulate(iterator, comparingByKey(), limit, 
         new CountingAccumulator<Flight, PlaneAgeRange>() {
            @Override public boolean filter(Flight flight) {
               return flight.notCancelled() && flight.getPlane().getYear() > 0;
            }
            @Override public PlaneAgeRange getKey(Flight flight) {
               int age = flight.getYear() - flight.getPlane().getYear();
               for(PlaneAgeRange range : AGE_RANGES) {
                  if(range.contains(age)) {
                     return range;
                  }
               }
               throw new IllegalStateException("No range for age of " + age);
            }
            @Override public void forEach(Entry<PlaneAgeRange, Long> entry) {
               builder.addRow(entry.getKey(), entry.getValue());
            }
         }
      );

      return builder.build();
   }

   public ResultSet reportTotalFlightsByAircraftType(ReportContext context) {
      final int year = context.getYear();
      final int limit = context.getLimit();
      final ResultSetBuilder builder = 
            new ResultSetBuilder().addColumn("Type", Types.VARCHAR)
                                  .addColumn("TotalFlights", Types.INTEGER);

      Iterator<Flight> iterator = context.getRepository().getFlightIterator(year);
      accumulate(iterator, comparingByValue(reverseOrder()), limit, 
         new CountingAccumulator<Flight, String>() {
            @Override public boolean filter(Flight flight) {
               return flight.notCancelled();
            }
            @Override public String getKey(Flight flight) {
               return flight.getPlane().getAircraftType().name();
            }
            @Override public void forEach(Entry<String, Long> entry) {
               builder.addRow(entry.getKey(), entry.getValue());
            }
         }
      );

      return builder.build();
   }

   public ResultSet reportTotalFlightsByEngineType(ReportContext context) {
      final int year = context.getYear();
      final int limit = context.getLimit();
      final ResultSetBuilder builder = 
            new ResultSetBuilder().addColumn("Type", Types.VARCHAR)
                                  .addColumn("TotalFlights", Types.INTEGER);

      Iterator<Flight> iterator = context.getRepository().getFlightIterator(year);
      accumulate(iterator, comparingByValue(reverseOrder()), limit, 
         new CountingAccumulator<Flight, String>() {
            @Override public boolean filter(Flight flight) {
               return flight.notCancelled();
            }
            @Override public String getKey(Flight flight) {
               return flight.getPlane().getEngineType().name();
            }
            @Override public void forEach(Entry<String, Long> entry) {
               builder.addRow(entry.getKey(), entry.getValue());
            }
         }
      );

      return builder.build();
   }
}