package module4.game.rep;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;
import module4.bot.expmax.Expectimax;
import module4.bot.expmax.heuristic.Heuristic;
import module4.game.gui.Frame;

/**
 *
 * @author Bjørnar W. Alvestad
 */
public final class Board {
	
	/** Board size. This must not be changed in any circumstances. */
	public static final int SIZE  = 4;
	
	/** Left movement constant. */
	public static final int LEFT  = 0;
	/** Right movement constant. */
	public static final int RIGHT = 1;
	/** Up movement constant. */
	public static final int UP    = 2;
	/** Down movement constant. */
	public static final int DOWN  = 3;
	
	/** Movement name. */
	public static final String[] DIR_STRING = { "LEFT", "RIGHT", "UP", "DOWN" };
	
	/** Lookup table for merging a row/column in the left direction. */
	private static final short[] lookup = new short[65536];
	/** Lookup table for the scores of all row/column merges. */
	private static final short[] score_lookup = new short[65536];
	/** An object providing the heuristic function used by expextimax. */
	private static final Heuristic heuristic;
	
	/** The random number generator used to generate random 2/4 tiles. */
	private static Random rng = new Random();
	/** The seed used in rng. */
	private static long seed;
	
	/** A long, divided into groups of 4 bits (values 0-15), where each bit group represents a cell
	    on the board. The value stored in each bit group denotes the exponent in the cell value. */
	private long boardData;
	/** Game over state. */
	private boolean gameOver;
	/** Game score. Each merge produces a score equal to the value of the merge product. */
	private int score;
	
	// Generate lookup tables and rng.
	static {
		generateLookup();
		heuristic = createHeuristic(Expectimax.DEFAULT_HEURISTIC);
	}

	/**
	 * Creates a new, empty board with score = 0.
	 */
	public Board() {
		gameOver = false;
		score = 0;
//		createRng();
	}
	
	/**
	 * Creates a new board, copying the board state (tiles), score and game over state
	 * from the specified board.
	 * @param original 
	 */
	public Board(Board original) {
		boardData = original.boardData;
		gameOver = original.gameOver;
		score = original.score;
//		rng = original.rng;
	}
	
	
	public void createRng() {
		createRng(Double.doubleToRawLongBits(Math.random()) ^ Double.doubleToRawLongBits(Math.random()));
	}
	
	
	public void createRng(long seed) {
		this.seed = seed;
		rng = new Random(seed);
	}
	
	public long getSeed() {
		return seed;
	}
	
	/**
	 * Performs a given action on this board.
	 * @param dir the direction to move
	 * @param spawnNew set to true if the board should automatically spawn a new, random tile after movement.
	 */
	public void performAction(int dir, boolean spawnNew) {
		if (gameOver)
			return;
		
		move(dir);
		
		if (spawnNew)
			spawnTileRnd();
	}
	
	/**
	 * Performs a given action on this board. This will automatically spawn a new, random tile after movement.
	 * @param dir the direction to move.
	 */
	public void performAction(int dir) {
		performAction(dir, true);
	}
	
	/**
	 * A legal move must produce a successor board with
	 * at least 1 free square for the random tile to spawn.
	 * @param dir
	 * @return 
	 */
	public boolean isLegalMove(int dir) {
		if (gameOver) return false;
		Board test_b = new Board(this);
		test_b.move(dir);
		return boardData != test_b.boardData;
		
//		if (gameOver) return false;
//		Board test_b = new Board(this);
//		test_b.move(dir);
//		return test_b.getFreeSquares() > 0;
	}
	
	/**
	 * Move/slide the board in a direction.
	 * @param dir 
	 */
	private void move(int dir) {
		switch (dir) {
			case LEFT:
				for (int row = 0; row < SIZE; row++) {
					int rowValue = reverseBitGroups(getRowValue(row));
					score += score_lookup[rowValue];
					rowValue = Short.toUnsignedInt(lookup[rowValue]);
					setRowValue(row, rowValue);
				}	break;
			case RIGHT:
				for (int row = 0; row < SIZE; row++) {
					int rowValue = getRowValue(row);
					score += score_lookup[rowValue];
					rowValue = Short.toUnsignedInt(lookup[rowValue]);
					setRowValue(row, reverseBitGroups(rowValue));
				}	break;
			case DOWN:
				for (int col = 0; col < SIZE; col++) {
					int colValue = reverseBitGroups(getColumnValue(col));
					score += score_lookup[colValue];
					colValue = Short.toUnsignedInt(lookup[colValue]);
					setColumnValue(col, colValue);
				}	break;
			case UP:
				for (int col = 0; col < SIZE; col++) {
					int colValue = getColumnValue(col);
					score += score_lookup[colValue];
					colValue = Short.toUnsignedInt(lookup[colValue]);
					setColumnValue(col, reverseBitGroups(colValue));
				}	break;
			default: throw new RuntimeException("Invalid direction: " + dir);
		}
	}
	
	/**
	 * @return true if game over.
	 */
	public boolean isGameOver() {
		return gameOver;
	}
	
	/**
	 * Returns the board data.
	 * @return 
	 */
	public long getBoardData() {
		return boardData;
	}
	
	/**
	 * Sets the board data. Should only be used during debugging.
	 * @param boardData 
	 */
	public void setBoardData(long boardData) {
		this.boardData = boardData;
	}
	
	
	/**
	 * Updates the game over state for this board.
	 */
	public void checkGameOver() {
		for (int dir = LEFT; dir <= DOWN; dir++) {
			Board b = new Board(this);
			b.move(dir);
			if (b.getFreeSquares() > 0) return;
		}
		
		gameOver = true;
	}
	
	/**
	 * Returns the value of a given row index.
	 * @param row
	 * @return 
	 */
	private int getRowValue(int row) {
		return (int)(boardData >>> row * SIZE * SIZE & 0xFFFF);
	}
	
	/**
	 * Sets the row value at the given index.
	 * @param row
	 * @param value 
	 */
	private void setRowValue(int row, int value) {
		// clear existing row
		boardData &= ~(0xFFFFL << row * SIZE * SIZE);
		
		// set new row
		boardData |= (long)value << row * SIZE * SIZE;
	}
	
	/**
	 * Returns the value of a given column
	 * @param column
	 * @return 
	 */
	private int getColumnValue(int column) {
		int val = 0;
		
		for (int y = 0; y < SIZE; y++)
			val |= getValue(column, y) << y * SIZE;
		
		return val;
	}
	
	/**
	 * Sets the column value at the given index.
	 * @param column
	 * @param value 
	 */
	private void setColumnValue(int column, int value) {
		for (int y = 0; y < SIZE; y++)
			setValue(column, y, value >>> y * SIZE);
	}
	
	/**
	 * Returns the value in a given board cell.
	 * @param x
	 * @param y
	 * @return 
	 */
	public int getValue(int x, int y) {
		//return (int)(boardData >>> shift(x, y) & 0xF);
		return getValue(x + y * SIZE);
	}
	
	/**
	 * Returns the value in a given board cell index.
	 * @param i
	 * @return 
	 */
	public int getValue(int i) {
		return (int)((boardData >>> i * SIZE) & 0xFL);
	}
	
	/**
	 * Sets the value in a given cell. Only the 4 lowest significant bits will be considered.
	 * @param x
	 * @param y
	 * @param value 
	 */
	public void setValue(int x, int y, int value) {
		//if (value > 15) throw new IllegalArgumentException("Value > 15!");
		value &= 0xF;
		
		int shift = shift(x, y);
		
		// clear existing bits
		boardData &= ~(0xFL << shift);
		
		// set new bits
		boardData |= (long)value << shift;
	}
	
	/**
	 * Set the value in a given cell index.
	 * @param cell_i
	 * @param value 
	 */
	public void setValue(int cell_i, int value) {
		value &= 0xF;
		int shift = cell_i * SIZE;
		boardData &= ~(0xFL << shift);
		boardData |= (long)value << shift;
	}
	
	/**
	 * Spawns a new tile at a random, free square.
	 */
	public void spawnTileRnd() {
		if (gameOver)
			return;
		
		ArrayList<Point> freeSquares = new ArrayList<>();
		
		for (int x = 0; x < SIZE; x++) {
			for (int y = 0; y < SIZE; y++) {
				if (getValue(x, y) == 0)
					freeSquares.add(new Point(x, y));
			}
		}
		
		if (freeSquares.isEmpty()) {
			return;
		}
		
		Point tile = freeSquares.get(rng.nextInt(freeSquares.size()));
		Frame.spawnPoint = tile;
		
		setValue(tile.x, tile.y, rng.nextInt(10) == 0 ? 2 : 1); // 10% chance to spawn 2^2=4, 90% chance top spawn 2^1=2
		
		checkGameOver();
	}
	
	/**
	 * Calculates the number of free squares on the board.
	 * @return 
	 */
	public int getFreeSquares() {
		int n = 0;
		
		n += (boardData & 0xFL) == 0L ? 1 : 0;
		n += (boardData & 0xFL << 4) == 0L ? 1 : 0;
		n += (boardData & 0xFL << 8) == 0L ? 1 : 0;
		n += (boardData & 0xFL << 12) == 0L ? 1 : 0;
		n += (boardData & 0xFL << 16) == 0L ? 1 : 0;
		n += (boardData & 0xFL << 20) == 0L ? 1 : 0;
		n += (boardData & 0xFL << 24) == 0L ? 1 : 0;
		n += (boardData & 0xFL << 28) == 0L ? 1 : 0;
		n += (boardData & 0xFL << 32) == 0L ? 1 : 0;
		n += (boardData & 0xFL << 36) == 0L ? 1 : 0;
		n += (boardData & 0xFL << 40) == 0L ? 1 : 0;
		n += (boardData & 0xFL << 44) == 0L ? 1 : 0;
		n += (boardData & 0xFL << 48) == 0L ? 1 : 0;
		n += (boardData & 0xFL << 52) == 0L ? 1 : 0;
		n += (boardData & 0xFL << 56) == 0L ? 1 : 0;
		n += (boardData & 0xFL << 60) == 0L ? 1 : 0;
		
		return n;
	}
	
	/**
	 * Returns the highest value on the board.
	 * @return 
	 */
	public int getHighestValue() {
		int best = 0;
		for (int i = 0; i < 16; i++) {
			int v = getValue(i);
			if (v > best) best = v;
		}
		return best;
	}
	
	/**
	 * Returns the current score.
	 * @return 
	 */
	public int getScore() {
		return score;
	}
	
	
	public double heuristic() {
		if (gameOver) return 0.0f;
		return heuristic.heuristic(this);
	}

	
	@Override
	public String toString() {
		String ret = "";
		
		for (int i = SIZE - 1; i >= 0; i--) {
			ret += "|\n";
			for (int j = 0; j < SIZE; j++) {
				ret += getValue(j, i) + " ";
			}
		}
		
		return ret.substring(1);
	}

	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj instanceof Board) {
			Board o = (Board)obj;
			return boardData == o.boardData;
		}
		return false;
	}
	
	
	

	/* --------------- STATIC ------------------------------------------------------------------- */
	
	
	private static int shift(int x, int y) {
		return (x + y * SIZE) * SIZE;
	}
	
	
	private static int reverseBitGroups(int a) {
		a = (a & 0xF0F) << 4 | (a & 0xF0F0) >>> 4;
		a = a << 8 | a >>> 8;
		return a & 0xFFFF;
	}
	
	
	public static void generateLookup() {
		int[] vals = new int[5];
		
		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				for (int k = 0; k < 16; k++) {
					for (int l = 0; l < 16; l++) {		
						vals[0] = i;
						vals[1] = j;
						vals[2] = k;
						vals[3] = l;
						vals[4] = 0;
						
						perform(vals);
						
						int a = i << 12 | j << 8 | k << 4 | l;
						int b = vals[3] << 12 | vals[2] << 8 | vals[1] << 4 | vals[0];
						
						lookup[a] = (short)b;
						score_lookup[a] = (short)vals[4];
					}
				}
			}
		}
	}
	
	
	private static void perform(int[] vals) {
		// merge
		for (int i = 0; i < 3; i++) {
			if (vals[i] == 0)
				continue;
			
			for (int j = i + 1; j < 4; j++) {
				if (vals[i] == vals[j]) {
					vals[4] += 2 << vals[i]; // increment score
					vals[i]++; // merge and increment
					vals[j] = 0; // clear the other tile
					break;
				} else if (vals[j] != 0)
					break;
			}
		}
		
		// slide
		for (int i = 0; i < 3; i++) {
			if (vals[i] == 0) { // empty space
				
				for (int j = i + 1; j < 4; j++) { // move all tiles from i and up
					if (vals[j] != 0) {
						vals[i] = vals[j]; // move value
						vals[j] = 0; // clear old value
						break;
					}
				}
				
			}
		}
	}
	
	
	private static Heuristic createHeuristic(Class<? extends Heuristic> clazz) {
		try {
			return clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException ex) {
			System.err.println(ex);
			return null;
		}
	}
	
}
