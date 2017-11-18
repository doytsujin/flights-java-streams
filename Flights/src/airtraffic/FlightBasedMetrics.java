package airtraffic;

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

	public int getTotalFlights() {
		return totalFlights;
	}

	public int getTotalCancelled() {
		return totalCancelled;
	}

	public int getTotalDiverted() {
		return totalDiverted;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}