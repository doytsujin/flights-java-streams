package airtraffic.iterator;

import static airtraffic.iterator.AccumulatorHelper.accumulate;
import static java.util.Comparator.reverseOrder;
import static java.util.Map.Entry.comparingByKey;
import static java.util.Map.Entry.comparingByValue;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.atomic.LongAccumulator;

import airtraffic.AbstractReportsProvider;
import airtraffic.Flight;
import airtraffic.Plane;
import airtraffic.Plane.AircraftType;
import airtraffic.Plane.EngineType;
import airtraffic.PlaneAgeRange;
import airtraffic.PlaneModel;
import airtraffic.PlaneReports;
import airtraffic.Repository;

/**
 * Generate various airplane statistics using Java iterators.
 *
 * @author tony@piazzaconsulting.com
 */
public class IteratorPlaneReports extends AbstractReportsProvider implements PlaneReports {
   private static final List<PlaneAgeRange> AGE_RANGES =
      Arrays.asList(PlaneAgeRange.between( 0,  5),
                    PlaneAgeRange.between( 6,  10),
                    PlaneAgeRange.between(11,  20),
                    PlaneAgeRange.between(21,  30),
                    PlaneAgeRange.between(31,  40),
                    PlaneAgeRange.between(41,  50),
                    PlaneAgeRange.between(51, 100));

   public void reportTotalPlanesByManfacturer(Repository repository) {
      println("Manufacturer\t\t\tCount");
      println("---------------------------------------");

      Iterator<Plane> iterator = repository.getPlaneIterator();
      accumulate(iterator, comparingByValue(reverseOrder()), MAX_LIMIT, 
         new CountingAccumulator<Plane, String>() {
            @Override public boolean filter(Plane plane) {
               return true;
            }
            @Override public String getKey(Plane plane) {
               return plane.getManufacturer();
            }
            @Override public void forEach(Entry<String, Long> entry) {
               printf("%-25s\t%5d\n", entry.getKey(), entry.getValue());
            }
         }
      );
   }

   public void reportTotalPlanesByYear(Repository repository) {
      println("Year\tCount");
      println("------------------");

      Iterator<Plane> iterator = repository.getPlaneIterator();
      accumulate(iterator, comparingByValue(reverseOrder()), MAX_LIMIT, 
         new CountingAccumulator<Plane, Integer>() {
            @Override public boolean filter(Plane plane) {
               return plane.getYear() > 0;
            }
            @Override public Integer getKey(Plane plane) {
               return plane.getYear();
            }
            @Override public void forEach(Entry<Integer, Long> entry) {
               printf("%4d\t%5d\n", entry.getKey(), entry.getValue());
            }
         }
      );
   }

   public void reportTotalPlanesByAircraftType(Repository repository) {
      println("Aircraft Type\t\t\tCount");
      println("---------------------------------------");

      Iterator<Plane> iterator = repository.getPlaneIterator();
      accumulate(iterator, comparingByValue(reverseOrder()), MAX_LIMIT, 
         new CountingAccumulator<Plane, AircraftType>() {
            @Override public boolean filter(Plane plane) {
               return true;
            }
            @Override public AircraftType getKey(Plane plane) {
               return plane.getAircraftType();
            }
            @Override public void forEach(Entry<AircraftType, Long> entry) {
               printf("%-25s\t%5d\n", entry.getKey(), entry.getValue());
            }
         }
      );
   }

   public void reportTotalPlanesByEngineType(Repository repository) {
      println("Engine Type\t\t\tCount");
      println("---------------------------------------");

      Iterator<Plane> iterator = repository.getPlaneIterator();
      accumulate(iterator, comparingByValue(reverseOrder()), MAX_LIMIT, 
         new CountingAccumulator<Plane, EngineType>() {
            @Override public boolean filter(Plane plane) {
               return true;
            }
            @Override public EngineType getKey(Plane plane) {
               return plane.getEngineType();
            }
            @Override public void forEach(Entry<EngineType, Long> entry) {
               printf("%-25s\t%5d\n", entry.getKey(), entry.getValue());
            }
         }
      );
   }

   public void reportPlanesWithMostCancellations(Repository repository) {
      final int year = selectYear(repository);
      final int limit = readLimit(10, 1, 100);

      println("Tail #\t\tCount");
      println("-----------------------");

      Iterator<Flight> iterator = repository.getFlightIterator(year);
      accumulate(iterator, comparingByValue(reverseOrder()), limit, 
         new CountingAccumulator<Flight, String>() {
            @Override public boolean filter(Flight flight) {
               return flight.cancelled() && flight.validTailNumber();
            }
            @Override public String getKey(Flight flight) {
               return flight.getTailNumber();
            }
            @Override public void forEach(Entry<String, Long> entry) {
               printf("%-8s\t%,6d\n", entry.getKey(), entry.getValue());
            }
         }
      );
   }

   public void reportMostFlightsByPlane(Repository repository) {
      final int year = selectYear(repository);
      final int limit = readLimit(10, 1, 100);

      println("Tail #\t  Manufacturer\t\tModel #\t\tCount");
      println(repeat("-", 67));

      Iterator<Flight> iterator = repository.getFlightIterator(year);
      accumulate(iterator, comparingByValue(reverseOrder()), limit, 
         new CountingAccumulator<Flight, Plane>() {
            @Override public boolean filter(Flight flight) {
               return flight.cancelled() && flight.validTailNumber();
            }
            @Override public Plane getKey(Flight flight) {
               return flight.getPlane();
            }
            @Override public void forEach(Entry<Plane, Long> entry) {
               Plane plane = entry.getKey();
               printf("%-8s  %-20s  %-10s  %,10d\n", 
                      plane.getTailNumber(), 
                      left(plane.getManufacturer(), 20),
                      left(plane.getModel().getModelNumber(), 10),
                      entry.getValue());
            }
         }
      );
   }

   public void reportMostFlightsByPlaneModel(Repository repository) {
      final int year = selectYear(repository);
      final int limit = readLimit(10, 1, 100);

      println("Manufacturer\t\t\tModel #\t\t\t  Count\t\tDaily Avg");
      println(repeat("-", 82));

      Iterator<Flight> iterator = repository.getFlightIterator(year);
      accumulate(iterator, comparingByValue(reverseOrder()), limit, 
         new CountingAccumulator<Flight, PlaneModel>() {
            @Override public boolean filter(Flight flight) {
               return flight.cancelled();
            }
            @Override public PlaneModel getKey(Flight flight) {
               return flight.getPlane().getModel();
            }
            @Override public void forEach(Entry<PlaneModel, Long> entry) {
               PlaneModel model = entry.getKey();
               Long count = entry.getValue();
               printf("%-25s\t%-20s\t%,10d\t%8.1f",
                      model.getManufacturer(),
                      model.getModelNumber(),
                      count,
                      count.floatValue() / 365
               );
            }
         }
      );
   }

   public void reportTotalFlightsByPlaneManufacturer(Repository repository) {
      final int year = selectYear(repository);

      println("Manufacturer\t\t\t Count");
      println("-------------------------------------------");

      Iterator<Flight> iterator = repository.getFlightIterator(year);
      accumulate(iterator, comparingByValue(reverseOrder()), MAX_LIMIT, 
         new CountingAccumulator<Flight, String>() {
            @Override public boolean filter(Flight flight) {
               return flight.notCancelled();
            }
            @Override public String getKey(Flight flight) {
               return flight.getPlane().getManufacturer();
            }
            @Override public void forEach(Entry<String, Long> entry) {
               printf("%-25s\t%,10d\n", entry.getKey(), entry.getValue());
            }
         }
      );
   }

   public void reportTotalFlightsByPlaneAgeRange(Repository repository) {
      final int year = selectYear(repository);

      println("Age Range\tCount");
      println(repeat("-", 27));

      Iterator<Flight> iterator = repository.getFlightIterator(year);
      final LongAccumulator total = new LongAccumulator((x, y) -> x + y, 0);
      accumulate(iterator, comparingByKey(), MAX_LIMIT, 
         new MapAccumulator<Flight, PlaneAgeRange, Long>() {
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
            @Override public Long initializeValue(Flight source) {
               return Long.valueOf(1);
            }
            @Override public Long updateValue(Flight source, Long value) {
               return Long.valueOf(value.longValue() + 1);
            }
            @Override public void forEach(Entry<PlaneAgeRange, Long> entry) {
               long value = entry.getValue().longValue();
               total.accumulate(value);
               printf("%-10s\t%,10d\n", entry.getKey(), value);
            }
         }
      );

      println(repeat("-", 27));
      printf("Total\t       %,11d\n", total.longValue());
   }

   public void reportTotalFlightsByAircraftType(Repository repository) {
      final int year = selectYear(repository);

      println("Aircraft Type\t\t\tCount");
      println("-------------------------------------------");

      Iterator<Flight> iterator = repository.getFlightIterator(year);
      accumulate(iterator, comparingByValue(reverseOrder()), MAX_LIMIT, 
         new CountingAccumulator<Flight, String>() {
            @Override public boolean filter(Flight flight) {
               return flight.notCancelled();
            }
            @Override public String getKey(Flight flight) {
               return flight.getPlane().getAircraftType().name();
            }
            @Override public void forEach(Entry<String, Long> entry) {
               printf("%-25s\t%,10d\n", entry.getKey(), entry.getValue());
            }
         }
      );
   }

   public void reportTotalFlightsByEngineType(Repository repository) {
      final int year = selectYear(repository);

      println("Engine Type\t\t\tCount");
      println("-------------------------------------------");

      Iterator<Flight> iterator = repository.getFlightIterator(year);
      accumulate(iterator, comparingByValue(reverseOrder()), MAX_LIMIT, 
         new CountingAccumulator<Flight, String>() {
            @Override public boolean filter(Flight flight) {
               return flight.notCancelled();
            }
            @Override public String getKey(Flight flight) {
               return flight.getPlane().getEngineType().name();
            }
            @Override public void forEach(Entry<String, Long> entry) {
               printf("%-25s\t%,10d\n", entry.getKey(), entry.getValue());
            }
         }
      );
   }
}