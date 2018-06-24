package airtraffic;

/**
 * A physical location on the earth.
 *
 * @author tony@piazzaconsulting.com
 */
public interface GeoLocation {
   public enum Units { MILES, KILOMETERS }

   double getLatitude();
   double getLongitude();
}