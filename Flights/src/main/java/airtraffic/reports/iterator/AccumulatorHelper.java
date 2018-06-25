package airtraffic.reports.iterator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Contains useful methods for accumulation.
 *
 * @author tony@piazzaconsulting.com
 */
public final class AccumulatorHelper {
   public static <T, K extends Comparable<K>, V extends Comparable<V>> void accumulate(
      Iterator<T> iterator, Comparator<Entry<K, V>> comparator, int limit, 
      MapAccumulator<T, K, V> accumulator) {
      Map<K, V> map = new HashMap<>();
      while(iterator.hasNext()) {
         T subject = iterator.next();
         if(accumulator.filter(subject)) {
            K key = accumulator.getKey(subject);
            V value = map.get(key);
            map.put(key, value == null ? 
               accumulator.initializeValue(subject) :
               accumulator.updateValue(subject, value)
            );
         }
      }
      List<Entry<K, V>> entries = new ArrayList<>(map.entrySet());
      Collections.sort(entries, comparator);
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
      map.clear();
      result.clear();
      entries.clear();
   }

   public static <T extends Comparable<T>> void accumulate(Iterator<T> iterator, 
      Comparator<? super T> comparator, int limit, ListAccumulator<T> accumulator) {
      List<T> list = new ArrayList<>();
      while(iterator.hasNext()) {
         T subject = iterator.next();
         if(accumulator.filter(subject)) {
            list.add(subject);
         }
      }
      Collections.sort(list, comparator);
      int count = 0;
      for(T subject : list) {
         accumulator.forEach(subject);
         if(++count >= limit) {
            break;
         }         
      }
      list.clear();
   }
}