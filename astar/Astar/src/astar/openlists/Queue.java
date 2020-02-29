package astar.openlists;

import astar.Node;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Bjox
 */
public class Queue extends OpenList {

	private final LinkedList<Node> linkedList;

	public Queue() {
		this.linkedList = new LinkedList<>();
	}
	
	@Override
	public void add(Node n) {
		linkedList.addLast(n);
	}

	@Override
	public Node get() {
		return linkedList.pollFirst();
	}

	@Override
	public int size() {
		return linkedList.size();
	}

	@Override
	public boolean contains(Node n) {
		return linkedList.contains(n);
	}

	@Override
	protected List getList() {
		return linkedList;
	}
	
	
}
