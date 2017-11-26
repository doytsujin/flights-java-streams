package airtraffic;

import java.util.Map;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.BiConsumer;

import airtraffic.Flight.CancellationCode;

/**
 * Aggregate statistics for an airport. 
 *
 * @author tony@piazzaconsulting.com
 */
public class AirportMetrics extends FlightBasedMetrics<Airport> {
   private LongAdder totalCancelledCarrier = new LongAdder();
   private LongAdder totalCancelledWeather = new LongAdder();
   private LongAdder totalCancelledNAS = new LongAdder();
   private LongAdder totalCancelledSecurity = new LongAdder();
   private LongAdder totalOrigins = new LongAdder();
   private LongAdder totalDestinations = new LongAdder();

   public AirportMetrics(Airport airport) {
      super(airport);
   }

   public AirportMetrics addFlight(Flight flight) {
      if(flight.getOrigin().equals(getSubject())) {
         totalOrigins.increment();
         // cancellations are counted only for the origin airport
         if(flight.cancelled()) {
            totalCancelled.increment();
            switch(flight.getCancellationCode()) {
               case CARRIER:  totalCancelledCarrier.increment();  break;
               case WEATHER:  totalCancelledWeather.increment();  break;
               case NAS:      totalCancelledNAS.increment();      break;
               case SECURITY: totalCancelledSecurity.increment(); break;
            }
         }
      } else if(flight.getDestination().equals(getSubject())) {
         totalDestinations.increment();
         // diversions are counted only for the destination airport
         if(flight.diverted()) {
            totalDiverted.increment();
         }
      } else {
         throw new IllegalArgumentException("Wrong airport");
      }

      totalFlights.increment();
      return this;
   }

   public long getTotalOrigins() {
      return totalOrigins.longValue();
   }

   public long getTotalDestinations() {
      return totalDestinations.longValue();
   }

   public long getTotalCancelledByCode(CancellationCode code) {
      switch(code) {
         case CARRIER:  return totalCancelledCarrier.longValue();
         case WEATHER:  return totalCancelledWeather.longValue();
         case NAS:      return totalCancelledNAS.longValue();
         case SECURITY: return totalCancelledSecurity.longValue();
         default:       return 0;
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
                         throw new IllegalArgumentException("Wrong airport");
                      }
                      AirportMetrics result = new AirportMetrics(metrics1.getSubject());
                      result.totalFlights.add(metrics1.totalFlights.longValue()); 
                      result.totalFlights.add(metrics2.totalFlights.longValue());
                      result.totalCancelled.add(metrics1.totalCancelled.longValue());
                      result.totalCancelled.add(metrics2.totalCancelled.longValue());
                      result.totalDiverted.add(metrics1.totalDiverted.longValue());
                      result.totalDiverted.add(metrics2.totalDiverted.longValue());
                      return result;
                   });
                }
             });
      };
   }
}