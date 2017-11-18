package airtraffic;

import java.util.Map;
import java.util.function.BiConsumer;

import airtraffic.Flight.CancellationCode;

/**
 * Aggregate statistics for an airport. 
 *
 * @author tony@piazzaconsulting.com
 */
public class AirportMetrics extends FlightBasedMetrics<Airport> {
	private int totalCancelledCarrier;
	private int totalCancelledWeather;
	private int totalCancelledNAS;
	private int totalCancelledSecurity;

	public AirportMetrics(Airport airport) {
		super(airport);
	}

	public void addFlight(Flight flight) {
		if(flight.getOrigin().equals(getSubject())) {
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
		} else if(flight.getDestination().equals(getSubject())) {
			// diversions are counted only for the destination airport
			if(flight.diverted()) {
				++totalDiverted;
			}
		} else {
			throw new IllegalArgumentException("Wrong airport");
		}

		++totalFlights;
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

	public static BiConsumer<Map<String, AirportMetrics>, Flight> accumulator() { 
		return (map, flight) -> {
			Airport origin = flight.getOrigin();
			AirportMetrics metrics1 = map.get(origin.getIATA());
			if(metrics1 == null) {
				metrics1 = new AirportMetrics(origin);
				map.put(origin.getIATA(), metrics1);
			}
			metrics1.addFlight(flight);

			Airport destination = flight.getDestination();
			AirportMetrics metrics2 = map.get(destination.getIATA());
			if(metrics2 == null) {
				metrics2 = new AirportMetrics(destination);
				map.put(destination.getIATA(), metrics2);
			}
			metrics2.addFlight(flight);
		};
	}

	public static BiConsumer<Map<String, AirportMetrics>, Map<String, AirportMetrics>> combiner() {
		return (map1, map2) -> {
			map1.entrySet()
				.stream()
				.forEach(e -> {
					String airport = e.getKey();
					AirportMetrics metrics = map2.get(airport);
					if(metrics != null) {
						map1.merge(airport, metrics, (metrics1, metrics2) -> {
							if(!metrics1.getSubject().equals(metrics2.getSubject())) {
								throw new IllegalArgumentException("Wrong carrier");
							}
							AirportMetrics result = new AirportMetrics(metrics1.getSubject());
							result.totalFlights = metrics1.totalFlights + metrics2.totalFlights;
							result.totalCancelled = metrics1.totalCancelled + metrics2.totalCancelled;
							result.totalDiverted = metrics1.totalDiverted + metrics2.totalDiverted;
							return result;
						});
					}
				});
		};
	}
}