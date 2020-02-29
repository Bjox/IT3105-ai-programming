package module3;

/**
 *
 * @author Bjox
 */
public class LoadingBar {
	
	private final int length;
	private final int maxVal;
	
	private int lastFilled = 0;
	private int step = 0;

	public LoadingBar(int length, int maxVal) {
		this.length = length;
		this.maxVal = maxVal;
	}
	
	
	public void step() {
		updateProgress(++step);
	}
	
	
	public void updateProgress(int val) {	
		if (val > maxVal) val = maxVal;
		
		double r = val / (double)maxVal;
		int filled = (int)(r * length);
		
		if (filled == lastFilled) return;
		lastFilled = filled;
		r = ((int)(r*10000))/100.0;
		
		System.out.print("[");
		
		for (int i = 0; i < filled; i++)
			System.out.print("=");
		
		for (int i = 0; i < length - filled; i++)
			System.out.print("-");
		
		System.out.print("]");
		
		System.out.print(" " + r + "%  \r");
	}
	
	public void finish() {
		updateProgress(maxVal);
		System.out.println();
	}
	
}
