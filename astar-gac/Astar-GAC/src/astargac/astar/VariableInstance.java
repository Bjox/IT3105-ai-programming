package astargac.astar;

import astargac.csp.Variable;


/**
 *
 * @author Bjox
 */
public class VariableInstance extends Variable {
	
	private final Variable cnetVar;

	/**
	 * Creates a new VariableInstance by copying data from an existing Variable.
	 * @param original 
	 */
	public VariableInstance(Variable original) {
		super(original);
		cnetVar = original;
	}
	
	/**
	 * Copy constructor.
	 * @param original 
	 */
	public VariableInstance(VariableInstance original) {
		super(original);
		cnetVar = original.cnetVar;
	}
	
	
	public Variable getCnetVar() {
		return cnetVar;
	}
	
}
