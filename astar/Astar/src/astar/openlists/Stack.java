package astar.openlists;

import astar.Node;
import java.util.List;

/**
 *
 * @author Bjox
 */
public class Stack extends OpenList {
	
	private final java.util.Stack<Node> stack;

	public Stack() {
		stack = new java.util.Stack<>();
	}
	
	@Override
	public void add(Node n) {
		stack.push(n);
	}

	@Override
	public Node get() {
		return stack.pop();
	}

	@Override
	public int size() {
		return stack.size();
	}

	@Override
	public boolean contains(Node n) {
		return stack.contains(n);
	}

	@Override
	protected List getList() {
		return stack;
	}
	
}
