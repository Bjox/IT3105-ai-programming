package astargac.gac;

import astargac.astar.ConstraintInstance;
import astargac.astar.VariableInstance;


/**
 *
 * @author Bjox
 */
public class TodoRevisePair {
	
	public final VariableInstance focalVar;
	public final ConstraintInstance constraint;

	
	public TodoRevisePair(VariableInstance focalVar, ConstraintInstance constraint) {
		this.focalVar = focalVar;
		this.constraint = constraint;
	}

	@Override
	public String toString() {
		return constraint.toString() + " : " + focalVar.toString();
	}
	
	
}
