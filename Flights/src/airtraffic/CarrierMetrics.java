package airtraffic;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Aggregate statistics for an airline carrier. 
 *
 * @author tony@piazzaconsulting.com
 */
public class CarrierMetrics {
	private Carrier carrier;
	private int totalFlights;
	private Set<String> airports = new HashSet<String>();
	private int totalCancelled;
	private int totalDiverted;

	public CarrierMetrics(Carrier carrier) {
		this.carrier = carrier;
	}

	public void addFlight(Flight flight) {
		if(!flight.getCarrier().equals(carrier)) {
			throw new IllegalArgumentException("Wrong carrier");
		}

		++totalFlights;
		if(flight.cancelled()) {
			++totalCancelled;
		}
		if(flight.diverted()) {
			++totalDiverted;
		}
		airports.add(flight.getOrigin().getIATA());
		airports.add(flight.getDestination().getIATA());
	}

	public static CarrierMetrics combine(CarrierMetrics metrics1, CarrierMetrics metrics2) {
		if(!metrics1.carrier.equals(metrics2.carrier)) {
			throw new IllegalArgumentException("Wrong carrier");
		}
		CarrierMetrics result = new CarrierMetrics(metrics1.carrier);
		result.totalFlights = metrics1.totalFlights + metrics2.totalFlights;
		result.totalCancelled = metrics1.totalCancelled + metrics2.totalCancelled;
		result.totalDiverted = metrics1.totalDiverted + metrics2.totalDiverted;
		result.airports.addAll(metrics1.airports);
		result.airports.addAll(metrics2.airports);
		return result;
	}

	public Carrier getCarrier() {
		return carrier;
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

	public Set<String> getAirports() {
		return Collections.unmodifiableSet(airports);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}