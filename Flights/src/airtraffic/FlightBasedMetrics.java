package airtraffic;

import java.util.Comparator;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Common behavior for classes that report flight-based metrics.
 *
 * @author tony@piazzaconsulting.com
 */
public abstract class FlightBasedMetrics<T> {
	protected int totalFlights;
	protected int totalCancelled;
	protected int totalDiverted;
	private T subject;

	protected FlightBasedMetrics(T subject) {
		this.subject = subject;
	}

	public abstract void addFlight(Flight flight);

	public T getSubject() {
		return subject;
	}

	public static Comparator<FlightBasedMetrics<?>> highestCancellationRateComparator() {
		return (m1, m2) -> Double.compare(m2.getCancellationRate(), m1.getCancellationRate());
	}

	public static Comparator<FlightBasedMetrics<?>> highestTotalFlightsComparator() {
		return (m1, m2) -> Integer.compare(m2.getTotalFlights(), m1.getTotalFlights());
	}

	public int getTotalFlights() {
		return totalFlights;
	}

	public int getTotalCancelled() {
		return totalCancelled;
	}

	public double getCancellationRate() {
		return (double)totalCancelled / (double)totalFlights;
	}

	public int getTotalDiverted() {
		return totalDiverted;
	}

	public double getDiversionRate() {
		return (double)totalDiverted / (double)totalFlights;
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