package astargac.csp;

import java.util.HashSet;
import java.util.Objects;


/**
 *
 * @author Bjox
 */
public class Variable implements Comparable<Variable> {
	
	private final String name;
	private final Domain<Integer> domain;

	/**
	 * Constructor.
	 * @param name 
	 */
	public Variable(String name) {
		this.name = name;
		domain = new Domain();
	}
	
	/**
	 * Copy constructor.
	 * @param original 
	 */
	public Variable(Variable original) {
		this(original.name);
		addToDomain(original.getDomain());
	}
	
	/**
	 * Returns the name of this variable.
	 * @return 
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns true if the size of the domain for this variable is equal to 1, false otherwise.
	 * @return 
	 */
	public boolean isSingleton() {
		return domainSize() == 1;
	}
	
	/**
	 * Adds the specified integers to the domain of this variable.
	 * @param vals 
	 */
	public final void addToDomain(int... vals) {
		for (int v : vals) domain.add(v);
	}
	
	/**
	 * Removes the specified integers from the domain of this variable.
	 * @param vals
	 * @return the number of integers removed.
	 */
	public int removeFromDomain(int... vals) {
		int c = 0;
		for (int v : vals) c += domain.remove(v) ? 1 : 0;
		return c;
	}
	
	/**
	 * @return the number of elements in the domain of this variable.
	 */
	public int domainSize() {
		return domain.size();
	}
	
	/**
	 * Remove all elements from the domain of this variable.
	 */
	public void clearDomain() {
		domain.clear();
	}
	
	
	/**
	 * @return an array containing the elements of the domain for this variable.
	 */
	public int[] getDomain() {
		int[] elements = new int[domainSize()];
		HashSet<Integer> set = domain.getSet();
		
		int i = 0;
		for (Integer e : set) {
			elements[i++] = e;
		}
		
		return elements;
	}
	
	
	public Domain<Integer> getDomainObject() {
		return domain;
	}
	

	@Override
	public String toString() {
		return name + ": " + domain.toString() + "   (" + domainSize() + (domainSize() == 1 ? " element)" : " elements)");
	}

	/**
	 * Comparing by alphabetic order of variable name.
	 * @param o
	 * @return 
	 */
	@Override
	public int compareTo(Variable o) {
		return name.compareTo(o.name);
	}
	
	/**
	 * Two variables are considered equal if their names are equal,
	 * and their domains contain the same elements.
	 * @param obj
	 * @return 
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		
		if (obj == this)
			return true;
		
		if (obj instanceof Variable) {
			Variable o = (Variable)obj;
			return name.equals(o.name) && domain.equals(o.domain);
		}
		return false;
	}

	
	@Override
	public int hashCode() {
		String s = name;
		HashSet<Integer> set = domain.getSet();
		for (Integer i : set) s += i.toString();
		
		int hash = 3;
		hash = 71 * hash + Objects.hashCode(this.name);
		hash = 71 * hash + Objects.hashCode(this.domain);
		hash = 71 * hash + Objects.hashCode(s);
		return hash;
	}

}
