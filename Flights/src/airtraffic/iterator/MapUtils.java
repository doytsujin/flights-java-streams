package airtraffic.iterator;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Contains useful methods for working with Maps that need to be sorted.
 *
 * @author tony@piazzaconsulting.com
 */
public final class MapUtils {
   public static <T, K extends Comparable<K>, V> void accumulate(Iterator<T> iterator, 
      Comparator<Entry<K, V>> comparator, int limit, 
      MapAccumulator<T, K, V> accumulator) {
      Map<K, V> map = new HashMap<>();
      while(iterator.hasNext()) {
         T subject = iterator.next();
         if(accumulator.filter(subject)) {
            K key = accumulator.getKey(subject);
            V counter = map.get(key);
            if (counter == null) {
               map.put(key, accumulator.initializeValue(subject));
            } else {
               map.put(key, accumulator.updateValue(subject, counter));
            }
         }
      }
      @SuppressWarnings("unchecked")
      Entry<K, V>[] entries = map.entrySet().toArray(new Entry[map.size()]);
      Arrays.sort(entries, comparator);
      Map<K, V> result = new LinkedHashMap<>();
      for (Entry<K, V> entry : entries) {
         result.put(entry.getKey(), entry.getValue());
      }
      int count = 0;
      for(Entry<K, V> entry : result.entrySet()) {
         accumulator.forEach(entry);
         if(++count >= limit) {
            break;
         }
      }
   }
}