package airtraffic.reports.stream;

import static java.util.Comparator.reverseOrder;
import static java.util.Map.Entry.comparingByKey;
import static java.util.Map.Entry.comparingByValue;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.Arrays;
import java.util.List;
import airtraffic.Flight;
import airtraffic.Plane;
import airtraffic.PlaneAgeRange;
import airtraffic.PlaneModel;
import airtraffic.ReportContext;
import airtraffic.annotations.StreamStyle;
import airtraffic.jdbc.ResultSetBuilder;
import airtraffic.reports.PlaneReports;

/**
 * Implementation of plane reports using streams style that was introduced
 * in Java 8.
 *
 * @author Tony Piazza <tony@piazzaconsulting.com>
 */
@StreamStyle
public class StreamPlaneReports implements PlaneReports {
   private static final List<PlaneAgeRange> AGE_RANGES =
      Arrays.asList(PlaneAgeRange.between(   0,  5),
                    PlaneAgeRange.between(   6,  10),
                    PlaneAgeRange.between(  11,  20),
                    PlaneAgeRange.between(  21,  30),
                    PlaneAgeRange.between(  31,  40),
                    PlaneAgeRange.between(  41,  50),
                    PlaneAgeRange.between(  51, 100));

   @Override
   public ResultSet reportTotalPlanesByManfacturer(ReportContext context) {
      final int limit = context.getLimit();
      final ResultSetBuilder builder = 
         new ResultSetBuilder().addColumn("Manufacturer", Types.VARCHAR)
                               .addColumn("TotalPlanes", Types.INTEGER);

      context.getRepository()
             .getPlaneStream()
             .collect(groupingBy(Plane::getManufacturer, counting()))
             .entrySet()
             .stream()
             .sorted(comparingByValue(reverseOrder()))
             .limit(limit)
             .forEach(entry -> 
                builder.addRow(entry.getKey(), entry.getValue())
             );

      return builder.build();
   }

   @Override
   public ResultSet reportTotalPlanesByYear(ReportContext context) {
      final int limit = context.getLimit();
      final ResultSetBuilder builder = 
         new ResultSetBuilder().addColumn("Year", Types.INTEGER)
                               .addColumn("TotalPlanes", Types.INTEGER);

      context.getRepository()
             .getPlaneStream()
             .filter(plane -> plane.getYear() > 0)
             .collect(groupingBy(Plane::getYear, counting()))
             .entrySet()
             .stream()
             .sorted(comparingByKey(reverseOrder()))
             .limit(limit)
             .forEach(entry ->
                builder.addRow(entry.getKey(), entry.getValue())
             );

      return builder.build();
   }

   @Override
   public ResultSet reportTotalPlanesByAircraftType(ReportContext context) {
      final int limit = context.getLimit();
      final ResultSetBuilder builder = 
         new ResultSetBuilder().addColumn("Type", Types.JAVA_OBJECT)
                               .addColumn("TotalPlanes", Types.INTEGER);

      context.getRepository()
             .getPlaneStream()
             .collect(groupingBy(Plane::getAircraftType, counting()))
             .entrySet()
             .stream()
             .sorted(comparingByValue(reverseOrder()))
             .limit(limit)
             .forEach(entry -> 
                builder.addRow(entry.getKey(), entry.getValue())
             );

      return builder.build();
   }

   @Override
   public ResultSet reportTotalPlanesByEngineType(ReportContext context) {
      final int limit = context.getLimit();
      final ResultSetBuilder builder = 
         new ResultSetBuilder().addColumn("Type", Types.JAVA_OBJECT)
                               .addColumn("TotalPlanes", Types.INTEGER);

      context.getRepository()
             .getPlaneStream()
             .collect(groupingBy(Plane::getEngineType, counting()))
             .entrySet()
             .stream()
             .sorted(comparingByValue(reverseOrder()))
             .limit(limit)
             .forEach(entry -> 
                builder.addRow(entry.getKey(), entry.getValue())
             );

      return builder.build();
   }

   @Override
   public ResultSet reportPlanesWithMostCancellations(ReportContext context) {
      final int year = context.getYear();
      final int limit = context.getLimit();
      final ResultSetBuilder builder = 
         new ResultSetBuilder().addColumn("TailNumber", Types.VARCHAR)
                               .addColumn("TotalCancellations", Types.INTEGER);

      context.getRepository()
             .getFlightStream(year)
             .filter(flight -> flight.cancelled() && flight.validTailNumber())
             .collect(groupingBy(Flight::getTailNumber, counting()))
             .entrySet()
             .stream()
             .sorted(comparingByValue(reverseOrder()))
             .limit(limit)
             .forEach(entry -> 
                builder.addRow(entry.getKey(), entry.getValue())
             );

      return builder.build();
   }

   @Override
   public ResultSet reportMostFlightsByPlane(ReportContext context) {
      final int year = context.getYear();
      final int limit = context.getLimit();
      final ResultSetBuilder builder = 
         new ResultSetBuilder().addColumn("TailNumber", Types.VARCHAR)
                               .addColumn("Manufacturer", Types.VARCHAR)
                               .addColumn("ModelNumber", Types.VARCHAR)
                               .addColumn("TotalFlights", Types.INTEGER);

      context.getRepository()
             .getFlightStream(year)
             .parallel()
             .filter(flight -> flight.notCancelled() && 
                               flight.validTailNumber())
             .collect(groupingBy(Flight::getPlane, counting()))
             .entrySet()
             .stream()
             .sorted(comparingByValue(reverseOrder()))
             .limit(limit)
             .forEach(entry -> {
                Plane plane = entry.getKey();
                builder.addRow(plane.getTailNumber(), 
                               plane.getManufacturer(),
                               plane.getModel().getModelNumber(),
                               entry.getValue());
             });

      return builder.build();
   }

   @Override
   public ResultSet reportMostFlightsByPlaneModel(ReportContext context) {
      final int year = context.getYear();
      final int limit = context.getLimit();
      final ResultSetBuilder builder = 
         new ResultSetBuilder().addColumn("Manufacturer", Types.VARCHAR)
                               .addColumn("ModelNumber", Types.VARCHAR)
                               .addColumn("TotalFlights", Types.INTEGER)
                               .addColumn("DailyAverage", Types.FLOAT);

      context.getRepository()
             .getFlightStream(year)
             .parallel()
             .filter(flight -> 
                flight.notCancelled() && 
                ! "UNKNOWN".equals(flight.getPlane().getManufacturer())
             )
             .map(flight -> flight.getPlane())
             .collect(groupingBy(Plane::getModel, counting()))
             .entrySet()
             .stream()
             .sorted(comparingByValue(reverseOrder()))
             .limit(limit)
             .forEach(entry -> {
                PlaneModel model = entry.getKey();
                Long count = entry.getValue();
                builder.addRow(model.getManufacturer(),
                               model.getModelNumber(),
                               count,
                               count.floatValue() / 365);
             });

      return builder.build();
   }

   @Override
   public ResultSet reportTotalFlightsByPlaneManufacturer(ReportContext context) {
      final int year = context.getYear();
      final int limit = context.getLimit();
      final ResultSetBuilder builder = 
         new ResultSetBuilder().addColumn("Manufacturer", Types.VARCHAR)
                               .addColumn("TotalFlights", Types.INTEGER);

      context.getRepository()
             .getFlightStream(year)
             .filter(flight -> flight.notCancelled())
             .map(flight -> flight.getPlane())
             .collect(groupingBy(Plane::getManufacturer, counting()))
             .entrySet()
             .stream()
             .sorted(comparingByValue(reverseOrder()))
             .limit(limit)
             .forEach(entry ->
                builder.addRow(entry.getKey(), entry.getValue())
             );

      return builder.build();
   }

   @Override
   public ResultSet reportTotalFlightsByPlaneAgeRange(ReportContext context) {
      final int year = context.getYear();
      final int limit = context.getLimit();
      final ResultSetBuilder builder = 
         new ResultSetBuilder().addColumn("Range", Types.JAVA_OBJECT)
                               .addColumn("TotalFlights", Types.INTEGER);

      context.getRepository()
             .getFlightStream(year)
             .parallel()
             .filter(flight -> flight.notCancelled() && 
                               flight.getPlane().getYear() > 0)
             .collect(groupingBy(PlaneAgeRange.classifier(AGE_RANGES), 
                                 counting()))
             .entrySet()
             .stream()
             .sorted(comparingByKey())
             .limit(limit)
             .forEach(entry ->
                builder.addRow(entry.getKey(), entry.getValue())
             );

      return builder.build();
   }

   @Override
   public ResultSet reportTotalFlightsByAircraftType(ReportContext context) {
      final int year = context.getYear();
      final int limit = context.getLimit();
      final ResultSetBuilder builder = 
         new ResultSetBuilder().addColumn("Type", Types.VARCHAR)
                               .addColumn("TotalFlights", Types.INTEGER);

      context.getRepository()
             .getFlightStream(year)
             .filter(flight -> flight.notCancelled())
             .map(flight -> flight.getPlane())
             .collect(groupingBy(Plane::getAircraftType, counting()))
             .entrySet()
             .stream()
             .sorted(comparingByValue(reverseOrder()))
             .limit(limit)
             .forEach(entry ->
                builder.addRow(entry.getKey(), entry.getValue())
             );

      return builder.build();
   }

   @Override
   public ResultSet reportTotalFlightsByEngineType(ReportContext context) {
      final int year = context.getYear();
      final int limit = context.getLimit();
      final ResultSetBuilder builder = 
         new ResultSetBuilder().addColumn("Type", Types.VARCHAR)
                               .addColumn("TotalFlights", Types.INTEGER);

      context.getRepository()
             .getFlightStream(year)
             .filter(flight -> flight.notCancelled())
             .map(flight -> flight.getPlane())
             .collect(groupingBy(Plane::getEngineType, counting()))
             .entrySet()
             .stream()
             .sorted(comparingByValue(reverseOrder()))
             .limit(limit)
             .forEach(entry -> 
                builder.addRow(entry.getKey(), entry.getValue())
             );

      return builder.build();
   }
}