package module3.nonogram;

/**
 *
 * @author Bjox
 */
public class Nonogram {
	
	public final int X, Y, COLUMNS, ROWS;
	
	public final int[][] rowspec;
	public final int[][] columnspec;
	
	private int rowc = 0, columnc = 0;

	public Nonogram(int x, int y) {
		this.X = x;
		this.Y = y;
		this.COLUMNS = x;
		this.ROWS = y;
		
		rowspec = new int[ROWS][];
		columnspec = new int[COLUMNS][];
	}
	
	
	public void addRowSpec(int[] spec) {
		rowspec[rowc++] = spec;
	}
	
	
	public void addColumnSpec(int[] spec) {
		columnspec[columnc++] = spec;
	}

	
	
	
	
}
