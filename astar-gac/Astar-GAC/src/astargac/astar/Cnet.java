package astargac.astar;

import astargac.csp.Constraint;
import astargac.csp.Variable;
import java.util.HashMap;

/**
 *
 * @author Bjox
 */
public class Cnet {
	
	private final HashMap<String, Constraint> constraints;
	private final HashMap<String, Variable> variables;

	
	public Cnet() {
		this.constraints = new HashMap<>();
		this.variables = new HashMap<>();
	}
	
	
	public void addConstraint(String expression) throws Exception {
		if (expression == null || expression.equals(""))
			return;
		
		Constraint c = new Constraint(expression);
		constraints.put(expression, c);
		
		// Add contraint-variable dependencies
		for (String arg : c.codeChunk.getArgNames()) {
			Variable v = variables.get(arg);
			
			if (v == null) {
				v = new Variable(arg);
				variables.put(arg, v);
			}
			
			c.addDependentVar(v);
		}
	}
	
	
	public void addToVariableDomain(String varName, int... args) {
		if (variables.containsKey(varName)) {
			variables.get(varName).addToDomain(args);
		} else {
			throw new RuntimeException("Variable \"" + varName + "\" not found.");
		}
	}
	
	
	public void addToAllVariableDomains(int... args) {
		variables.forEach((K, V) -> V.addToDomain(args));
	}
	
	/**
	 * Used to generate init state.
	 * @return 
	 */
	public CspState generateCspState() {
		CspState state = new CspState();
		
		constraints.values().stream().forEach(c -> {
			ConstraintInstance ci = new ConstraintInstance(c);
			state.addConstraintInstance(ci);
			
			c.forEachDependentVar(v -> {
				VariableInstance vi = state.getVi(v.getName());
				
				if (vi == null) {
					vi = new VariableInstance(v);
					state.addVariableInstance(vi);
				}
				
				ci.addDependentVi(vi);
			});
		});
		
		return state;
	}
	
	
	@Override
	public String toString() {
		final StringBuilder str = new StringBuilder();
		
		constraints.forEach((K, V) -> str.append("Constraint: ").append(V.toString()).append("\n"));
		
		variables.forEach((K, V) -> str.append(V.toString()).append("\n"));
		
		return str.toString().substring(0, str.length() - 1);
	}
	
}
