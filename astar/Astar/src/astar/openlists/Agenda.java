package astar.openlists;

import astar.Node;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Bjox
 */
public class Agenda extends OpenList {

	private final ArrayList<Node> list;

	public Agenda() {
		this.list = new ArrayList<>();
	}
	
	@Override
	public void add(Node n) {
		list.add(n);
		sort(list);
	}

	@Override
	public Node get() {
		return list.remove(0);
	}

	@Override
	public int size() {
		return list.size();
	}

	@Override
	public boolean contains(Node n) {
		return list.contains(n);
	}

	@Override
	protected List getList() {
		return list;
	}
	
}
