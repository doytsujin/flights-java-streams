package airtraffic;

import java.util.List;
import java.util.function.Function;

import org.apache.commons.lang3.Range;

/**
 * Represents a range of distances.
 *
 * @author tony@piazzaconsulting.com
 */
public class IntRange implements Comparable<IntRange> {
	private Range<Integer> range;

	private IntRange(int start, int end) {
		this.range = Range.between(start, end);
	}

	public static IntRange between(int start, int end) {
		return new IntRange(start, end);
	}

	public boolean contains(int value) {
		return range.contains(value);
	}

	public static Function<Flight, IntRange> classifier(List<IntRange> ranges) {
		return f -> ranges.stream()
						  .filter(r -> r.contains(f.getDistance()))
						  .findAny()
						  .get();
	}

	@Override
	public int compareTo(IntRange other) {
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