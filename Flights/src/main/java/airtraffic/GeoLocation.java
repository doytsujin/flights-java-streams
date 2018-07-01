package airtraffic;

/**
 * A physical location on the earth.
 *
 * @author Tony Piazza <tony@piazzaconsulting.com>
 */
public interface GeoLocation {
   public enum Units { MILES, KILOMETERS }

   double getLatitude();
   double getLongitude();
}