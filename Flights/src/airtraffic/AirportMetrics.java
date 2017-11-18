package airtraffic;

import org.apache.commons.lang3.builder.ToStringBuilder;

import airtraffic.Flight.CancellationCode;

/**
 * Aggregate statistics for an airport. 
 *
 * @author tony@piazzaconsulting.com
 */
public class AirportMetrics {
	private Airport airport;
	private int totalFlights;
	private int totalCancelled;
	private int totalCancelledCarrier;
	private int totalCancelledWeather;
	private int totalCancelledNAS;
	private int totalCancelledSecurity;
	private int totalDiverted;

	public AirportMetrics(Airport airport) {
		this.airport = airport;
	}

	public void addFlight(Flight flight) {
		if(flight.getOrigin().equals(airport)) {
			// cancellations are counted only for the origin airport
			if(flight.cancelled()) {
				++totalCancelled;
				switch(flight.getCancellationCode()) {
					case CARRIER:	++totalCancelledCarrier;		break;
					case WEATHER:	++totalCancelledWeather;		break;
					case NAS:		++totalCancelledNAS;			break;
					case SECURITY:	++totalCancelledSecurity;	break;
				}
			}
		} else if(flight.getDestination().equals(airport)) {
			// diversions are counted only for the destination airport
			if(flight.diverted()) {
				++totalDiverted;
			}
		} else {
			throw new IllegalArgumentException("Wrong airport");
		}

		++totalFlights;
	}

	public static AirportMetrics combine(AirportMetrics metrics1, AirportMetrics metrics2) {
		if(!metrics1.airport.equals(metrics2.airport)) {
			throw new IllegalArgumentException("Wrong carrier");
		}
		AirportMetrics result = new AirportMetrics(metrics1.airport);
		result.totalFlights = metrics1.totalFlights + metrics2.totalFlights;
		result.totalCancelled = metrics1.totalCancelled + metrics2.totalCancelled;
		result.totalDiverted = metrics1.totalDiverted + metrics2.totalDiverted;
		return result;
	}

	public Airport getAirport() {
		return airport;
	}

	public int getTotalFlights() {
		return totalFlights;
	}

	public int getTotalCancelled() {
		return totalCancelled;
	}

	public int getTotalCancelledByCode(CancellationCode code) {
		switch(code) {
			case CARRIER:	return totalCancelledCarrier;
			case WEATHER:	return totalCancelledWeather;
			case NAS:		return totalCancelledNAS;
			case SECURITY:	return totalCancelledSecurity;
			default:			return 0;
		}
	}

	public int getTotalDiverted() {
		return totalDiverted;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}