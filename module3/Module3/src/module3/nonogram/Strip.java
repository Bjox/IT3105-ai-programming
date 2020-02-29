package module3.nonogram;

import java.util.ArrayList;

/**
 *
 * @author Bjox
 */
public class Strip {
	
	private final int[] spec;
	private final int[] segmentStart;
	private final int length;
	

	public Strip(int[] spec, int length) {
		this.spec = spec;
		this.length = length;
		segmentStart = new int[spec.length];
		
		int i = 0;
		int nextStart = 0;
		
		for (int segmentLength : spec) {
			if (segmentLength < 1) throw new IllegalArgumentException("Invalid segment specification (" + segmentLength + ").");
			
			segmentStart[i++] = nextStart;
			nextStart += segmentLength + 1;
		}
		
	}
	
	
	public Strip(Strip original) {
		this.spec = original.spec;
		this.length = original.length;
		segmentStart = new int[original.segmentStart.length];
		System.arraycopy(original.segmentStart, 0, segmentStart, 0, segmentStart.length);
	}
	
	
	public int[] getSpec() {
		return spec;
	}
	
	
	public int[] getSegmentStart() {
		return segmentStart;
	}
	
	
	public boolean isFilled(int i) {
		if (i >= length) throw new ArrayIndexOutOfBoundsException(i);
		
		for (int j = spec.length - 1; j >= 0; j--) {
			if (segmentStart[j] <= i)
				return segmentStart[j] + spec[j] - 1 >= i;
		}
		
		return false;
	}
	
	
	private boolean canPushSegment(int segmentIndex) {
		return segmentIndex < spec.length &&
				segmentStart[spec.length - 1] + spec[spec.length - 1] < length;
	}
	
	
	private boolean pushSegment(int segmentIndex) {
		if (!canPushSegment(segmentIndex)) return false;
		
		for (int j = segmentIndex; j < spec.length; j++)
			segmentStart[j]++;
		
		return true;
	}
	
	
	public int numberOfSegments() {
		return spec.length;
	}
	
	
	public Strip[] getPermutations() {
		ArrayList<Strip> strips = new ArrayList<>();
		strips.add(this);
		recursive_permutation(new Strip(this), 0, strips);
		return strips.toArray(new Strip[0]);
	}
	
	
	public int getBitField(boolean reverse) {
		int bits = 0;
		for (int i = 0; i < length; i++) {
			if (isFilled(reverse ? length - i - 1 : i)) bits += 1 << i;
		}
		return bits;
	}

	
	@Override
	public String toString() {
		String str = "[";
		
		for (int i = 0; i < length; i++)
			str += isFilled(i) ? "#" : "-";
		
		return str + "]";
	}
	
	
	private static void recursive_permutation(Strip strip, int segment_to_push, ArrayList<Strip> permutations) {
		while (strip.canPushSegment(segment_to_push)) {
			if (segment_to_push + 1 < strip.numberOfSegments())
				recursive_permutation(new Strip(strip), segment_to_push + 1, permutations);
			
			strip.pushSegment(segment_to_push);
			permutations.add(new Strip(strip));
		}
	}
	
}
