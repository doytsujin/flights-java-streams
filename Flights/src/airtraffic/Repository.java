package airtraffic;

import static java.util.stream.Collectors.toMap;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import org.simpleflatmapper.csv.CellValueReader;
import org.simpleflatmapper.csv.CsvMapper;
import org.simpleflatmapper.csv.CsvMapperFactory;
import org.simpleflatmapper.csv.CsvParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import airtraffic.Plane.AircraftType;
import airtraffic.Plane.EngineType;
import airtraffic.Plane.OwnershipType;

/**
 * Provides access to the data used by the application classes.
 *
 * @author tony@piazzaconsulting.com
 */
public final class Repository {
   private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("mm/dd/yyyy");
   private static final String[] AIRPORT_HEADERS = 
      { "IATA", "name", "city", "state", "country", "latitude", "longitude" };
   private static final String[] CARRIER_HEADERS = { "code", "name" };
   private static final CellValueReader<LocalDate> DATE_VALUE_READER =
      (chars, offset, length, ctx) -> {
         LocalDate result = null;
         try {
            result = LocalDate.parse(String.valueOf(chars, offset, length), DATE_FORMAT);
         } catch (DateTimeParseException e) {
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
   private final Logger logger = LoggerFactory.getLogger(Repository.class);
   private final Path airportPath;
   private final Path carrierPath;
   private final Path planePath;
   private final Map<Integer, Path> flightPaths;
   private Map<String, Airport> airportMap;
   private Map<String, Carrier> carrierMap;
   private Map<String, Plane> planeMap;

   public Repository() {
      Reader reader;
      try {
         reader = new FileReader("config.yaml");
      } catch (FileNotFoundException e) {
         throw new RepositoryException(e);
      }
      Yaml yaml = new Yaml(new Constructor(Config.class));
      Config config = yaml.load(reader);
      Map<Integer, String> pathMap = config.getFlightPaths();
      this.flightPaths = new HashMap<>();
      Path path;
      for(Entry<Integer, String> e: pathMap.entrySet()) {
         path = Paths.get(e.getValue());
         if(Files.exists(path)) {
            this.flightPaths.put(e.getKey(), path);
            logger.debug("Found valid path: {}", path);
         } else {
            logger.warn("Invalid path: {}", path);
         }
      }
      if(this.flightPaths.isEmpty()) {
         throw new IllegalStateException("No flight data found");
      }
      this.airportPath = getAndValidatePath(config.getAirportPath());
      this.carrierPath = getAndValidatePath(config.getCarrierPath());
      this.planePath = getAndValidatePath(config.getPlanePath());
   }

   public Stream<Airport> getAirportStream() {
      try {
         return CsvParser.skip(1)         // skip header
                         .mapTo(Airport.class)
                         .headers(AIRPORT_HEADERS)
                         .stream(getReader(airportPath));
      } catch (IOException e) {
         throw new RepositoryException(e);
      }
   }

   private Map<String, Airport> getAirportMap() {
      if(airportMap == null) {
         airportMap = getAirportStream().collect(toMap(Airport::getIATA, Function.identity()));
      }
      return airportMap;
   }

   public Airport getAirport(String iata) {
      return getAirportMap().get(iata);
   }

   public Stream<Carrier> getCarrierStream() {
      try {
         return CsvParser.skip(1)         // skip header
                         .mapTo(Carrier.class)
                         .headers(CARRIER_HEADERS)
                         .stream(getReader(carrierPath));
      } catch (IOException e) {
         throw new RepositoryException(e);
      }
   }

   private Map<String, Carrier> getCarrierMap() {
      if(carrierMap == null) {
         carrierMap = getCarrierStream().collect(toMap(Carrier::getCode, Function.identity()));
      }
      return carrierMap;
   }

   public Carrier getCarrier(String code) {
      return getCarrierMap().get(code);
   }

   public Stream<Flight> getFlightStream(int year) {
      Path path = flightPaths.get(year);
      if(path == null) {
         throw new IllegalArgumentException("No flight data for year " + year);
      }
      try {
         return Files.lines(path)
                     .skip(1)            // skip header
                     .map(s -> new Flight(s, this));
      } catch (IOException e) {
         throw new RepositoryException(e);
      }
   }

   public Stream<Plane> getPlaneStream() {
      try {
         return CsvParser.skip(1)         // skip header
                         .mapWith(PLANE_MAPPER)
                         .stream(getReader(planePath));
      } catch (IOException e) {
         throw new RepositoryException(e);
      }
   }

   private Map<String, Plane> getPlaneMap() {
      if(planeMap == null) {
         planeMap = getPlaneStream().collect(toMap(Plane::getTailNumber, Function.identity()));
      }
      return planeMap;
   }

   public Plane getPlane(String tailNumber) {
      return getPlaneMap().get(tailNumber);
   }

   public Set<Integer> getFlightYears() {
      return flightPaths.keySet();
   }

   private BufferedReader getReader(Path path) throws IOException {
      return new BufferedReader(new FileReader(path.toFile()));
   }

   private Path getAndValidatePath(String path) {
      Path result = Paths.get(path);
      if(Files.notExists(result)) {
         throw new IllegalStateException("Invalid path: " + path);
      }
      logger.debug("Found valid path: {}", path);
      return result;
   }
}