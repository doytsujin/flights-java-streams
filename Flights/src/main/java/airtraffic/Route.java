package airtraffic;

/**
 * Immutable domain class used to represent the pairing of 2 airports.
 *
 * @author Tony Piazza <tony@piazzaconsulting.com>
 */
public class Route implements Comparable<Route> {
   private final String first;
   private final String second;

   public Route(String first, String second) {
      this.first = first;
      this.second = second;
   }

   @Override
   public boolean equals(Object obj) {
      if(obj == null || !(obj instanceof Route)) {
         return false;
      }
      Route other = (Route)obj;
      return (this.first.equals(other.first) && this.second.equals(other.second)) ||
             (this.first.equals(other.second) && this.second.equals(other.first));
   }

   @Override
   public int hashCode() {
      return this.first.hashCode() + this.second.hashCode();
   }

   @Override
   public String toString() {
      return first.compareTo(second) >= 0 ? 
         first + " <-> " + second : 
         second + " <-> " + first;
   }

   @Override
   public int compareTo(Route other) {
      return this.toString().compareTo(other.toString());
   }
}