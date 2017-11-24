package airtraffic;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * Aggregate statistics for an airline carrier. 
 *
 * @author tony@piazzaconsulting.com
 */
public class CarrierMetrics extends FlightBasedMetrics<Carrier> {
   Set<String> airports = new HashSet<String>();

   public CarrierMetrics(Carrier carrier) {
      super(carrier);
   }

   @Override
   public void addFlight(Flight flight) {
      if(!flight.getCarrier().equals(getSubject())) {
         throw new IllegalArgumentException("Wrong carrier");
      }
   
      totalFlights.increment();
      if(flight.cancelled()) {
         totalCancelled.increment();
      }
      if(flight.diverted()) {
         totalDiverted.increment();
      }
      airports.add(flight.getOrigin().getIATA());
      airports.add(flight.getDestination().getIATA());
   }

   public Set<String> getAirports() {
      return Collections.unmodifiableSet(airports);
   }

   public static BiConsumer<Map<String, CarrierMetrics>, Flight> accumulator() { 
      return (map, flight) -> {
         Carrier carrier = flight.getCarrier();
         CarrierMetrics metrics = map.get(carrier.getCode());
         if(metrics == null) {
            metrics = new CarrierMetrics(carrier);
            map.put(carrier.getCode(), metrics);
         }
         metrics.addFlight(flight);
      };
   }

   public static BiConsumer<Map<String, CarrierMetrics>, Map<String, CarrierMetrics>> combiner() {
      return (map1, map2) -> {
         map1.entrySet()
             .stream()
             .forEach(e -> {
                String carrier = e.getKey();
                CarrierMetrics metrics = map2.get(carrier);
                if(metrics != null) {
                   map1.merge(carrier, metrics, (metrics1, metrics2) -> {
                      if(!metrics1.getSubject().equals(metrics2.getSubject())) {
                         throw new IllegalArgumentException("Wrong carrier");
                      }
                      CarrierMetrics result = new CarrierMetrics(metrics1.getSubject());
                      result.totalFlights.add(metrics1.totalFlights.longValue());
                      result.totalFlights.add(metrics2.totalFlights.longValue());
                      result.totalCancelled.add(metrics1.totalCancelled.longValue());
                      result.totalCancelled.add(metrics2.totalCancelled.longValue());
                      result.totalDiverted.add(metrics1.totalDiverted.longValue());
                      result.totalDiverted.add(metrics2.totalDiverted.longValue());
                      result.airports.addAll(metrics1.airports);
                      result.airports.addAll(metrics2.airports);
                      return result;
                   });
                }
             });
      };
   }
}