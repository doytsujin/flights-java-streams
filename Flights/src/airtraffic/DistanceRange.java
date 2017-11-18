package airtraffic;

import org.apache.commons.lang3.Range;

/**
 * Represents a range of distances.
 *
 * @author tony@piazzaconsulting.com
 */
public class DistanceRange implements Comparable<DistanceRange> {
	private Range<Integer> range;

	private DistanceRange(int start, int end) {
		this.range = Range.between(start, end);
	}

	public static DistanceRange between(int start, int end) {
		return new DistanceRange(start, end);
	}

	public boolean contains(int value) {
		return range.contains(value);
	}

	@Override
	public int compareTo(DistanceRange other) {
		return this.range.getMaximum() - other.range.getMinimum(); 
	}

	@Override
	public String toString() {
		return String.format("%,d to %,d",range.getMinimum(), range.getMaximum());
	}

	@Override
	public boolean equals(Object other) {
		return range.equals(other);
	}

	@Override
	public int hashCode() {
		return range.hashCode();
	}
}