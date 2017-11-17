package airtraffic;

import static java.util.stream.Collectors.averagingInt;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.left;
import static org.apache.commons.lang3.StringUtils.repeat;
import static org.apache.commons.lang3.StringUtils.splitByCharacterTypeCamelCase;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
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

/**
 * Generate various flight statistics using Java 8 streams.
 * 
 * @author tony@piazzaconsulting.com
 */
public class FlightReportsApp {
	private static final String REPORT_METHOD_NAME_PREFIX = "report";
	private static final int REPORT_METHOD_PARAMETER_COUNT = 2;
	private static final Class<?> REPORT_METHOD_RETURN_TYPE = Void.TYPE;

	public static void main(String[] args) throws Exception {
		List<Method> reportMethods = getReportMethods();
		TextIO io = TextIoFactory.getTextIO();
		int optionNum = getReportOption(reportMethods, io);
		if(optionNum == 0) {
			System.exit(0);
		}
		Method method = reportMethods.get(optionNum-1);
		io.getTextTerminal().println(getReportDescription(method));
		FlightReportsApp stats = new FlightReportsApp();
		ReferenceData reference = new ReferenceData();
		Stream<Flight> source = Files.lines(Paths.get("data/flights-2008.csv"))
									 .skip(1)	// skip header
									 .map(s -> new Flight(s, reference));
		method.invoke(stats, source, io);
	}

	public void reportTotalFlightsFromOrigin(Stream<Flight> source, TextIO io) {
		String origin = io.newStringInputReader()
						  .read("Origin");
		long count = source.filter(f -> f.notCancelled() && 
				                        f.getOrigin().getIATA().equals(origin))
						   .count();
		io.getTextTerminal().printf("Total flights from %s is %,d\n", origin, count);
	}

	public void reportTotalFlightsToDestination(Stream<Flight> source, TextIO io) {
		String destination = io.newStringInputReader()
							   .read("Destination");
		long count = source.filter(f -> f.notCancelled() && 
				                        f.getDestination().getIATA().equals(destination))
						   .count();
		io.getTextTerminal().printf("Total flights to %s is %,d\n", destination, count);
	}

	public void reportTotalFlightsFromOriginToDestination(Stream<Flight> source, TextIO io) {
		String origin = io.newStringInputReader()
						  .read("Origin");
		String destination = io.newStringInputReader()
							   .read("Destination");
		long count = source.filter(f -> f.notCancelled() && 
				                        f.getOrigin().getIATA().equals(origin) &&
				                        f.getDestination().getIATA().equals(destination))
						   .count();
		io.getTextTerminal().printf("Total flights from %s to %s is %,d\n", 
									origin, destination, count);
	}

	public void reportMostFlightsByOrigin(Stream<Flight> source, TextIO io) {
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
			  .forEachOrdered(e -> terminal.printf("%3s\t%d\n", 
					  								e.getKey().getIATA(), e.getValue()));
	}

	public void reportTopDestinationsFromOrigin(Stream<Flight> source, TextIO io) {
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
			  .forEachOrdered(e -> terminal.printf(" %3s\t\t%,10d\n", 
					  								e.getKey().getIATA(), e.getValue()));
	}

	public void reportMostPopularRoutes(Stream<Flight> source, TextIO io) {
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
			  .forEachOrdered(e -> terminal.printf("%s\t%,10d\n", 
					  								e.getKey(), e.getValue().intValue()));
	}

	public void reportWorstAverageDepartureDelayByOrigin(Stream<Flight> source, TextIO io) {
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
			  .forEachOrdered(e -> terminal.printf(" %3s\t\t%.0f\n", 
					  								e.getKey().getIATA(), e.getValue()));
	}

	public void reportWorstAverageArrivalDelayByDestination(Stream<Flight> source, TextIO io) {
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
			  .forEachOrdered(e -> terminal.printf(" %3s\t\t\t%.0f\n", 
					  								e.getKey().getIATA(), e.getValue()));
	}

	public void reportMostCancelledFlightsByOrigin(Stream<Flight> source, TextIO io) {
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
			  .forEachOrdered(e -> terminal.printf(" %3s\t\t%,8d\n", 
					  								e.getKey().getIATA(), e.getValue()));
	}

	public void reportMostCancelledFlightsByCarrier(Stream<Flight> source, TextIO io) {
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
			  .forEachOrdered(e -> terminal.printf("%-24s\t%,8d\n",
					  								left(e.getKey(), 24), e.getValue()));
	}

	public void reportVariousCarrierStatistics(Stream<Flight> source, TextIO io) {
		BiConsumer<Map<String, CarrierMetrics>, Flight> accumulator = 
			(map, flight) -> {
				Carrier carrier = flight.getCarrier();
				CarrierMetrics stats = map.get(carrier.getCode());
				if(stats == null) {
					stats = new CarrierMetrics(carrier);
					map.put(carrier.getCode(), stats);
				}
				stats.addFlight(flight);
			};

		BiConsumer<Map<String, CarrierMetrics>, Map<String, CarrierMetrics>> combiner =
			(map1, map2) -> {
				map1.entrySet()
				.stream()
				.forEach(e -> {
					String carrier = e.getKey();
					CarrierMetrics stats = map2.get(carrier);
					if(stats != null) {
						map1.merge(carrier, stats, CarrierMetrics::combine);
					}
				});
			};

		TextTerminal<?> terminal = io.getTextTerminal();
		terminal.println("IATA    Carrier Name                        Total        Cancelled %   Diverted %    Airports");
		terminal.println(StringUtils.repeat("-", 94));
		source.collect(HashMap::new, accumulator, combiner)
			  .entrySet()
			  .stream()
			  .sorted((e1, e2) -> e1.getKey().compareTo(e2.getKey()))
			  .forEach(e -> {
				  CarrierMetrics metrics = e.getValue();
				  Carrier carrier = metrics.getCarrier();
				  String carrierName = carrier.getName();
				  terminal.printf(" %2s     %-30s     %,9d    %6.1f        %6.1f         %,5d\n", 
						  		  carrier.getCode(),
						  		  carrierName.substring(0, Math.min(carrierName.length(), 29)),
						  		  metrics.getTotalFlights(),
						  		  metrics.getTotalCancelled() * 100.0 / metrics.getTotalFlights(),
						  		  metrics.getTotalDiverted() * 100.0 / metrics.getTotalFlights(),
						  		  metrics.getAirports().size()
				  );
			  });
	}

	public void reportFlightCountsByAircraftType(Stream<Flight> source, TextIO io) {
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

	public void reportFlightCountsByEngineType(Stream<Flight> source, TextIO io) {
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

	public void reportFlightCountsByManufacturer(Stream<Flight> source, TextIO io) {
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

	public void reportFlightCountsByPlaneYear(Stream<Flight> source, TextIO io) {
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

	public void reportMostFlightsByOriginState(Stream<Flight> source, TextIO io) {
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

	public void reportMostFlightsByDestinationState(Stream<Flight> source, TextIO io) {
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

	public void reportMostFlightsByPlane(Stream<Flight> source, TextIO io) {
		int limit = io.newIntInputReader()
				  .withDefaultValue(10)
				  .withMinVal(1)
				  .withMaxVal(100)
				  .read("Limit");
		TextTerminal<?> terminal = io.getTextTerminal();
		terminal.println("Tail #\t  Manufacturer\t\tModel #\t\tCount\t Daily Avg");
		terminal.println(repeat("-", 67));
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

	public void reportMostFlightsByPlaneModel(Stream<Flight> source, TextIO io) {
		int limit = io.newIntInputReader()
					  .withDefaultValue(10)
					  .withMinVal(1)
					  .withMaxVal(100)
					  .read("Limit");
		TextTerminal<?> terminal = io.getTextTerminal();
		terminal.println("Manufacturer\t\t\tModel #\t\t\t  Count\t\tDaily Avg");
		terminal.println(repeat("-", 82));
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

	private static int getReportOption(List<Method> printMethods, TextIO io) {
		TextTerminal<?> terminal = io.getTextTerminal();
		terminal.println("Program options:\n");
		String format = "%2d  %s\n";
		int n = 0;
		terminal.printf(format, n, "Exit program");
		for(Method m : printMethods) {
			terminal.printf(format, ++n, getReportDescription(m));
		}
		return io.newIntInputReader()
				 .withDefaultValue(0)
				 .withMinVal(0)
				 .withMaxVal(printMethods.size())
				 .read("\nOption");
	}

	private static String getReportDescription(Method method) {
		String name = method.getName().substring(REPORT_METHOD_NAME_PREFIX.length());
		String[] words = splitByCharacterTypeCamelCase(name);
		return Arrays.stream(words).collect(joining(" "));
					 
	}

	private static List<Method> getReportMethods() {
		return Arrays.stream(FlightReportsApp.class.getDeclaredMethods())
					 .filter(m -> Modifier.isPublic(m.getModifiers()) &&
							 	  m.getName().startsWith(REPORT_METHOD_NAME_PREFIX) &&
							 	  m.getParameterTypes().length == REPORT_METHOD_PARAMETER_COUNT &&
							 	  m.getReturnType().equals(REPORT_METHOD_RETURN_TYPE))
					 .sorted((m1, m2) -> m1.getName().compareTo(m2.getName()))
					 .collect(toList());
	}
}