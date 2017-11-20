package airtraffic;

import static java.util.stream.Collectors.toMap;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

public final class Repository {
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("mm/dd/yyyy");
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
	private final static CsvMapper<Plane> PLANE_MAPPER = 
		CsvMapperFactory.newInstance()
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
	private final Path airportPath;
	private final Path carrierPath;
	private final Path planePath;
	private final Path flightPath;
	private final int flightYear;
	private Map<String, Airport> airportMap;
	private Map<String, Carrier> carrierMap;
	private Map<String, Plane> planeMap;

	public Repository() {
		// TODO: Move these to a YAML file
		airportPath = Paths.get("data/airports.csv");
		carrierPath = Paths.get("data/carriers.csv");
		planePath = Paths.get("data/planes.csv");
		flightPath = Paths.get("data/flights-2008.csv");
		flightYear = 2008;
	}

	public Stream<Airport> getAirportStream() {
		try {
			return CsvParser.skip(1)			// skip header
							.mapTo(Airport.class)
							.headers("IATA", "name", "city", "state", "country", "latitude", "longitude")
							.stream(getReader(airportPath));
		} catch (IOException e) {
			throw new RepositoryException(e);
		}
	}

	public Map<String, Airport> getAirportMap() {
		if(airportMap == null) {
			airportMap = getAirportStream().collect(toMap(Airport::getIATA, Function.identity()));
		}
		return airportMap;
	}

	public Stream<Carrier> getCarrierStream() {
		try {
			return CsvParser.skip(1)			// skip header
							.mapTo(Carrier.class).headers("code", "name")
							.stream(getReader(carrierPath));
		} catch (IOException e) {
			throw new RepositoryException(e);
		}
	}

	public Map<String, Carrier> getCarrierMap() {
		if(carrierMap == null) {
			carrierMap = getCarrierStream().collect(toMap(Carrier::getCode, Function.identity()));
		}
		return carrierMap;
	}

	public Stream<Flight> getFlightStream() {
		try {
			return Files.lines(flightPath)
						.skip(1)				// skip header
						.map(s -> new Flight(s, this));
		} catch (IOException e) {
			throw new RepositoryException(e);
		}
	}

	public Stream<Plane> getPlaneStream() {
		try {
			return CsvParser.skip(1)			// skip header
							.mapWith(PLANE_MAPPER)
							.stream(getReader(planePath));
		} catch (IOException e) {
			throw new RepositoryException(e);
		}
	}

	public Map<String, Plane> getPlaneMap() {
		if(planeMap == null) {
			planeMap = getPlaneStream().collect(toMap(Plane::getTailNumber, Function.identity()));
		}
		return planeMap;
	}

	public int getFlightYear() {
		return flightYear;
	}

	private BufferedReader getReader(Path path) throws IOException {
		return new BufferedReader(new FileReader(path.toFile()));
	}
}