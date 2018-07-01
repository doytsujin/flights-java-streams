package airtraffic.reports.iterator;


/**
 * Interface that describes methods needed to support an accumulator based
 * on a List.
 *
 * @author Tony Piazza <tony@piazzaconsulting.com>
 */
public interface ListAccumulator<T> {
   boolean filter(T source);
   void forEach(T source);
}