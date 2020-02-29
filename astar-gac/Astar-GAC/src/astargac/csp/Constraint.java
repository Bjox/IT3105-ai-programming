package astargac.csp;

import astargac.codechunk.CodeChunk;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Consumer;

/**
 *
 * @author Bjox
 */
public class Constraint {
	
	public final CodeChunk codeChunk;
	private final ArrayList<Variable> dependentVars;
	
	public Constraint(String expression) throws Exception {
		this.codeChunk = new CodeChunk(expression);
		this.dependentVars = new ArrayList<>();
	}
	
	
	public void addDependentVar(Variable var) {
		dependentVars.add(var);
		java.util.Collections.sort(dependentVars);
	}
	
	
	public boolean check(int... args) {
		return (args == null || args.length == 0) ? codeChunk.exec() : codeChunk.exec(args);
	}
	
	/**
	 * Returns an array containing all appearing variables.
	 * @return 
	 */
	public Variable[] getDependentVars() {
		return dependentVars.toArray(new Variable[0]);
	}
	
	
	public ArrayList<Variable> getDependentVarsArrayList() {
		return dependentVars;
	}
	
	/**
	 * Returns an array containing all appearing variables, excepts for <code>focalVar</code>.
	 * @param focalVar
	 * @return 
	 */
	public Variable[] getDependentVars(Variable focalVar) {
		ArrayList<Variable> vars = new ArrayList<>();
		
		dependentVars.stream().filter((var) -> (!var.equals(focalVar))).forEach((var) -> {
			vars.add(var);
		});
		
		return vars.toArray(new Variable[0]);
	}

	/**
	 * Performs the given action for every dependent variable for this constraint.
	 * @param action 
	 */
	public void forEachDependentVar(Consumer<? super Variable> action) {
		dependentVars.forEach(action);
	}
	
	
	public boolean containsDependentVar(Variable v) {
		//return depVarHashMap.containsKey(v.getName());
		return dependentVars.contains(v);
	}
	
	
	@Override
	public String toString() {
		return codeChunk.expression;
	}

	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj instanceof Constraint) {
			Constraint other = (Constraint)obj;
			return other.codeChunk.expression.equals(codeChunk.expression);
		}
		return false;
	}
	

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 71 * hash + Objects.hashCode(this.codeChunk);
		return hash;
	}

	
	
	
	
}
