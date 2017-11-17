package airtraffic;

import static java.util.stream.Collectors.toMap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import org.simpleflatmapper.csv.CellValueReader;
import org.simpleflatmapper.csv.CsvMapper;
import org.simpleflatmapper.csv.CsvMapperFactory;
import org.simpleflatmapper.csv.CsvParser;

import airtraffic.Plane.AircraftType;
import airtraffic.Plane.EngineType;
import airtraffic.Plane.OwnershipType;

/**
 * Utility class used to load various data files in CSV format and make the 
 * data available via methods that returns Maps or Streams of specific domain 
 * types.
 *
 * @author tony@piazzaconsulting.com
 */
public class ReferenceData {
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("mm/dd/yyyy");

	private Map<String, Airport> _airports;
	private Map<String, Carrier> _carriers;
	private Map<String, Plane> _planes;

	public ReferenceData() throws IOException {
		loadAirports(new File("data/airports.csv"));
		loadCarriers(new File("data/carriers.csv"));
		loadPlanes(new File("data/planes.csv"),
				   new File("data/cancelled-planes.csv"));
	}

	private BufferedReader getReader(File path) throws IOException {
		return new BufferedReader(new FileReader(path));
	}

	private void loadAirports(File sourceFile) throws IOException {
		_airports = Collections.unmodifiableMap(
			CsvParser.skip(1) // skip header
					 .mapTo(Airport.class).headers("IATA", "name", "city", 
						                           "state", "country", 
						                           "latitude", "longitude")
					 .stream(getReader(sourceFile))
					 .collect(toMap(Airport::getIATA, Function.identity()))
		);
	}

	private void loadCarriers(File sourceFile) throws IOException {
		_carriers = Collections.unmodifiableMap(
			CsvParser.skip(1) // skip header
					 .mapTo(Carrier.class).headers("code", "name")
					 .stream(getReader(sourceFile))
					 .collect(toMap(Carrier::getCode, Function.identity()))
		);
	}

	private static final CellValueReader<Date> DATE_VALUE_READER =
		(chars, offset, length, ctx) -> {
			Date result = null;
			try {
				result = DATE_FORMAT.parse(String.valueOf(chars, offset, length));
			} catch (ParseException e) {
				/* eat it */
			}
			return result;
		};

	private static final CellValueReader<Integer> INT_VALUE_READER = 
		(chars, offset, length, ctx) -> {
			String value = String.valueOf(chars, offset, length);
			int result = 0;
			try {
				result = new Integer(value);
			} catch(NumberFormatException e) {
				/* eat it */
			}
			return result;
		};

	private Stream<Plane> loadCancelledPlanes(File sourceFile) throws IOException {
		final CsvMapper<Plane> mapper = CsvMapperFactory.newInstance()
				.addCustomValueReader("cancelDate", DATE_VALUE_READER)
				.addCustomValueReader("year", INT_VALUE_READER)
				.newBuilder(Plane.class)
				.addMapping("tailNumber")
				.addMapping("serialNumber")
				.addMapping("MMSCode")
				.addMapping("manufacturer")
				.addMapping("modelNumber")
				.addMapping("year")
				.addMapping("cancelDate")
				.addMapping("name")
				.addMapping("street")
				.addMapping("city")
				.addMapping("state")
				.addMapping("zipCode")
				.mapper();

		// Ignore planes cancelled after the flight data was collected
		final Date cutoff = new Calendar.Builder()
										.setDate(2009, 1, 1)
										.build()
										.getTime();

		if(sourceFile.exists()) {
			Reader source = new BufferedReader(new FileReader(sourceFile));
			return CsvParser.skip(1)		// skip header
					 		.mapWith(mapper)
					 		.stream(source)
					 		.filter(p -> p.getCancelDate() != null && 
					 				p.getCancelDate().before(cutoff));
		} else {
			return Stream.empty();
		}
	}

	private void loadPlanes(File sourceFile1, File sourceFile2) throws IOException {
		final CsvMapper<Plane> mapper = CsvMapperFactory.newInstance()
			.addCustomValueReader("ownershipType", (chars, offset, length, ctx) -> {
				return OwnershipType.get(String.valueOf(chars, offset, length));
			}).addCustomValueReader("issueDate", DATE_VALUE_READER)
			.addCustomValueReader("aircraftType", (chars, offset, length, ctx) -> {
				return AircraftType.get(String.valueOf(chars, offset, length));
			}).addCustomValueReader("engineType", (chars, offset, length, ctx) -> {
				return EngineType.get(String.valueOf(chars, offset, length));
			}).addCustomValueReader("year", INT_VALUE_READER)
			.newBuilder(Plane.class)
			.addMapping("tailNumber")
			.addMapping("ownershipType")
			.addMapping("manufacturer")
			.addMapping("issueDate")
			.addMapping("modelNumber")
			.addMapping("status")
			.addMapping("aircraftType")
			.addMapping("engineType")
			.addMapping("year")
			.mapper();

		_planes = Collections.unmodifiableMap(
			Stream.concat(
				CsvParser.skip(1)		// skip header
						 .mapWith(mapper)
						 .stream(getReader(sourceFile1)),
				loadCancelledPlanes(sourceFile2)
	            ).collect(toMap(Plane::getTailNumber, Function.identity()))
			);
	}

	public Map<String, Airport> getAirportMap() {
		return _airports;
	}

	public Stream<Airport> getAirportStream() {
		return _airports.values().stream();
	}

	public Map<String, Carrier> getCarrierMap() {
		return _carriers;
	}

	public Stream<Carrier> getCarrierStream() {
		return _carriers.values().stream();
	}

	public Map<String, Plane> getPlaneMap() {
		return _planes;
	}

	public Stream<Plane> getPlaneStream() {
		return _planes.values().stream();
	}
}