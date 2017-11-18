package airtraffic;

import java.util.stream.Stream;

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
}