package airtraffic;

import static airtraffic.GeoLocation.Units.MILES;
import static java.lang.Math.PI;
import static java.lang.Math.acos;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

import java.util.Comparator;

/**
 * GeoLocation helper methods. 
 *
 * @author tony@piazzaconsulting.com
 */
public final class GeoHelper {
   public static Comparator<GeoLocation> distanceFromReferenceComparator(GeoLocation refLoc, GeoLocation.Units units) {
      return (loc1, loc2) -> (int)(getDistance(loc1, refLoc, units) - getDistance(loc2, refLoc, units));
   }

   public static double getDistance(GeoLocation loc1, GeoLocation loc2, GeoLocation.Units units) {
      double theta = loc1.getLongitude() - loc2.getLongitude();
      double dist = sin(deg2rad(loc1.getLatitude())) * sin(deg2rad(loc2.getLatitude())) + 
                    cos(deg2rad(loc1.getLatitude())) * cos(deg2rad(loc2.getLatitude())) * 
                    cos(deg2rad(theta));
      dist = rad2deg(acos(dist)) * 69.09;
      return units.equals(MILES) ? dist * 0.8684 : dist * 1.609344;
   }

   // This function converts decimal degrees to radians
   private static double deg2rad(double deg) {
      return (deg * PI / 180.0);
   }

   // This function converts radians to decimal degrees
   private static double rad2deg(double rad) {
      return (rad * 180.0 / PI);
   }
}