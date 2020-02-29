package astar;

import java.util.ArrayList;
import java.util.Objects;

/**
 *
 * @author Bjox
 * @param <T>
 */
public class Node<T extends AstarState> implements Comparable<Node> {

	public Node<T>            parent;
	public ArrayList<Node<T>> childs;
	public T			      state;
	public Status             status;

	public float g;	// distance from start
	public float h;	// estimated distance to goal (heuristic value)
	public float f;	// f = g + h
	
	public enum Status { OPEN, CLOSED; }

	
	public Node() {
		childs = new ArrayList<>();
		status = Status.OPEN;
	}
	
	/**
	 * Computes and sets the heuristic value for this node.
	 */
	public void compute_h() {
		h = state.heuristic();
	}

	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj instanceof Node) return state.id() == ((Node)obj).state.id();
		return false;
	}

	
	@Override
	public int hashCode() {
		int hash = 5;
		hash = 67 * hash + Objects.hashCode(state.id());
		return hash;
	}

	
	@Override
	public int compareTo(Node o) {
		if (f < o.f) return -1;
		if (f > o.f) return 1;
		return 0;
	}
	
	/**
	 * Calculates the depth of this node.
	 * @return the node depth within the tree.
	 */
	public int depth() {
		Node node = this;
		int n = 0;
		while (node != null) {
			n++;
			node = node.parent;
		}
		return n;
	}

	
	@Override
	public String toString() {
		return "f = " + f + ", " + state.toString();
	}
	
	
}
