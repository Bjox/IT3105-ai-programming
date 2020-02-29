package module1;

import astar.AstarState;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Objects;

/**
 *
 * @author Bjox
 */
public class MazeState extends AstarState {

	public Point position;
	public static Board board;

	
	/**
	 * Copy
	 * @param original 
	 */
	public MazeState(MazeState original) {
		position = new Point(original.position.x, original.position.y);
	}

	
	public MazeState() {
	}
	
	
	/**
	 * The heuristic function.
	 * Estimates distance to goal by calculating the
	 * Manhattan distance from current position to the goal position.
	 * @return 
	 */
	@Override
	public float heuristic() {
		return manhattanDist(position, board.goal);
	}

	
	@Override
	public boolean isSolution() {
		return position.equals(board.goal);
	}

	
	@Override
	public AstarState[] generateChildren() {
		ArrayList<MazeState> ch = new ArrayList<>();
		
		// right
		MazeState newState = new MazeState(this);
		newState.position.x++;
		if (isValidPos(newState.position)) ch.add(newState);
		
		// left
		newState = new MazeState(this);
		newState.position.x--;
		if (isValidPos(newState.position)) ch.add(newState);
		
		// up
		newState = new MazeState(this);
		newState.position.y++;
		if (isValidPos(newState.position)) ch.add(newState);
		
		// down
		newState = new MazeState(this);
		newState.position.y--;
		if (isValidPos(newState.position)) ch.add(newState);
		
		MazeState[] arr = new MazeState[ch.size()];
		for (int i = 0; i < ch.size(); i++) {
			arr[i] = ch.get(i);
		}
		return arr;
	}

	
	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
		if (o instanceof MazeState) {
			return id() == ((MazeState)o).id();
		}
		return false;
	}

	
	@Override
	public int hashCode() {
		int hash = 5;
		hash = 29 * hash + Objects.hashCode(this.position);
		return hash;
	}

	
	@Override
	public int id() {
		return position.y * board.X + position.x;
	}
	
	
	@Override
	public float arc_cost(AstarState other) {
		return 1.0f;
	}

	@Override
	public String toString() {
		return position.toString();
	}
	
	
	private boolean isValidPos(Point p) {
		if (p.x >= board.X || p.x < 0 || p.y >= board.Y || p.y < 0)
			return false;
		return board.isFree(p.x, p.y);
	}
	
	
	private int manhattanDist(Point p1, Point p2) {
		return Math.abs(p1.x - p2.x) + Math.abs(p1.y - p2.y);
	}
	
	private float euclideanDist(Point p1, Point p2) {
		return (float)Math.sqrt((p1.x - p2.x)*(p1.x - p2.x) + (p1.y - p2.y)*(p1.y - p2.y));
	}
	
}
