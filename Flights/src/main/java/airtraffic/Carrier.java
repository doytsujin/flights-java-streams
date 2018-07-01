package airtraffic;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Domain class that represents an airline carrier that provides flight service.
 * 
 * The data can be downloaded here:
 * 
 * http://stat-computing.org/dataexpo/2009/carriers.csv
 *
 * @author Tony Piazza <tony@piazzaconsulting.com>
 */
public class Carrier implements Comparable<Carrier> {
   private String code;
   private String name;

   public String getCode() {
      return code;
   }

   public void setCode(String code) {
      this.code = code;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   @Override
   public boolean equals(Object obj) {
      return EqualsBuilder.reflectionEquals(this, obj, false);
   }

   @Override
   public int hashCode() {
      return HashCodeBuilder.reflectionHashCode(this, false);
   }

   @Override
   public String toString() {
      return ToStringBuilder.reflectionToString(this);
   }

   @Override
   public int compareTo(Carrier other) {
      return this.code.compareTo(other.code);
   }
}