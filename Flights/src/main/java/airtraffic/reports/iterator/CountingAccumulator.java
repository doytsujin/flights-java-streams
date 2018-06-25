package airtraffic.reports.iterator;

/**
 * Implementation of MapAccumulator that counts the number of instances.
 *
 * @author tony@piazzaconsulting.com
 */
public abstract class CountingAccumulator<T, K> implements MapAccumulator<T, K, Long> {
   @Override
   public Long initializeValue(T source) {
      return Long.valueOf(1);
   }
   @Override
   public Long updateValue(T source, Long value) {
      return Long.valueOf(value.longValue() + 1);
   }
}