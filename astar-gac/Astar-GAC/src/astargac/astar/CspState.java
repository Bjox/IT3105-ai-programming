package astargac.astar;

import astar.AstarState;
import astargac.gac.Gac;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;


/**
 *
 * @author Bjox
 */
public class CspState extends AstarState {
	
	/** The maximum number of successor states generated at each node expansion. */
	public static int max_generated_childs = 10;
	
	protected final HashMap<String, VariableInstance> vis;
	protected final HashMap<String, ConstraintInstance> cis;
	
	protected int index = -1;
	
	public CspState() {
		vis = new HashMap<>();
		cis = new HashMap<>();
	}
	
	
	/**
	 * Copy constructor.
	 * @param original 
	 */
	public CspState(CspState original) {
		this();
		
		// Copy VariableInstances
		original.vis.forEach((K, vi) -> vis.put(K, new VariableInstance(vi)));
		
		// For each ConstraintInstance in original
		original.cis.forEach((K, ci) -> {
			// Add a copy of ci
			ConstraintInstance newci = new ConstraintInstance(ci);
			cis.put(K, newci);
			
			// For each dependent var in ci, add dependency entry for newci
			ci.getCnetConst().forEachDependentVar(v -> newci.addDependentVi(vis.get(v.getName())));
		});
		
		index = original.index;
	}
	
	
	public VariableInstance getVi(String name) {
		return vis.get(name);
	}
	
	
	public ConstraintInstance getCi(String expr) {
		return cis.get(expr);
	}
	
	
	public void addVariableInstance(VariableInstance vi) {
		vis.put(vi.getName(), vi);
	}
	
	
	public void addConstraintInstance(ConstraintInstance ci) {
		cis.put(ci.getCnetConst().codeChunk.expression, ci);
	}
	
	
	public void makeAssumption(String varName, int val) {
		VariableInstance vi = vis.get(varName);
		vi.clearDomain();
		vi.addToDomain(val);
	}
	
	
	/**
	 * Performs the given action for every element in the cross-product of constraints and their dependent variables.
	 * @param action 
	 */
	public void forEachConstraintVariableComb(BiConsumer<? super ConstraintInstance, ? super VariableInstance> action) {
		cis.forEach((cK, c) -> c.forEachDependentVar(v -> action.accept(c, v)));
	}
	
	
	public VariableInstance getVariable(String name) {
		return vis.get(name);
	}
	
	
	public void forEachVariable(Consumer<? super VariableInstance> action) {
		vis.forEach((K, V) -> action.accept(V));
	}
	
	
	public void forEachConstraint(Consumer<? super ConstraintInstance> action) {
		cis.forEach((K, V) -> action.accept(V));
	}
	
	
	public Collection<ConstraintInstance> getConstraintInstances() {
		return cis.values();
	}
	
	
	public Collection<VariableInstance> getVariableInstances() {
		return vis.values();
	}
	
	
	public String variableString() {
		StringBuilder str = new StringBuilder();
		
		vis.forEach((K, V) -> str.append(V.toString()).append("\n"));
		
		return str.toString().substring(0, str.length() - 1);
	}
	
	
	public int getNumberOfUnsatisfiedConstraints() {
		Gac gac = new Gac(this);
		return gac.getUnsatisfiedConstraints();
	}
	
	
	public int getNumberOfVariables() {
		return vis.size();
	}
	
	
	public int getNumberOfSingletons() {
		int n = 0;
		
		for (VariableInstance v : vis.values()) {
			if (v.isSingleton()) n++;
		}
		
		return n;
	}
	
	
	@Override
	public float heuristic() {
		if (isContradictory()) return Float.MAX_VALUE;
		
		float h = 0.0f;
		
		for (VariableInstance vi : vis.values()) {
			h += vi.domainSize() - 1;
		}
		
		return h;
	}
	
	
	@Override
	public boolean isSolution() {
		if (vis.values().stream().allMatch(vi -> vi.isSingleton())) {
			return getNumberOfUnsatisfiedConstraints() == 0;
		}
		return false;
	}
	
	
	@Override
	public boolean isContradictory() {
		return vis.values().stream().anyMatch(v -> v.domainSize() == 0);
	}

	
	@Override
	public AstarState[] generateChildren() {	
		ArrayList<CspState> children = new ArrayList<>();
		
		// For each VariableInstance in this state
		for (VariableInstance v : vis.values()) {
			if (v.domainSize() > 1) {
				for (int dv : v.getDomain()) {
					CspState newState = new CspState(this);
					newState.index = index + 1;
					
					Gac gac = new Gac(newState);
					gac.rerun(v.getName(), dv);

					if (!newState.isContradictory()) {
						children.add(newState);
						if (children.size() == max_generated_childs) return children.toArray(new CspState[0]);
					}
				}
			}
		}
		
		
		
		return children.toArray(new CspState[0]);
	}

	
	@Override
	public int id() {
		return hashCode();
	}

	
	@Override
	public float arc_cost(AstarState other) {
		return 1.0f;
	}

	
	@Override
	public int hashCode() {
		int hash = 5;
		hash = 17 * hash + Objects.hashCode(this.vis);
		hash = 17 * hash + Objects.hashCode(this.cis);
		return hash;
	}

	
	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		
		if (obj == this)
			return true;
		
		if (obj instanceof CspState) {
			CspState other = (CspState)obj;
			return vis.equals(other.vis) && cis.equals(other.cis);
		}
		
		return false;
	}

	
	@Override
	public String toString() {
		final StringBuilder str = new StringBuilder();
		
		cis.forEach((cK, c) -> str.append("Constraint instance: ").append(c.toString()).append("\n"));
		
		vis.forEach((K, V) -> str.append(V.toString()).append("\n"));
		
		return str.toString().substring(0, str.length() - 1);
	}


	
}
