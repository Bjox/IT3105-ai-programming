package astargac.gac;

import astargac.astar.ConstraintInstance;
import astargac.astar.CspState;
import astargac.astar.VariableInstance;
import java.util.ArrayDeque;
import java.util.function.BooleanSupplier;

/**
 * Class that implements the General Arc Consistency algorithm for solving CSPs.
 * @author Bjox
 */
public class Gac {
	
	private final ArrayDeque<TodoRevisePair> todoReviseQueue;
	private final CspState csp;
	
	/**
	 * Creates a new GAC instance for solving the supplied CSP.
	 * @param csp 
	 */
	public Gac(CspState csp) {
		this.csp = csp;
		todoReviseQueue = new ArrayDeque<>();
	}
	
	/**
	 * Solves the CSP.
	 */
	public void solve() {
		initialization();
		domain_filtering_loop();
	}
	
	
	/**
	 * All domains must be singleston!
	 * @return 
	 */
	public int getUnsatisfiedConstraints() {
		int n = 0;
		
		ccheck: for (ConstraintInstance ci : csp.getConstraintInstances()) {
			for (VariableInstance vi : ci.getDependentVarsCollection()) {
				if (vi.domainSize() > 0) {
					ci.getCnetConst().codeChunk.set(vi.getName(), vi.getDomain()[0]);
				} else {
					n++;
					continue ccheck;
				}
			}
			
			if (!ci.getCnetConst().check()) n++;
		}
		
		return n;
	}
	
	
	private void addTodoRevisePair(VariableInstance focalVar, ConstraintInstance constraint) {
		todoReviseQueue.add(new TodoRevisePair(focalVar, constraint));
	}
	
	/**
	 * Fill todoReviseQueue with TODO-REVISE requests.
	 */
	private void initialization() {
		// Adds every constraint c and variable v in c pair to todoReviseQueue
		csp.forEachConstraintVariableComb((c, v) -> addTodoRevisePair(v, c));
	}
	
	
	private void domain_filtering_loop() {
		while (!todoReviseQueue.isEmpty()) {
			TodoRevisePair todoRevise = todoReviseQueue.poll();
			
			// Domain size of focal var before filtering
			int focalVarDomainSize = todoRevise.focalVar.domainSize();
			
			// Filtering of focal var with respect to the respective constraint
			REVISEstar(todoRevise.focalVar, todoRevise.constraint);
			
			// If the domain of focalVar got reduced
			if (todoRevise.focalVar.domainSize() < focalVarDomainSize) {
				csp.forEachConstraintVariableComb((c, v) -> {
					// If the constraint-variable pair is not equal to the TodoRevisePair polled from the queue
					if (c.containsDependentVar(todoRevise.focalVar) && c != todoRevise.constraint && v != todoRevise.focalVar) {
						addTodoRevisePair(v, c);
					}
				});
			}
		}
	}
	
	
	public void rerun(String varName, int assumptionVal) {
		VariableInstance assumptionVar = csp.getVariable(varName);
		if (csp.getVariable(assumptionVar.getName()) != assumptionVar)
			throw new RuntimeException("Variable \"" + assumptionVar.getName() + "\" does not show up in the current CSP.");
		
		assumptionVar.clearDomain();
		assumptionVar.addToDomain(assumptionVal);
		
		csp.forEachConstraint(c -> {
			if (c.containsDependentVar(assumptionVar)) {
				c.forEachDependentVar(v -> {
					if (!v.equals(assumptionVar)) addTodoRevisePair(v, c);
				});
			}
		});
		
		domain_filtering_loop();
	}
	
	
	private int[][] domains;
	private ConstraintInstance testConst;
	private VariableInstance filteredVar;
	private VariableInstance[] otherVars;
	
	private void REVISEstar(VariableInstance focalVar, ConstraintInstance constraint) {
		/* Init */
		VariableInstance[] vars = constraint.getDependentVars(focalVar);
		int[] focalVals = focalVar.getDomain();
		int nVars = vars.length;
		
		domains = new int[nVars][];
		testConst = constraint;
		filteredVar = focalVar;
		otherVars = vars;
		/* End init */
		
		// Clears the domain of focal var. Only valid elements will be added
		filteredVar.clearDomain();
		
		// Fills the domain data from all other variables
		for (int i = 0; i < nVars; i++) {
			domains[i] = vars[i].getDomain();
		}
		
		// Filter each element in focal var domain
		for (int i = 0; i < focalVals.length; i++) {
			// The focal val being tested
			int focalVal = focalVals[i];
			
			// Sets the current focal var value in the arg list of the constraint being tested
			testConst.getCnetConst().codeChunk.set(focalVar.getName(), focalVal);
			
			if (nVars > 0) {
				a(nVars, 0, focalVal, (v) -> { return testAndHandle(v); } );
			}
			else
				testAndHandle(focalVal);
		}
	}
	
	/**
	 * Recursive
	 */
	private boolean a(int maxDepth, int curDepth, int focalVal, BooleanFunc func) {
		// For each element in the current depth domain (i'th other variable)
		for (int i = 0; i < domains[curDepth].length; i++) {
			// Sets the value in constraint arg list
			testConst.getCnetConst().codeChunk.set(otherVars[curDepth].getName(), domains[curDepth][i]);
			
			// If there are more variables
			if (curDepth + 1 < maxDepth) {
				// Recursively go to next var (i'th + 1 other variable)
				if (a(maxDepth, curDepth + 1, focalVal, func)) return true;
			}
			else {				
				// Tests the constraint.
				// If valid, add the focal value back in the focalVar domain.
				if (func.exec(focalVal))
				//if (testAndHandle(focalVal))
					// Return true to to skip further testing of this focal value
					return true; 
			}
		}
		
		return false;
	}
	
	/**
	 * Tests the constraint (testConst). If valid, adds the focalVal arg to
	 * filteredVar domain.
	 * @param focalVal
	 * @return the result of the constraint test.
	 */
	private boolean testAndHandle(int focalVal) {
		boolean result = testConst.getCnetConst().check();
		if (result) {
			filteredVar.addToDomain(focalVal);
		}
		return result;
	}
	
	interface BooleanFunc { public boolean exec(int a); }
	
}
