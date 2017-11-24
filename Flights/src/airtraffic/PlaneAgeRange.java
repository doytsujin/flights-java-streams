package airtraffic;

import java.util.List;
import java.util.function.Function;

import org.apache.commons.lang3.Range;

public class PlaneAgeRange implements Comparable<PlaneAgeRange> {
   private Range<Integer> range;

   private PlaneAgeRange(int start, int end) {
      this.range = Range.between(start, end);
   }

   public static PlaneAgeRange between(int start, int end) {
      return new PlaneAgeRange(start, end);
   }

   public boolean contains(int value) {
      return range.contains(value);
   }

   public static Function<Flight, PlaneAgeRange> classifier(List<PlaneAgeRange> ranges) {
      return f -> ranges.stream()
                        .filter(r -> r.contains(f.getYear() - f.getPlane().getYear()))
                        .findAny()
                        .get();
   }

   @Override
   public int compareTo(PlaneAgeRange other) {
      return this.range.getMaximum() - other.range.getMinimum(); 
   }

   @Override
   public String toString() {
      return String.format("%,d to %,d",range.getMinimum(), range.getMaximum());
   }

   @Override
   public boolean equals(Object other) {
      return range.equals(other);
   }

   @Override
   public int hashCode() {
      return range.hashCode();
   }
}