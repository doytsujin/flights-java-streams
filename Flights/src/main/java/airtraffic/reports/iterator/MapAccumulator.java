package airtraffic.reports.iterator;

import java.util.Map.Entry;

/**
 * Interface that describes methods needed to support an accumulator based
 * on a Map.
 *
 * @author Tony Piazza <tony@piazzaconsulting.com>
 */
public interface MapAccumulator<T, K, V> {
   boolean filter(T source);
   K getKey(T source);
   V initializeValue(T source);
   V updateValue(T source, V value);
   void forEach(Entry<K, V> entry);
}