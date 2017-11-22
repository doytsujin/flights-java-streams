package airtraffic;

import static java.lang.Integer.parseInt;
import static org.apache.commons.lang3.StringUtils.length;

import java.time.LocalDate;
import java.time.YearMonth;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;


/**
 * Domain class that represents a scheduled event for flying an aircraft 
 * between an origin and a destination. This is the central class in
 * this project and is used to query the large dataset made available
 * by American Statistical Association. The data can be downloaded here:
 *
 *   http://stat-computing.org/dataexpo/2009/the-data.html
 *
 * @author tony@piazzaconsulting.com
 */
public class Flight {
   public enum CancellationCode {
      CARRIER, WEATHER, NAS, SECURITY;

      public static CancellationCode get(String code) {
         if("A".equals(code)) {
            return CARRIER;
         } else if("B".equals(code)) {
            return WEATHER;
         } else if("C".equals(code)) {
            return NAS;
         } else if("D".equals(code)) {
            return SECURITY;
         } else {
            return null;
         }
      }
   }

   private int _year;                            // 1987-2008
   private int _month;                           // 1-12
   private int _dayOfMonth;                      // 1-31
   private int _dayOfWeek;                       // 1 (Monday) - 7 (Sunday)
   private int _departureTime;                   // actual departure time (local, hhmm)
   private int _CRSDepartureTime;                // scheduled departure time (local, hhmm)
   private int _arrivalTime;                     // actual arrival time (local, hhmm)
   private int _CRSArrivalTime;                  // scheduled arrival time (local, hhmm)
   private Carrier _carrier   ;                  // unique carrier code, mapped to instance of Carrier
   private int _flightNumber;                    // flight number
   private String _tailNumber;                   // plane tail number
   private int _actualElapsedTime;               // in minutes
   private int _CRSElapsedTime;                  // in minutes
   private int _airTime;                         // in minutes
   private int _arrivalDelay;                    // in minutes
   private int _departureDelay;                  // in minutes
   private Airport _origin;                      // origin IATA airport code, mapped to instance of Airport
   private Airport _destination;                 // destination IATA airport code, mapped to instance of Airport
   private int _distance;                        // in miles
   private int _taxiIn;                          // taxi in time, in minutes
   private int _taxiOut;                         // taxi out time, in minutes
   private boolean _cancelled;                   // was the flight cancelled?
   private CancellationCode _cancellationCode;   // reason for cancellation (A = carrier, B = weather, C = NAS, D = security)
   private boolean _diverted;                    // 1 = yes, 0 = no
   private int _carrierDelay;                    // in minutes
   private int _weatherDelay;                    // in minutes
   private int _NASDelay;                        // in minutes
   private int _securityDelay;                   // in minutes
   private int _lateAircraftDelay;               // in minutes

   private Plane plane;
   private Route route;
   private LocalDate date;

   public Flight(String input, Repository repository) {
      String[] source = input.split(",");
      _year = parseInt(source[0]);
      _month = parseInt(source[1]);
      _dayOfMonth = parseInt(source[2]);
      _dayOfWeek = parseInt(source[3]);
      _departureTime = "NA".equals(source[4]) ? 0 : parseInt(source[4]);
      _CRSDepartureTime = parseInt(source[5]);
      _arrivalTime = "NA".equals(source[6]) ? 0 : parseInt(source[6]);
      _CRSArrivalTime = parseInt(source[7]);
      _carrier = repository.getCarrierMap().get(source[8]);
      _flightNumber = parseInt(source[9]);
      if(length(source[10]) > 0) {
         _tailNumber = source[10];
      }
      _actualElapsedTime = "NA".equals(source[11]) ? 0 : parseInt(source[11]);
      _CRSElapsedTime = "NA".equals(source[12]) ? 0 : parseInt(source[12]);
      _airTime = "NA".equals(source[13]) ? 0 : parseInt(source[13]);
      _arrivalDelay = "NA".equals(source[14]) ? 0 : parseInt(source[14]);
      _departureDelay = "NA".equals(source[15]) ? 0 : parseInt(source[15]);
      _origin = repository.getAirportMap().get(source[16]);
      _destination = repository.getAirportMap().get(source[17]);
      _distance = parseInt(source[18]);
      _taxiIn = "NA".equals(source[19]) ? 0 : parseInt(source[19]);
      _taxiOut = "NA".equals(source[20]) ? 0 : parseInt(source[20]);
      _cancelled = source[21].equals("1");
      _cancellationCode = CancellationCode.get(source[22]);
      _diverted = source[23].equals("1");
      _carrierDelay = "NA".equals(source[24]) ? 0 : parseInt(source[24]);
      _weatherDelay = "NA".equals(source[25]) ? 0 : parseInt(source[25]);
      _NASDelay = "NA".equals(source[26]) ? 0 : parseInt(source[26]);
      _securityDelay = "NA".equals(source[27]) ? 0 : parseInt(source[27]);
      _lateAircraftDelay = "NA".equals(source[28]) ? 0 : parseInt(source[28]);

      plane = repository.getPlaneMap().get(_tailNumber);
      if(plane == null) {
         plane = new Plane();
         plane.setTailNumber(_tailNumber);
      }
      route = new Route(_origin.getIATA(), _destination.getIATA());
      date = LocalDate.of(_year, _month, _dayOfMonth);
   }

   public String describeRoute() {
      return _origin + " to " + _destination;
   }

   public YearMonth getYearMonth() {
      return YearMonth.from(date);
   }

   public Plane getPlane() {
      return plane;
   }

   public Route getRoute() {
      return route;
   }

   public LocalDate getDate() {
      return date;
   }

   public boolean validTailNumber() {
      return _tailNumber != null;
   }

   public int getYear() {
      return _year;
   }

   public int getMonth() {
      return _month;
   }

   public int getDayOfMonth() {
      return _dayOfMonth;
   };

   public int getDayOfWeek() {
      return _dayOfWeek;
   }

   public int getDepartureTime() {
      return _departureTime;
   }

   public int getCRSDepartureTime() {
      return _CRSDepartureTime;
   }

   public int getArrivalTime() {
      return _arrivalTime;
   }

   public int getCRSArrivalTime() {
      return _CRSArrivalTime;
   }

   public Carrier getCarrier() {
      return _carrier;
   }

   public int getFlightNumber() {
      return _flightNumber;
   }

   public String getTailNumber() {
      return _tailNumber;
   }

   public int getActualElapsedTime() {
      return _actualElapsedTime;
   }

   public int getCRSElapsedTime() {
      return _CRSElapsedTime;
   }

   public int getAirTime() {
      return _airTime;
   }

   public int getArrivalDelay() {
      return _arrivalDelay;
   }

   public int getDepartureDelay() {
      return _departureDelay;
   }

   public Airport getOrigin() {
      return _origin;
   }

   public Airport getDestination() {
      return _destination;
   }

   public int getDistance() {
      return _distance;
   }

   public int getTaxiIn() {
      return _taxiIn;
   }

   public int getTaxiOut() {
      return _taxiOut;
   }

   public boolean cancelled() {
      return _cancelled;
   }

   public boolean notCancelled() {
      return ! _cancelled;
   }

   public CancellationCode getCancellationCode() {
      return _cancellationCode;
   }

   public boolean diverted() {
      return _diverted;
   }

   public boolean notDiverted() {
      return ! _diverted;
   }

   public int getCarrierDelay() {
      return _carrierDelay;
   }

   public int getWeatherDelay() {
      return _weatherDelay;
   }

   public int getNASDelay() {
      return _NASDelay;
   }

   public int getSecurityDelay() {
      return _securityDelay;
   }

   public int getLateAircraftDelay() {
      return _lateAircraftDelay;
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
}