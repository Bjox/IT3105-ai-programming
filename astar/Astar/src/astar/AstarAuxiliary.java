package astar;

/**
 *
 * @author Bjox
 * @param <T>
 */
public interface AstarAuxiliary<T extends AstarState> {
	
	/**
	 * This method is called immediately after a node is popped from the open list of the Astar algorithm.
	 * @param node the node popped from the open list.
	 */
	public void poppedNode(Node<T> node);
	
}
