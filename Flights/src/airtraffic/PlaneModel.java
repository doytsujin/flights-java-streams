package airtraffic;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Immutable domain class that represents a specific model built by plane manufacturer.
 * 
 * @author tony@piazzaconsulting.com
 */
public class PlaneModel implements Comparable<PlaneModel> {
   private final String manufacturer;
   private final String modelNumber;

   public PlaneModel(String manufacturer, String modelNumber) {
      this.manufacturer = manufacturer;
      this.modelNumber = modelNumber;
   }

   public String getManufacturer() {
      return manufacturer;
   }

   public String getModelNumber() {
      return modelNumber;
   }

   @Override
   public boolean equals(Object obj) {
      return EqualsBuilder.reflectionEquals(this, obj, false);
   }

   @Override
   public int hashCode() {
      return this.manufacturer.hashCode() + this.modelNumber.hashCode();
   }

   @Override
   public String toString() {
      return ToStringBuilder.reflectionToString(this);
   }

   @Override
   public int compareTo(PlaneModel other) {
      return new CompareToBuilder().append(this.manufacturer, other.manufacturer)
                                   .append(this.modelNumber, other.modelNumber)
                                   .toComparison();
   }
}