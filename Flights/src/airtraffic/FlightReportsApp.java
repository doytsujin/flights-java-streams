package airtraffic;

import static java.util.stream.Collectors.averagingInt;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;
import org.beryx.textio.TextTerminal;

import airtraffic.annotation.ReportDescription;

/**
 * Generate various flight statistics using Java 8 streams.
 * 
 * @author tony@piazzaconsulting.com
 */
public class FlightReportsApp {
	public static void main(String[] args) throws Exception {
		List<Method> printMethods = 
			getPrintMethodStream().sorted((m1, m2) -> m1.getName().compareTo(m2.getName()))
								  .collect(toList());
		TextIO io = TextIoFactory.getTextIO();
		int optionNum = getReportOption(printMethods, io);
		if(optionNum > 0) {
			Method method = printMethods.get(optionNum-1);
			io.getTextTerminal().println(getReportDescription(method));
			FlightReportsApp stats = new FlightReportsApp();
			ReferenceData reference = new ReferenceData();
			Stream<Flight> source = Files.lines(Paths.get("data/flights-2008.csv"))
										.skip(1)	// skip header
										.map(s -> new Flight(s, reference));
			method.invoke(stats, source, io);
		} else {
			System.exit(0);
		}
	}

	private static int getReportOption(List<Method> printMethods, TextIO io) {
		TextTerminal<?> terminal = io.getTextTerminal();
		terminal.println("Program options:\n");
		terminal.println(" 0  Exit program");
		int n = 0;
		for(Method m : printMethods) {
			terminal.printf("%2d  %s\n", ++n, m.getName().substring(5));
		}
		return io.newIntInputReader()
				 .withDefaultValue(0)
				 .withMinVal(0)
				 .withMaxVal(printMethods.size())
				 .read("\nOption");
	}

	private static String getReportDescription(Method method) {
		return method.getDeclaredAnnotation(ReportDescription.class).value();
	}

	private static Stream<Method> getPrintMethodStream() {
		return Arrays.stream(FlightReportsApp.class.getDeclaredMethods())
					 .filter(m -> m.getName().startsWith("print") && 
							 	  m.getParameterTypes().length > 0 &&
							 	  m.getReturnType().equals(Void.TYPE));
	}

	@ReportDescription("Total flights from a specific origin (IATA code).")
	public void printTotalFlightsFromOrigin(Stream<Flight> source, TextIO io) {
		String origin = io.newStringInputReader()
						  .read("Origin");
		long count = source.filter(f -> f.notCancelled() && 
				                        f.getOrigin().getIATA().equals(origin))
						   .count();
		io.getTextTerminal().printf("Total flights from %s is %,d\n", origin, count);
	}

	@ReportDescription("Total flights to a specific destination (IATA code).")
	public void printTotalFlightsToDestination(Stream<Flight> source, TextIO io) {
		String destination = io.newStringInputReader()
							   .read("Destination");
		long count = source.filter(f -> f.notCancelled() && 
				                        f.getDestination().getIATA().equals(destination))
						   .count();
		io.getTextTerminal().printf("Total flights to %s is %,d\n", destination, count);
	}

	@ReportDescription("Total flights from a specific origin (IATA code).")
	public void printTotalFlightsFromOriginToDestination(Stream<Flight> source, TextIO io) {
		String origin = io.newStringInputReader()
						  .read("Origin");
		String destination = io.newStringInputReader()
							   .read("Destination");
		long count = source.filter(f -> f.notCancelled() && 
				                        f.getOrigin().getIATA().equals(origin) &&
				                        f.getDestination().getIATA().equals(destination))
						   .count();
		io.getTextTerminal().printf("Total flights from %s to %s is %,d\n", origin, destination, count);
	}

	@ReportDescription("Most flights by origin (IATA code).")
	public void printMostFlightsByOrigin(Stream<Flight> source, TextIO io) {
		int limit = io.newIntInputReader()
					  .withDefaultValue(10)
					  .withMinVal(1)
					  .withMaxVal(100)
					  .read("Limit");
		TextTerminal<?> terminal = io.getTextTerminal();
		terminal.println("\nOrigin\tCount");
		terminal.println("---------------");
		source.filter(f -> f.notCancelled())
			  .collect(groupingBy(Flight::getOrigin, counting()))
			  .entrySet()
			  .stream()
			  .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
			  .limit(limit)
			  .forEachOrdered(e -> terminal.printf("%3s\t%d\n", e.getKey().getIATA(), e.getValue()));
	}

	@ReportDescription("Top destinations from a specific origin (IATA code).")
	public void printTopDestinationsFromOrigin(Stream<Flight> source, TextIO io) {
		String origin = io.newStringInputReader()
						  .read("Origin");
		int limit = io.newIntInputReader()
				  .withDefaultValue(10)
				  .withMinVal(1)
				  .withMaxVal(100)
				  .read("Limit");
		TextTerminal<?> terminal = io.getTextTerminal();
		terminal.println("\nDestination\t   Count");
		terminal.println("------------------------------");
		source.filter(f -> f.notCancelled() && f.getOrigin().getIATA().equals(origin))
			  .collect(groupingBy(Flight::getDestination, counting()))
			  .entrySet()
			  .stream()
			  .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
			  .limit(limit)
			  .forEachOrdered(e -> terminal.printf(" %3s\t\t%,10d\n", e.getKey().getIATA(), e.getValue()));
	}

	@ReportDescription("Most popular routes.")
	public void printMostPopularRoutes(Stream<Flight> source, TextIO io) {
		int limit = io.newIntInputReader()
				  .withDefaultValue(10)
				  .withMinVal(1)
				  .withMaxVal(100)
				  .read("Limit");
		TextTerminal<?> terminal = io.getTextTerminal();
		terminal.println("Route\t\t    Count");
		terminal.println("---------------------------");
		source.filter(f -> f.notCancelled())
			  .collect(groupingBy(Flight::getRoute, counting()))
			  .entrySet()
			  .stream()
			  .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
			  .limit(limit)
			  .forEachOrdered(e -> terminal.printf("%s\t%,10d\n", e.getKey(), e.getValue().intValue()));
	}

	@ReportDescription("Worst average departure delay.")
	public void printWorstAverageDepartureDelay(Stream<Flight> source, TextIO io) {
		int limit = io.newIntInputReader()
				  .withDefaultValue(10)
				  .withMinVal(1)
				  .withMaxVal(100)
				  .read("Limit");
		TextTerminal<?> terminal = io.getTextTerminal();
		terminal.println("Origin\tDelay (min)");
		terminal.println("----------------------");
		source.filter(f -> f.notCancelled())
			  .collect(groupingBy(Flight::getOrigin, averagingInt(f -> f.getDepartureDelay())))
			  .entrySet()
			  .stream()
			  .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
			  .limit(limit)
			  .forEachOrdered(e -> terminal.printf(" %3s\t\t%.0f\n", e.getKey().getIATA(), e.getValue()));
	}

	@ReportDescription("Worst average arrival delay")
	public void printWorstAverageArrivalDelay(Stream<Flight> source, TextIO io) {
		int limit = io.newIntInputReader()
				  .withDefaultValue(10)
				  .withMinVal(1)
				  .withMaxVal(100)
				  .read("Limit");
		TextTerminal<?> terminal = io.getTextTerminal();
		terminal.println("Destination\tDelay (min)");
		terminal.println("----------------------------");
		source.filter(f -> f.notCancelled())
			  .collect(groupingBy(Flight::getDestination, averagingInt(f -> f.getArrivalDelay())))
			  .entrySet()
			  .stream()
			  .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
			  .limit(limit)
			  .forEachOrdered(e -> terminal.printf(" %3s\t\t\t%.0f\n", e.getKey().getIATA(), e.getValue()));
	}

	@ReportDescription("Most cancelled flights by origin (IATA code).")
	public void printMostCancelledFlightsByOrigin(Stream<Flight> source, TextIO io) {
		int limit = io.newIntInputReader()
				  .withDefaultValue(10)
				  .withMinVal(1)
				  .withMaxVal(100)
				  .read("Limit");
		TextTerminal<?> terminal = io.getTextTerminal();
		terminal.println("Origin\t\t  Count");
		terminal.println("---------------------------");
		source.filter(f -> f.cancelled())
			  .collect(groupingBy(Flight::getOrigin, counting()))
			  .entrySet()
			  .stream()
			  .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
			  .limit(limit)
			  .forEachOrdered(e -> terminal.printf(" %3s\t\t%,8d\n", e.getKey().getIATA(), e.getValue()));
	}

	@ReportDescription("Most cancelled flights by carrier.")
	public void printMostCancelledFlightsByCarrier(Stream<Flight> source, TextIO io) {
		int limit = io.newIntInputReader()
				  .withDefaultValue(10)
				  .withMinVal(1)
				  .withMaxVal(100)
				  .read("Limit");
		TextTerminal<?> terminal = io.getTextTerminal();
		terminal.println("Carrier\t\t\t\t Count");
		terminal.println("-----------------------------------------");
		source.filter(f -> f.cancelled())
		      .map(f -> f.getCarrier())
			  .collect(groupingBy(Carrier::getName, counting()))
			  .entrySet()
			  .stream()
			  .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
			  .limit(limit)
			  .forEachOrdered(e -> terminal.printf("%-24s\t%,8d\n", left(e.getKey(), 24), e.getValue()));
	}

	private String left(String input, int size) {
		return input != null && input.length() > size ? input.substring(0, size) : input;
	}

	@ReportDescription("Various carrier statistics.")
	public void printCarrierStats(Stream<Flight> source, TextIO io) {
		BiConsumer<Map<String, CarrierStats>, Flight> accumulator = 
			(map, flight) -> {
				Carrier carrier = flight.getCarrier();
				CarrierStats stats = map.get(carrier.getCode());
				if(stats == null) {
					stats = new CarrierStats(carrier);
					map.put(carrier.getCode(), stats);
				}
				stats.addFlight(flight);
			};

		BiConsumer<Map<String, CarrierStats>, Map<String, CarrierStats>> combiner =
			(map1, map2) -> {
				map1.entrySet()
				.stream()
				.forEach(e -> {
					String carrier = e.getKey();
					CarrierStats stats = map2.get(carrier);
					if(stats != null) {
						map1.merge(carrier, stats, CarrierStats::combine);
					}
				});
			};

		TextTerminal<?> terminal = io.getTextTerminal();
		terminal.println("IATA    Carrier Name                      Total        Cancelled     %    Airports");
		terminal.println("-----------------------------------------------------------------------------------");
		source.collect(HashMap::new, accumulator, combiner)
			  .entrySet()
			  .stream()
			  .sorted((e1, e2) -> e2.getValue().getTotalFlights() - e1.getValue().getTotalFlights())
			  .forEach(e -> {
				  CarrierStats stats = e.getValue();
				  Carrier carrier = stats.getCarrier();
				  String carrierName = carrier.getName();
				  terminal.printf(" %2s     %-30s     %,9d   %,8d  %6.1f   %,5d\n", 
						  		  carrier.getCode(),
						  		  carrierName.substring(0, Math.min(carrierName.length(), 29)),
						  		  stats.getTotalFlights(),
						  		  stats.getTotalCancelled(),
						  		  stats.getTotalCancelled() * 100.0 / stats.getTotalFlights(),
						  		  stats.getAirports().size()
				  );
			  });
	}

	@ReportDescription("Flight counts by aircraft type.")
	public void printFlightCountsByAircraftType(Stream<Flight> source, TextIO io) {
		TextTerminal<?> terminal = io.getTextTerminal();
		terminal.println("Aircraft Type\t\t\tCount");
		terminal.println("-------------------------------------------");
		source.filter(f -> f.notCancelled())
			  .map(f -> f.getPlane())
			  .collect(groupingBy(Plane::getAircraftType, counting()))
			  .entrySet()
			  .stream()
			  .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
			  .forEachOrdered(e -> terminal.printf("%-25s\t%,10d\n", e.getKey(), e.getValue()));
	}

	@ReportDescription("Flight counts by engine type.")
	public void printFlightCountsByEngineType(Stream<Flight> source, TextIO io) {
		TextTerminal<?> terminal = io.getTextTerminal();
		terminal.println("Engine Type\t\t\tCount");
		terminal.println("-------------------------------------------");
		source.filter(f -> f.notCancelled())
			  .map(f -> f.getPlane())
			  .collect(groupingBy(Plane::getEngineType, counting()))
			  .entrySet()
			  .stream()
			  .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
			  .forEachOrdered(e -> terminal.printf("%-25s\t%,10d\n", e.getKey(), e.getValue()));
	}

	@ReportDescription("Flight counts by manufacturer.")
	public void printFlightCountsByManufacturer(Stream<Flight> source, TextIO io) {
		TextTerminal<?> terminal = io.getTextTerminal();
		terminal.println("Manufacturer\t\t\t Count");
		terminal.println("-------------------------------------------");
		source.filter(f -> f.notCancelled())
			  .map(f -> f.getPlane())
			  .collect(groupingBy(Plane::getManufacturer, counting()))
			  .entrySet()
			  .stream()
			  .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
			  .forEachOrdered(e -> terminal.printf("%-25s\t%,10d\n", e.getKey(), e.getValue()));
	}

	@ReportDescription("Flight counts by plane year.")
	public void printFlightCountsByPlaneYear(Stream<Flight> source, TextIO io) {
		TextTerminal<?> terminal = io.getTextTerminal();
		terminal.println("Year\t  Count");
		terminal.println("-------------------");
		source.filter(f -> f.notCancelled())
			  .map(f -> f.getPlane())
			  .collect(groupingBy(Plane::getYear, counting()))
			  .entrySet()
			  .stream()
			  .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
			  .forEachOrdered(e -> terminal.printf("%4s\t%,10d\n", 
						  						  e.getKey().longValue() == 0 ? "????" : e.getKey(), 
						  						  e.getValue()));
	}

	@ReportDescription("Most flights by origin state.")
	public void printMostFlightsByOriginState(Stream<Flight> source, TextIO io) {
		int limit = io.newIntInputReader()
					  .withDefaultValue(10)
					  .withMinVal(1)
					  .withMaxVal(100)
					  .read("Limit");
		TextTerminal<?> terminal = io.getTextTerminal();
		terminal.println("State\t  Count");
		terminal.println("-------------------");
		source.filter(f -> f.notCancelled())
		      .map(f -> f.getOrigin())
			  .collect(groupingBy(Airport::getState, counting()))
			  .entrySet()
			  .stream()
			  .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
			  .limit(limit)
			  .forEachOrdered(e -> terminal.printf("%2s\t%,10d\n", e.getKey(), e.getValue()));
	}

	@ReportDescription("Most flights by destination state.")
	public void printMostFlightsByDestinationState(Stream<Flight> source, TextIO io) {
		int limit = io.newIntInputReader()
					  .withDefaultValue(10)
					  .withMinVal(1)
					  .withMaxVal(100)
					  .read("Limit");
		TextTerminal<?> terminal = io.getTextTerminal();
		terminal.println("State\tCount");
		terminal.println("-------------------");
		source.filter(f -> f.notCancelled())
		      .map(f -> f.getDestination())
			  .collect(groupingBy(Airport::getState, counting()))
			  .entrySet()
			  .stream()
			  .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
			  .limit(limit)
			  .forEachOrdered(e -> terminal.printf("%2s\t%,10d\n", e.getKey(), e.getValue()));
	}

	@ReportDescription("Most flights by a specific plane.")
	public void printMostFlightsByPlane(Stream<Flight> source, TextIO io) {
		int limit = io.newIntInputReader()
				  .withDefaultValue(10)
				  .withMinVal(1)
				  .withMaxVal(100)
				  .read("Limit");
		TextTerminal<?> terminal = io.getTextTerminal();
		terminal.println("Tail #\t  Manufacturer\t\tModel #\t\tCount\t Daily Avg");
		terminal.println(StringUtils.repeat("-", 67));
		source.filter(f -> f.notCancelled())
			  .collect(groupingBy(Flight::getPlane, counting()))
			  .entrySet()
			  .stream()
			  .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
			  .limit(limit)
			  .forEachOrdered(e -> {
				  Plane plane = e.getKey();
				  terminal.printf("%-8s  %-20s  %-10s  %,10d\t  %6.1f\n", 
						  		  plane.getTailNumber(), 
						  		  left(plane.getManufacturer(), 20),
						  		  left(plane.getModel().getModelNumber(), 10),
						  		  e.getValue(),
						  		  e.getValue().floatValue() / 365);  
			  });
	}

	@ReportDescription("Most flights by a plane model.")
	public void printMostFlightsByPlaneModel(Stream<Flight> source, TextIO io) {
		int limit = io.newIntInputReader()
					  .withDefaultValue(10)
					  .withMinVal(1)
					  .withMaxVal(100)
					  .read("Limit");
		TextTerminal<?> terminal = io.getTextTerminal();
		terminal.println("Manufacturer\t\t\tModel #\t\t\t  Count\t\tDaily Avg");
		terminal.println(StringUtils.repeat("-", 82));
		source.filter(f -> f.notCancelled())
			  .map(p -> p.getPlane())
			  .collect(groupingBy(Plane::getModel, counting()))
			  .entrySet()
			  .stream()
			  .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
			  .limit(limit)
			  .map(e -> {
				  	PlaneModel model = e.getKey();
				  	Long count = e.getValue();
			  		return String.format("%-25s\t%-20s\t%,10d\t%8.1f",
			  							 model.getManufacturer(),
			  							 model.getModelNumber(),
			  							 count,
			  							 count.floatValue() / 365);
			  }).forEachOrdered(s -> terminal.println(s));
	}
}