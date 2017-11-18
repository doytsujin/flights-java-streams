package airtraffic;

import static airtraffic.GeoHelper.distanceFromReferenceComparator;
import static airtraffic.GeoHelper.getDistance;
import static airtraffic.GeoLocation.Units.MILES;

import java.util.stream.Stream;

/**
 * Generate various airport statistics using Java 8 streams.
 * 
 * @author tony@piazzaconsulting.com
 */
public class AirportReportsApp extends AbstractReportsApp {
	public static void main(String[] args) throws Exception {
		Stream<Airport> source = new ReferenceData().getAirportStream();
		AirportReportsApp app = new AirportReportsApp();
		app.executeSelectedReport(source);
	}

	public void reportAirportsForState(Stream<Airport> source) {
		String state = readString("State");
		println("\nIATA\tAirport Name\t\t\t\t\tCity");
		println(repeat("-", 77));
		source.filter(a -> a.getState().equals(state))
			  .sorted()
			  .forEach(a -> printf(" %3s\t%-40s\t%-20s\n", a.getIATA(), a.getName(), a.getCity()));
	}

	public void reportAirportsNearLocation(Stream<Airport> source) {
		double latitude = readDouble("Latitude", -90.0, 90.0);
		double longitude = readDouble("Longitude", -180.0, 180.0);
		GeoLocation loc = new GeoLocation() {
			@Override public double getLatitude()	{ return latitude;	}
			@Override public double getLongitude()	{ return longitude;	}
		};
		int distance = readInt("Distance (miles)", 1, 1000);
		println("\nIATA\tAirport Name\t\t\t\t\tState\tCity\t\tDistance");
		println(repeat("-", 89));
		source.filter(a -> getDistance(a, loc, MILES) <= distance)
			  .sorted(distanceFromReferenceComparator(loc, MILES))
			  .forEach(a -> printf(" %3s\t%-40s\t %2s\t%-15s    %,4.0f\n", 
					  				a.getIATA(), 
					  				a.getName(), 
					  				a.getState(), 
					  				left(a.getCity(), 15),
					  				getDistance(a, loc, MILES)));
	}
}