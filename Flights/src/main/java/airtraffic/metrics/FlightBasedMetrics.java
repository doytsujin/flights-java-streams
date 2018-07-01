package airtraffic.metrics;

import java.util.Comparator;
import java.util.concurrent.atomic.LongAdder;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import airtraffic.Flight;

/**
 * Common behavior for classes that report flight-based metrics.
 *
 * @author Tony Piazza <tony@piazzaconsulting.com>
 */
public abstract class FlightBasedMetrics<T> {
   protected LongAdder totalFlights = new LongAdder();
   protected LongAdder totalCancelled = new LongAdder();
   protected LongAdder totalDiverted = new LongAdder();
   private T subject;

   protected FlightBasedMetrics(T subject) {
      this.subject = subject;
   }

   public abstract FlightBasedMetrics<T> addFlight(Flight flight);

   public T getSubject() {
      return subject;
   }

   public static Comparator<FlightBasedMetrics<?>> highestCancellationRateComparator() {
      return (m1, m2) -> Double.compare(m2.getCancellationRate(), m1.getCancellationRate());
   }

   public static Comparator<FlightBasedMetrics<?>> highestTotalFlightsComparator() {
      return (m1, m2) -> Long.compare(m2.getTotalFlights(), m1.getTotalFlights());
   }

   public Long getTotalFlights() {
      return totalFlights.longValue();
   }

   public long getTotalCancelled() {
      return totalCancelled.longValue();
   }

   public double getCancellationRate() {
      return totalCancelled.doubleValue() / totalFlights.doubleValue();
   }

   public long getTotalDiverted() {
      return totalDiverted.longValue();
   }

   public double getDiversionRate() {
      return totalDiverted.doubleValue() / totalFlights.doubleValue();
   }

   @Override
   public String toString() {
      return ToStringBuilder.reflectionToString(this);
   }

   @Override
   public boolean equals(Object obj) {
      return EqualsBuilder.reflectionEquals(this, obj, false);
   }

   @Override
   public int hashCode() {
      return HashCodeBuilder.reflectionHashCode(this, false);
   }
}