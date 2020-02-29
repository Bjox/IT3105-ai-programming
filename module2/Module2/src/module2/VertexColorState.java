package module2;

import astar.AstarState;
import astargac.astar.CspState;
import astargac.astar.VariableInstance;
import astargac.gac.Gac;
import java.util.ArrayList;
import module2.graph.Graph;

/**
 *
 * @author Bjox
 */
public class VertexColorState extends CspState {
	
	private final Graph graph;
	

	public VertexColorState(Graph graph) {
		super();
		this.graph = graph;
	}

	
	public VertexColorState(VertexColorState original) {
		super(original);
		graph = original.graph;
	}
	
	
	public VertexColorState(CspState original, Graph graph) {
		super(original);
		this.graph = graph;
	}

	
	@Override
	public AstarState[] generateChildren() {
		ArrayList<VertexColorState> children = new ArrayList<>();
		
		VariableInstance varToExpand = null;
		
		do {
			index++;
			if (index >= graph.vertices.length) return new AstarState[0];
			
			varToExpand = vis.get(graph.vertices[index].getName());
			
			if (varToExpand.domainSize() == 0) return new AstarState[0];
		} while (varToExpand.domainSize() == 1);
		
		
		for (int dv : varToExpand.getDomain()) {
			VertexColorState newState = new VertexColorState(this);

			Gac gac = new Gac(newState);
			gac.rerun(varToExpand.getName(), dv);

			if (!newState.isContradictory()) children.add(newState);
		}
		
		return children.toArray(new VertexColorState[0]);
	}
	
	
	
}
