package astar;

import astar.openlists.OpenList;
import astar.openlists.Agenda;
import astar.openlists.Queue;
import astar.openlists.Stack;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Bjox
 * @param <T>
 */
public class Astar<T extends AstarState> {
	
	public static final int BEST_FIRST    = 0;
	public static final int BREADTH_FIRST = 1;
	public static final int DEPTH_FIST    = 2;
	
	public final int mode;
	
	public final AstarAuxiliary aux;

	private HashMap<Integer, Node> nodes;	   // Hashmap containing all generated nodes.
	private ArrayList<Node>        closedList; // List of closed nodes.
	private OpenList               openList;   // The container type of the open list is determined by the search mode.

	private boolean ready = true;
	private int numPopped = 0;
	private Node<T> solutionNode;
	
	/**
	 * Initializes a new search environment with empty data containers.
	 * @param mode The search mode; BEST_FIRST, BREADTH_FIRST or DEPTH_FIRST
	 * @param aux An optional Astart auxiliary object
	 */
	public Astar(int mode, AstarAuxiliary aux) {
		this.mode = mode;
		this.aux = aux;
		nodes = new HashMap<>();
		closedList = new ArrayList<>();
		
		switch (mode) {
			case BEST_FIRST:
				openList = new Agenda();
				break;
				
			case BREADTH_FIRST:
				openList = new Queue();
				break;
				
			case DEPTH_FIST:
				openList = new Stack();
				break;
				
			default: throw new RuntimeException("Invalid search mode (" + mode + ").");
		}
	}
	
	
	/**
	 * Initializes a new search environment with empty data containers.
	 * @param mode The search mode; BEST_FIRST, BREADTH_FIRST or DEPTH_FIRST
	 */
	public Astar(int mode) {
		this(mode, null);
	}
	
	/**
	 * Returns the total number of nodes generated in the run.
	 * @return 
	 */
	public int getNodesGenerated() {
		return nodes.size();
	}
	
	/**
	 * Returns the solution depth for the run.
	 * @return 
	 */
	public int getSolutionDepth() {
		return solutionNode == null ? -1 : solutionNode.depth();
	}
	
	
	public int getOpenListSize() {
		return openList.size();
	}
	
	
	public Node<T> getSolutionNode() {
		return solutionNode;
	}
	
	
	public int getPoppedNodes() {
		return numPopped;
	}
	

	/**
	 * Start the search.
	 * @param init_state
	 * @return 
	 */
	public T search(T init_state) {
		if (!ready) throw new RuntimeException("Create a new Astar instance.");
		
		ready = false;
		Node<T> root = new Node();
		
		root.state = init_state;
		root.g = 0;
		root.h = root.state.heuristic();
		root.f = root.g + root.h;
		
		nodes.put(root.state.id(), root);
		openList.add(root);
		
		return (T)agenda_loop();
	}
	
	
	private AstarState agenda_loop() {
		
		while (openList.size() > 0) {
			Node<T> parent = openList.get(); // pop node from open list
			closedList.add(parent);	// add the newly popped node to closed list
			parent.status = Node.Status.CLOSED;	// set status to closed
			
			numPopped++; // increment the num popped count
			
			if (aux != null) aux.poppedNode(parent);
			
			solutionNode = parent; // set the partial solution
			
			if (parent.state.isSolution()) { // success
				return parent.state; // return solution
			}
			
			T[] children = (T[])parent.state.generateChildren();	// expand parent
			
			for (T state : children) { // for each successor state
				Node<T> child = new Node(); // create node for state
				child.state = state; // set the state
				
				boolean isUnique = true; // unique flag
				
				if (nodes.containsKey(child.state.id()))  // if the node has already been created
				{
					child = nodes.get(child.state.id()); // fetch the corresponding node
					isUnique = false;
				}
				
				parent.childs.add(child);	// add child to parent
				
				if (isUnique) {	// if child is not present in the open nor the closed list (has not been generated before)
					attach_and_eval(child, parent);
					if (!state.isContradictory()) openList.add(child); // add the node to open list if it is valid
					nodes.put(child.state.id(), child);
				}
				else if (parent.g + arc_cost(parent, child) < child.g) { // better parent
					attach_and_eval(child, parent);
					if (child.status == Node.Status.CLOSED) propagate_path_improvements(child);
				}
				
			}
		}
		
		return null; // no solution bro
	}

	
	private void attach_and_eval(Node<T> child, Node<T> parent) {
		child.parent = parent;
		child.g = parent.g + arc_cost(parent, child);
		child.compute_h();
		child.f = child.g + child.h;
	}
	
	
	private void propagate_path_improvements(Node<T> parent) {
		for (Node<T> child : parent.childs) {
			if (parent.g + arc_cost(parent, child) < child.g) {
				child.parent = parent;
				child.g = parent.g + arc_cost(parent, child);
				child.f = child.g + child.h;
				propagate_path_improvements(child);
			}
		}
	}
	
	
	private float arc_cost(Node A, Node B) {
		return A.state.arc_cost(B.state);
	}
}
