package airtraffic;

import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Represents the configuration loaded from a YAML file.
 *
 * @author Tony Piazza <tony@piazzaconsulting.com>
 */
public final class Config {
   private String airportPath;
   private String carrierPath;
   private String planePath;
   private Map<Integer, String> flightPaths;

   public String getAirportPath() {
      return airportPath;
   }

   public void setAirportPath(String airportPath) {
      this.airportPath = airportPath;
   }

   public String getCarrierPath() {
      return carrierPath;
   }

   public void setCarrierPath(String carrierPath) {
      this.carrierPath = carrierPath;
   }

   public String getPlanePath() {
      return planePath;
   }

   public void setPlanePath(String planePath) {
      this.planePath = planePath;
   }

   public Map<Integer, String> getFlightPaths() {
      return flightPaths;
   }

   public void setFlightPaths(Map<Integer, String> flightPaths) {
      this.flightPaths = flightPaths;
   }

   @Override
   public String toString() {
      return ToStringBuilder.reflectionToString(this);
   }
}