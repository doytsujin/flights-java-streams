package airtraffic.stream;

import static java.util.Comparator.reverseOrder;
import static java.util.Map.Entry.comparingByKey;
import static java.util.Map.Entry.comparingByValue;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static org.apache.commons.lang3.StringUtils.left;

import java.util.Arrays;
import java.util.List;

import airtraffic.Flight;
import airtraffic.Plane;
import airtraffic.PlaneAgeRange;
import airtraffic.PlaneModel;
import airtraffic.PlaneReports;
import airtraffic.ReportContext;

/**
 * Generate various airplane statistics using Java 8 streams.
 *
 * @author tony@piazzaconsulting.com
 */
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
   public void reportTotalPlanesByManfacturer(ReportContext context) {
      context.getRepository()
             .getPlaneStream()
             .collect(groupingBy(Plane::getManufacturer, counting()))
             .entrySet()
             .stream()
             .sorted(comparingByValue(reverseOrder()))
             .forEach(e -> context.getTerminal()
                                  .printf("%-25s\t%5d\n", 
                                          e.getKey(), 
                                          e.getValue()));
   }

   @Override
   public void reportTotalPlanesByYear(ReportContext context) {
      context.getRepository().getPlaneStream()
             .filter(p -> p.getYear() > 0)
             .collect(groupingBy(Plane::getYear, counting()))
             .entrySet()
             .stream()
             .sorted(comparingByKey(reverseOrder()))
             .forEach(e -> context.getTerminal()
                                  .printf("%4d\t%5d\n", 
                                          e.getKey(), 
                                          e.getValue()));
   }

   @Override
   public void reportTotalPlanesByAircraftType(ReportContext context) {
      context.getRepository().getPlaneStream()
             .collect(groupingBy(Plane::getAircraftType, counting()))
             .entrySet()
             .stream()
             .sorted(comparingByValue(reverseOrder()))
             .forEach(e -> context.getTerminal()
                                  .printf("%-25s\t%5d\n", 
                                          e.getKey(), 
                                          e.getValue()));
   }

   @Override
   public void reportTotalPlanesByEngineType(ReportContext context) {
      context.getRepository()
             .getPlaneStream()
             .collect(groupingBy(Plane::getEngineType, counting()))
             .entrySet()
             .stream()
             .sorted(comparingByValue(reverseOrder()))
             .forEach(e -> context.getTerminal()
                                  .printf("%-25s\t%5d\n", 
                                          e.getKey(), 
                                          e.getValue()));
   }

   @Override
   public void reportPlanesWithMostCancellations(ReportContext context) {
      final int year = context.getYear();
      final int limit = context.getLimit();

      context.getRepository()
             .getFlightStream(year)
             .filter(f -> f.cancelled() && f.validTailNumber())
             .collect(groupingBy(Flight::getTailNumber, counting()))
             .entrySet()
             .stream()
             .sorted(comparingByValue(reverseOrder()))
             .limit(limit)
             .forEach(e -> context.getTerminal()
                                  .printf("%-8s\t%,6d\n", 
                                          e.getKey(), 
                                          e.getValue()));
   }

   @Override
   public void reportMostFlightsByPlane(ReportContext context) {
      final int year = context.getYear();
      final int limit = context.getLimit();

      context.getRepository()
             .getFlightStream(year)
             .filter(f -> f.notCancelled() && f.validTailNumber())
             .collect(groupingBy(Flight::getPlane, counting()))
             .entrySet()
             .stream()
             .sorted(comparingByValue(reverseOrder()))
             .limit(limit)
             .forEach(e -> {
                Plane plane = e.getKey();
                context.getTerminal()
                       .printf("%-8s  %-20s  %-10s  %,10d\n", 
                               plane.getTailNumber(), 
                               left(plane.getManufacturer(), 20),
                               left(plane.getModel().getModelNumber(), 10),
                               e.getValue());
             });
   }

   @Override
   public void reportMostFlightsByPlaneModel(ReportContext context) {
      final int year = context.getYear();
      final int limit = context.getLimit();

      context.getRepository()
             .getFlightStream(year)
             .filter(f -> f.notCancelled())
             .map(p -> p.getPlane())
             .collect(groupingBy(Plane::getModel, counting()))
             .entrySet()
             .stream()
             .sorted(comparingByValue(reverseOrder()))
             .limit(limit)
             .forEach(e -> {
                PlaneModel model = e.getKey();
                Long count = e.getValue();
                context.getTerminal()
                       .printf("%-25s\t%-20s\t%,10d\t%8.1f",
                               model.getManufacturer(),
                               model.getModelNumber(),
                               count,
                               count.floatValue() / 365);
             });
   }

   @Override
   public void reportTotalFlightsByPlaneManufacturer(ReportContext context) {
      final int year = context.getYear();

      context.getRepository()
             .getFlightStream(year)
             .filter(f -> f.notCancelled())
             .map(f -> f.getPlane())
             .collect(groupingBy(Plane::getManufacturer, counting()))
             .entrySet()
             .stream()
             .sorted(comparingByValue(reverseOrder()))
             .forEach(e -> context.getTerminal()
                                  .printf("%-25s\t%,10d\n", 
                                          e.getKey(), 
                                          e.getValue()));
   }

   @Override
   public void reportTotalFlightsByPlaneAgeRange(ReportContext context) {
      final int year = context.getYear();

      context.getRepository()
             .getFlightStream(year)
             .filter(f -> f.notCancelled() && 
                          f.getPlane().getYear() > 0)
             .collect(groupingBy(PlaneAgeRange.classifier(AGE_RANGES), 
                                 counting()))
             .entrySet()
             .stream()
             .sorted(comparingByKey())
             .forEach(e -> context.getTerminal()
                                  .printf("%-10s\t%,10d\n", 
                                          e.getKey(), 
                                          e.getValue()));
   }

   @Override
   public void reportTotalFlightsByAircraftType(ReportContext context) {
      final int year = context.getYear();

      context.getRepository()
             .getFlightStream(year)
             .filter(f -> f.notCancelled())
             .map(f -> f.getPlane())
             .collect(groupingBy(Plane::getAircraftType, counting()))
             .entrySet()
             .stream()
             .sorted(comparingByValue(reverseOrder()))
             .forEach(e -> context.getTerminal()
                                  .printf("%-25s\t%,10d\n", 
                                          e.getKey(), 
                                          e.getValue()));
   }

   @Override
   public void reportTotalFlightsByEngineType(ReportContext context) {
      final int year = context.getYear();

      context.getRepository().getFlightStream(year)
             .filter(f -> f.notCancelled())
             .map(f -> f.getPlane())
             .collect(groupingBy(Plane::getEngineType, counting()))
             .entrySet()
             .stream()
             .sorted(comparingByValue(reverseOrder()))
             .forEach(e -> context.getTerminal()
                                  .printf("%-25s\t%,10d\n", 
                                          e.getKey(), 
                                          e.getValue()));
   }
}