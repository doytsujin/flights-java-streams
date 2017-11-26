package airtraffic.iterator;

import java.util.Map.Entry;

public interface MapAccumulator<T, K, V> {
   boolean filter(T source);
   K getKey(T source);
   void forEach(Entry<K, V> entry);
}