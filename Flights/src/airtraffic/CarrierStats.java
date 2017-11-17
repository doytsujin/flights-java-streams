package airtraffic;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Class used to aggregate statistics for an airline carrier. 
 *
 * @author tony@piazzaconsulting.com
 */
public class CarrierStats {
	private Carrier carrier;
	private int totalFlights;
	private Set<String> airports = new HashSet<String>();
	private int totalCancelled;

	public CarrierStats(Carrier carrier) {
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
		airports.add(flight.getOrigin().getIATA());
		airports.add(flight.getDestination().getIATA());
	}

	public static CarrierStats combine(CarrierStats stats1, CarrierStats stats2) {
		if(!stats1.carrier.equals(stats2.carrier)) {
			throw new IllegalArgumentException("Wrong carrier");
		}
		CarrierStats result = new CarrierStats(stats1.carrier);
		result.totalFlights = stats1.totalFlights + stats2.totalFlights;
		result.totalCancelled = stats1.totalCancelled + stats2.totalCancelled;
		result.airports.addAll(stats1.airports);
		result.airports.addAll(stats2.airports);
		return result;
	}

	public void add(CarrierStats other) {
		if(!this.carrier.equals(other.carrier)) {
			throw new IllegalArgumentException("Wrong carrier");
		}
		this.totalFlights += other.totalFlights;
		this.totalCancelled += other.totalCancelled;
		this.airports.addAll(other.airports);
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

	public Set<String> getAirports() {
		return Collections.unmodifiableSet(airports);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}