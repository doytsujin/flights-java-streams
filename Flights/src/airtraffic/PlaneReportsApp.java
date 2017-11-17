package airtraffic;

import static java.lang.System.out;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

import java.util.stream.Stream;

/**
 * Generate various airplane statistics using Java 8 streams.
 *
 * @author tony@piazzaconsulting.com
 */
public class PlaneReportsApp {
	public static void main(String[] args) throws Exception {
		Stream<Plane> source = new ReferenceData().getPlaneStream();
		PlaneReportsApp stats = new PlaneReportsApp();
//		stats.printPlaneCountsByManfacturer(source);
//		stats.printPlaneCountsByYear(source);
//		stats.printPlaneCountsByAircraftType(source);
		stats.printPlaneCountsByEngineType(source);
	}

	public void printPlaneCountsByManfacturer(Stream<Plane> source) {
		out.println("Manufacturer\t\t\tCount");
		out.println("---------------------------------------");
		source.collect(groupingBy(Plane::getManufacturer, counting()))
			  .entrySet()
			  .stream()
			  .sorted((e1, e2) -> e2.getValue().intValue() - e1.getValue().intValue())
			  .forEach(e -> out.printf("%-25s\t%5d\n", e.getKey(), e.getValue()));
	}

	public void printPlaneCountsByYear(Stream<Plane> source) {
		out.println("Year\tCount");
		out.println("------------------");
		source.filter(p -> p.getYear() > 0)
			  .collect(groupingBy(Plane::getYear, counting()))
			  .entrySet()
			  .stream()
			  .sorted((e1, e2) -> e2.getKey().intValue() - e1.getKey().intValue())
			  .forEach(e -> out.printf("%4d\t%5d\n", e.getKey(), e.getValue()));
	}

	public void printPlaneCountsByAircraftType(Stream<Plane> source) {
		out.println("Aircraft Type\t\t\tCount");
		out.println("---------------------------------------");
		source.collect(groupingBy(Plane::getAircraftType, counting()))
			  .entrySet()
			  .stream()
			  .sorted((e1, e2) -> e2.getValue().intValue() - e1.getValue().intValue())
			  .forEach(e -> out.printf("%-25s\t%5d\n", e.getKey(), e.getValue()));
	}

	public void printPlaneCountsByEngineType(Stream<Plane> source) {
		out.println("Engine Type\t\t\tCount");
		out.println("---------------------------------------");
		source.collect(groupingBy(Plane::getEngineType, counting()))
			  .entrySet()
			  .stream()
			  .sorted((e1, e2) -> e2.getValue().intValue() - e1.getValue().intValue())
			  .forEach(e -> out.printf("%-25s\t%5d\n", e.getKey(), e.getValue()));
	}
}