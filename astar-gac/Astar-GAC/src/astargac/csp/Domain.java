package astargac.csp;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Consumer;

/**
 *
 * @author Bjox
 */
public class Domain<T> {
	
	private final HashSet<T> set;
	
	
	public Domain() {
		this.set = new HashSet<>();
	}

	
	public void add(T element) {
		set.add(element);
	}
	
	
	public boolean remove(T element) {
		return set.remove(element);
	}
	
	
	public int size() {
		return set.size();
	}
	
	
	public void clear() {
		set.clear();
	}
	
	
	public Object[] elements() {
		return set.toArray();
	}
	
	
	public void forEach(Consumer<? super T> action) {
		set.forEach(action);
	}
	
	
	public HashSet<T> getSet() {
		return set;
	}
	
	
	public Iterator<T> getIterator() {
		return set.iterator();
	}
	
	
	@Override
	public String toString() {
		return set.toString();
	}

	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj instanceof Domain) {
			Domain other = (Domain)obj;
			return other.set.equals(set);
		}
		return false;
	}
	

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 67 * hash + Objects.hashCode(this.set);
		return hash;
	}
	
	
	
}
