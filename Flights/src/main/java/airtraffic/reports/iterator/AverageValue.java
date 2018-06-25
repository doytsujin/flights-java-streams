package airtraffic.reports.iterator;

/**
 * Simple class that stores total and count values to compute average.
 *
 * @author tony@piazzaconsulting.com
 */
public class AverageValue implements Comparable<AverageValue> {
   private long total;
   private long count;

   public AverageValue(long amount) {
      add(amount);
   }

   public AverageValue add(long amount) {
      total += amount;
      ++count;
      return this;
   }

   public double getAverage() {
      return (double)total / (double)count;
   }

   @Override
   public int compareTo(AverageValue other) {
      return Double.compare(this.getAverage(), other.getAverage());
   }
}