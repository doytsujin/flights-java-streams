package airtraffic;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Domain class that represents the physical location of the origin and 
 * destination of a flight. The data can be downloaded here: 
 *
 *	http://stat-computing.org/dataexpo/2009/airports.csv
 *
 * @author tony@piazzaconsulting.com
 */
public class Airport implements Comparable<Airport>, GeoLocation {
	private String iata;
	private String name;
	private String city;
	private String state;
	private String country;
	private double latitude;
	private double longitude;

	public String getIATA() {
		return iata;
	}

	public void setIATA(String iata) {
		this.iata = iata;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
	
	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	@Override
	public int compareTo(Airport other) {
		return this.iata.compareTo(other.iata);
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