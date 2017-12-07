package airtraffic.iterator;

import static airtraffic.iterator.AccumulatorHelper.accumulate;
import static java.util.Comparator.reverseOrder;
import static java.util.Map.Entry.comparingByKey;
import static java.util.Map.Entry.comparingByValue;
import static org.apache.commons.lang3.StringUtils.left;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import airtraffic.Flight;
import airtraffic.Plane;
import airtraffic.Plane.AircraftType;
import airtraffic.Plane.EngineType;
import airtraffic.PlaneAgeRange;
import airtraffic.PlaneModel;
import airtraffic.PlaneReports;
import airtraffic.ReportContext;

/**
 * Generate various airplane statistics using Java iterators.
 *
 * @author tony@piazzaconsulting.com
 */
public class IteratorPlaneReports implements PlaneReports {
   private static final List<PlaneAgeRange> AGE_RANGES =
      Arrays.asList(PlaneAgeRange.between( 0,  5),
                    PlaneAgeRange.between( 6,  10),
                    PlaneAgeRange.between(11,  20),
                    PlaneAgeRange.between(21,  30),
                    PlaneAgeRange.between(31,  40),
                    PlaneAgeRange.between(41,  50),
                    PlaneAgeRange.between(51, 100));

   public void reportTotalPlanesByManfacturer(ReportContext context) {
      final int limit = context.getLimit();

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
               context.getTerminal()
                      .printf("%-25s\t%5d\n", entry.getKey(), entry.getValue());
            }
         }
      );
   }

   public void reportTotalPlanesByYear(ReportContext context) {
      final int limit = context.getLimit();

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
               context.getTerminal()
                      .printf("%4d\t%5d\n", entry.getKey(), entry.getValue());
            }
         }
      );
   }

   public void reportTotalPlanesByAircraftType(ReportContext context) {
      final int limit = context.getLimit();

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
               context.getTerminal()
                      .printf("%-25s\t%5d\n", entry.getKey(), entry.getValue());
            }
         }
      );
   }

   public void reportTotalPlanesByEngineType(ReportContext context) {
      final int limit = context.getLimit();

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
               context.getTerminal()
                      .printf("%-25s\t%5d\n", entry.getKey(), entry.getValue());
            }
         }
      );
   }

   public void reportPlanesWithMostCancellations(ReportContext context) {
      final int year = context.getYear();
      final int limit = context.getLimit();

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
               context.getTerminal()
                      .printf("%-8s\t%,6d\n", entry.getKey(), entry.getValue());
            }
         }
      );
   }

   public void reportMostFlightsByPlane(ReportContext context) {
      final int year = context.getYear();
      final int limit = context.getLimit();

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
               context.getTerminal()
                      .printf("%-8s  %-20s  %-10s  %,10d\n", 
                              plane.getTailNumber(), 
                              left(plane.getManufacturer(), 20),
                              left(plane.getModel().getModelNumber(), 10),
                              entry.getValue());
            }
         }
      );
   }

   public void reportMostFlightsByPlaneModel(ReportContext context) {
      final int year = context.getYear();
      final int limit = context.getLimit();

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
               context.getTerminal()
                      .printf("%-25s\t%-20s\t%,10d\t%8.1f",
                              model.getManufacturer(),
                              model.getModelNumber(),
                              count,
                              count.floatValue() / 365);
            }
         }
      );
   }

   public void reportTotalFlightsByPlaneManufacturer(ReportContext context) {
      final int year = context.getYear();
      final int limit = context.getLimit();

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
               context.getTerminal()
                      .printf("%-25s\t%,10d\n", 
                              entry.getKey(), 
                              entry.getValue());
            }
         }
      );
   }

   public void reportTotalFlightsByPlaneAgeRange(ReportContext context) {
      final int year = context.getYear();
      final int limit = context.getLimit();

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
               context.getTerminal()
                      .printf("%-10s\t%,10d\n", 
                              entry.getKey(), 
                              entry.getValue());
            }
         }
      );
   }

   public void reportTotalFlightsByAircraftType(ReportContext context) {
      final int year = context.getYear();
      final int limit = context.getLimit();

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
               context.getTerminal()
                      .printf("%-25s\t%,10d\n", 
                              entry.getKey(), 
                              entry.getValue());
            }
         }
      );
   }

   public void reportTotalFlightsByEngineType(ReportContext context) {
      final int year = context.getYear();
      final int limit = context.getLimit();

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
               context.getTerminal()
                      .printf("%-25s\t%,10d\n", 
                              entry.getKey(), 
                              entry.getValue());
            }
         }
      );
   }
}