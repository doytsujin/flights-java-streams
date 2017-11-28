package airtraffic;

import java.time.LocalDate;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Simple class for grouping flight data by a combination of 2 properties.
 *
 * @author tony@piazzaconsulting.com
 */
public class PairGroup<S, T> implements Comparable<PairGroup<S, T>> {
   private final S first;
   private final T second;

   public static PairGroup<Airport, LocalDate> pairAirportDay(Airport airport, 
      LocalDate date) {
      return new PairGroup<Airport, LocalDate>(airport, date);
   }

   public static PairGroup<Carrier, LocalDate> pairCarrierDay(Carrier carrier, 
      LocalDate date) {
      return new PairGroup<Carrier, LocalDate>(carrier, date);
   }

   public PairGroup(S first, T second) {
      this.first = first;
      this.second = second;
   }

   public S getFirst() {
      return first;
   }

   public T getSecond() {
      return second;
   }

   @Override
   public int compareTo(PairGroup<S, T> other) {
      return new CompareToBuilder().append(this.first, other.first)
                                   .append(this.first, other.second)
                                   .toComparison();
   }

   @Override
   public boolean equals(Object obj) {
      return EqualsBuilder.reflectionEquals(this, obj, false);
   }

   @Override
   public int hashCode() {
      return HashCodeBuilder.reflectionHashCode(this, false);
   }

   @Override
   public String toString() {
      return ToStringBuilder.reflectionToString(this);
   }
}