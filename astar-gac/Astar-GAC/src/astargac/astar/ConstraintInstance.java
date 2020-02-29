package astargac.astar;

import astargac.csp.Constraint;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;
import java.util.function.Consumer;

/**
 *
 * @author Bjox
 */
public class ConstraintInstance {
	
	private final Constraint cnetConst;
	//private final ArrayList<String> dependentVisNames;
	private final HashMap<String, VariableInstance> dependentVis;
	
	/**
	 * Creates a new ConstraintInstance. Dependency vars are not copied.
	 * @param original 
	 */
	public ConstraintInstance(Constraint original) {
		this.cnetConst = original;
		//this.dependentVisNames = new ArrayList<>();
		this.dependentVis = new HashMap<>();
	}
	
	/**
	 * Copy constructor. Dependency vars are not copied.
	 * @param original 
	 */
	public ConstraintInstance(ConstraintInstance original) {
		this(original.cnetConst);
	}

	
	public void addDependentVi(VariableInstance vi) {
		//dependentVisNames.add(vi.getName());
		dependentVis.put(vi.getName(), vi);
	}
	
	
	public Constraint getCnetConst() {
		return cnetConst;
	}
	
	
	public void forEachDependentVar(Consumer<? super VariableInstance> action) {
		//dependentVis.forEach(action);
		dependentVis.forEach((K, V) -> action.accept(V));
	}
	
	
	public Collection<VariableInstance> getDependentVarsCollection() {
		return dependentVis.values();
	}
	
	
	public boolean containsDependentVar(VariableInstance var) {
		//return dependentVis.contains(var);
		return dependentVis.containsKey(var.getName());
	}
	
	
	public VariableInstance[] getDependentVars(VariableInstance focalVar) {
//		ArrayList<VariableInstance> vars = new ArrayList<>();
//		
//		dependentVis.stream().filter(var -> !var.equals(focalVar)).forEach(var -> {
//			vars.add(var);
//		});
//		
//		return vars.toArray(new VariableInstance[0]);
		
		ArrayList<VariableInstance> vars = new ArrayList<>();
		
		dependentVis.values().stream().filter(var -> !var.equals(focalVar)).forEach(var -> {
			vars.add(var);
		});
		
		return vars.toArray(new VariableInstance[0]);
	}

	
	@Override
	public String toString() {
		return cnetConst.toString();
	}

	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		
		if (obj instanceof ConstraintInstance) {
			ConstraintInstance other = (ConstraintInstance)obj;
			return cnetConst.equals(other.cnetConst);
		}
		
		return false;
	}
	

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 31 * hash + Objects.hashCode(this.cnetConst);
		return hash;
	}
	
	
	
}
