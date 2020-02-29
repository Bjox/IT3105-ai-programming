package astar.openlists;

import astar.Node;
import java.util.List;

/**
 *
 * @author Bjox
 */
public abstract class OpenList {
	
	public abstract void add(Node n);
	
	public abstract Node get();
	
	public abstract int size();
	
	public abstract boolean contains(Node n);
	
	protected <T extends Comparable<? super T>> void sort(List<T> list) {
		java.util.Collections.sort(list);
	}
	
	protected abstract List getList();
	
	public void sort() {
		sort(getList());
	}
	
}
