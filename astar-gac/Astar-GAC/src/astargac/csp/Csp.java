package astargac.csp;

import astargac.astar.ConstraintInstance;
import astargac.astar.VariableInstance;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Class describing a Constraint Satisfaction Problem, including a set of constraints and 
 * variables with a definite domain. A CSP can be solved using GAC.<br><br>
 * <i>Deprecated. Replaced by <code>CspState</code>.</i>
 * @author Bjox
 */
@Deprecated
public class Csp {
	
	private final HashMap<String, VariableInstance> variables; // Key = variable name
	private final HashMap<String, ConstraintInstance> constraints;

	
	public Csp(HashMap<String, VariableInstance> variables, HashMap<String, ConstraintInstance> constraints) {
		this.variables = variables;
		this.constraints = constraints;
	}
	
//	public Csp() {
//		this.variables = new HashMap<>();
//		this.constraints = new HashMap<>();
//	}
	
	
//	public void addConstraint(String constraint) throws Exception {
//		if (constraint == null || constraint.equals(""))
//			return;
//		
//		Constraint c = new Constraint(constraint);
//		constraints.add(c);
//		
//		// Add contraint-variable dependencies
//		for (String arg : c.codeChunk.getArgNames()) {
//			Variable v = variables.get(arg);
//			
//			if (v == null) {
//				v = new Variable(arg);
//				variables.put(arg, v);
//			}
//			
//			c.addDependentVar(v);
//		}
//	}
	
	
	public VariableInstance getVariable(String name) {
		return variables.get(name);
	}
	
	
	public ConstraintInstance getConstraint(String expr) {
		return constraints.get(expr);
	}
	
	
//	public void addToVariableDomain(String varName, int... args) {
//		if (variables.containsKey(varName)) {
//			variables.get(varName).addToDomain(args);
//		} else {
//			throw new RuntimeException("Variable \"" + varName + "\" not found.");
//		}
//	}
	
	
//	public void addToAllVariableDomains(int... args) {
//		variables.forEach((String K, Variable V) -> V.addToDomain(args));
//	}
	
	
	public ConstraintInstance[] getConstraints() {
		return constraints.values().toArray(new ConstraintInstance[0]);
	}
	
	/**
	 * Performs the given action for every ConstraintInstance in this CSP.
	 * @param action 
	 */
	public void forEachConstraint(Consumer<? super ConstraintInstance> action) {
		constraints.forEach((K, V) -> action.accept(V));
	}
	
	/**
	 * Performs the given action for every element in the cross-product of constraints and their dependent variables.
	 * @param action 
	 */
	public void forEachConstraintVariableComb(BiConsumer<? super ConstraintInstance, ? super VariableInstance> action) {
		constraints.forEach((cK, c) -> c.forEachDependentVar(v -> action.accept(c, v)));
	}
	
	/**
	 * Performs the given action for every variable in this CSP.
	 * @param action 
	 */
	public void forEachVariable(Consumer<? super VariableInstance> action) {
		variables.forEach((K, V) -> action.accept(V));
	}
	
	
	public Collection<VariableInstance> getVariableCollection() {
		return variables.values();
	}
	
	
	public boolean isSolved() {
		return variables.values().stream().allMatch(v -> v.isSingleton());
	}
	

	@Override
	public String toString() {
		final StringBuilder str = new StringBuilder();
		
		constraints.forEach((cK, c) -> str.append("Constraint instance: ").append(c.toString()).append("\n"));
		
		variables.forEach((K, V) -> str.append(V.toString()).append("\n"));
		
		return str.toString().substring(0, str.length() - 1);
	}

	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj instanceof Csp) {
			Csp other = (Csp)obj;
			return constraints.equals(other.constraints) && variables.equals(other.variables);
		}
		return false;
	}

	
	@Override
	public int hashCode() {
		int hash = 3;
		hash = 71 * hash + Objects.hashCode(this.variables);
		hash = 71 * hash + Objects.hashCode(this.constraints);
		return hash;
	}
	
	
	
}
