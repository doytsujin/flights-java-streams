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
   public static <T, K extends Comparable<K>> void accumulateCount(Iterator<T> iterator, 
      Comparator<Entry<K, Long>> comparator, int limit, 
      MapAccumulator<T, K, Long> accumulator) {
      Map<K, Long> map = new HashMap<>();
      while(iterator.hasNext()) {
         T subject = iterator.next();
         if(accumulator.filter(subject)) {
            K key = accumulator.getKey(subject);
            Long counter = map.get(key);
            if (counter == null) {
               map.put(key, Long.valueOf(1));
            } else {
               map.put(key, Long.valueOf(counter.longValue() + 1));
            }
         }
      }
      @SuppressWarnings("unchecked")
      Entry<K, Long>[] entries = map.entrySet().toArray(new Entry[map.size()]);
      Arrays.sort(entries, comparator);
      Map<K, Long> result = new LinkedHashMap<K, Long>();
      for (Entry<K, Long> entry : entries) {
         result.put(entry.getKey(), entry.getValue());
      }
      int count = 0;
      for(Entry<K, Long> entry : result.entrySet()) {
         accumulator.forEach(entry);
         if(++count >= limit) {
            break;
         }
      }
   }
}