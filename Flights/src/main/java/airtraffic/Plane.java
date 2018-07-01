package airtraffic;

import java.time.LocalDate;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Domain class that represents a plane scheduled for use on an airline flight.
 * 
 * The base data can be downloaded here:
 * 
 * http://stat-computing.org/dataexpo/2009/plane-data.csv
 *
 * There is also data for planes with cancelled registrations here:
 * 
 * http://registry.faa.gov/aircraftrenewal_reports/CanceledReg_Inquiry.aspx
 *
 * @author Tony Piazza <tony@piazzaconsulting.com>
 */
public class Plane implements Comparable<Plane> {
   public enum OwnershipType {
      CORPORATION, CO_OWNER, INDIVIDUAL, PARTNERSHIP, FOREIGN_CORPORATION, UNKNOWN;

      public static OwnershipType get(String value) {
         if("Corporation".equals(value)) {
            return CORPORATION;
         } else if("Co-Owner".equals(value)) {
            return CO_OWNER;
         } else if("Individual".equals(value)) {
            return INDIVIDUAL;
         } else if("Partnership".equals(value)) {
            return PARTNERSHIP;
         } else if("Foreign Corporation".equals(value)) {
            return FOREIGN_CORPORATION;
         } else {
            return UNKNOWN;
         }
      }
   }

   public enum AircraftType {
      FIXED_WING_MULTI_ENGINE, FIXED_WING_SINGLE_ENGINE, ROTORCRAFT, BALLOON, UNKNOWN;

      public static AircraftType get(String value) {
         if("Fixed Wing Multi-Engine".equals(value)) {
            return FIXED_WING_MULTI_ENGINE;
         } else if("Fixed Wing Single-Engine".equals(value)) {
            return FIXED_WING_SINGLE_ENGINE;
         } else if("Rotorcraft".equals(value)) {
            return ROTORCRAFT;
         } else if("Balloon".equals(value)) {
            return BALLOON;
         } else {
            return UNKNOWN;
         }         
      }
   }

   public enum EngineType {
      TURBO_FAN, TURBO_JET, RECIPROCATING, TURBO_PROP, TURBO_SHAFT, NONE, FOUR_CYCLE, UNKNOWN;

      public static EngineType get(String value) {
         if("Turbo-Fan".equals(value)) {
            return TURBO_FAN;
         } else if("Turbo-Jet".equals(value)) {
            return TURBO_JET;
         } else if("Reciprocating".equals(value)) {
            return RECIPROCATING;
         } else if("Turbo-Prop".equals(value)) {
            return TURBO_PROP;
         } else if("Turbo-Shaft".equals(value)) {
            return TURBO_SHAFT;
         } else if("None".equals(value)) {
            return NONE;
         } else if("4 Cycle".equals(value)) {
            return FOUR_CYCLE;
         } else {
            return UNKNOWN;
         }
      }
   }

   private String tailNumber;
   private OwnershipType ownershipType = OwnershipType.UNKNOWN;
   private LocalDate issueDate;
   private String manufacturer = "UNKNOWN";
   private String modelNumber = "UNKNOWN";
   private String status = "UNKNOWN";
   private AircraftType aircraftType = AircraftType.UNKNOWN;
   private EngineType engineType = EngineType.UNKNOWN;
   private int year;

   public String getTailNumber() {
      return tailNumber;
   }

   public void setTailNumber(String tailNumber) {
      this.tailNumber = tailNumber;
   }

   public OwnershipType getOwnershipType() {
      return ownershipType;
   }

   public void setOwnershipType(OwnershipType ownershipType) {
      this.ownershipType = ownershipType;
   }

   public String getManufacturer() {
      return manufacturer;
   }

   public void setManufacturer(String manufacturer) {
      this.manufacturer = manufacturer;
   }

   public LocalDate getIssueDate() {
      return issueDate;
   }

   public void setIssueDate(LocalDate issueDate) {
      this.issueDate = issueDate;
   }

   public String getModelNumber() {
      return modelNumber;
   }

   public void setModelNumber(String modelNumber) {
      this.modelNumber = modelNumber;
   }

   public String getStatus() {
      return status;
   }

   public void setStatus(String status) {
      this.status = status;
   }

   public AircraftType getAircraftType() {
      return aircraftType;
   }

   public void setAircraftType(AircraftType aircraftType) {
      this.aircraftType = aircraftType;
   }

   public EngineType getEngineType() {
      return engineType;
   }

   public void setEngineType(EngineType engineType) {
      this.engineType = engineType;
   }

   public int getYear() {
      return year;
   }

   public void setYear(int year) {
      this.year = year;
   }

   public PlaneModel getModel() {
      return new PlaneModel(this.manufacturer, this.modelNumber);
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
   public int compareTo(Plane other) {
      return new CompareToBuilder().append(this.manufacturer, other.manufacturer)
                                   .append(this.modelNumber, other.modelNumber)
                                   .append(this.tailNumber, other.tailNumber)
                                   .toComparison();
   }
}