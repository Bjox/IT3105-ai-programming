package astar;

/**
 *
 * @author Bjox
 */
public abstract class AstarState {
	
	/**
	 * Estimates the distance to goal by executng the heuristic function.
	 * @return 
	 */
	public abstract float heuristic();
	
	/**
	 * @return true if this state is a solution. false otherwise.
	 */
	public abstract boolean isSolution();
	
	/**
	 * Returns true if this state represents a dead end, and should not be expanded.
	 * @return 
	 */
	public boolean isContradictory() {
		return false;
	}
	
	/**
	 * Generates valid child states by applying operators to this state.
	 * @return 
	 */
	public abstract AstarState[] generateChildren();
	
	/**
	 * Returns a unique identifier for this state.
	 * @return 
	 */
	public abstract int id();
	
	/**
	 * Calculates the arc cost between this state and another.
	 * @param other the other state.
	 * @return 
	 */
	public abstract float arc_cost(AstarState other);
	
}
