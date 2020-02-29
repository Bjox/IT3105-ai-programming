package module2;

import astar.Astar;
import astar.AstarAuxiliary;
import astar.Node;
import astargac.astar.Cnet;
import astargac.astar.CspState;
import astargac.gac.Gac;
import module2.graph.Edge;
import module2.graph.Graph;
import module2.graph.Vertex;
import module2.gui.Frame;
import module2.io.GraphReader;

/**
 *
 * @author Bjox
 */
public class Main {
	
	public static int[] range(int a, int b) {
		int[] r = new int[b - a + 1];
		for (int i = a; i <= b; i++) r[i-a] = i;
		return r;
	}

	public static void main(String[] args) throws Exception {
		
		if (args.length < 2) {
			System.out.println("This program takes 2 arguments:");
			System.out.println("Module2.jar [number of colors] [config file]");
			System.out.println("Where:");
			System.out.println("[number of colors] is the maximum number of colors used in this vertex coloring problem.");
			System.out.println("[config file] is a file containing the specifications of the graph to be colored.");
			return;
		}
		
		int K;
		
		try {
			K = Integer.parseInt(args[0]);
			System.out.println("K = " + K);
		} catch (NumberFormatException e) {
			System.out.println("Invalid argument \"" + args[0] + "\". Expected integer.");
			return;
		}
		
		GraphReader graphr = new GraphReader(args[1]);
		
		Graph graph = graphr.getGraph();
		
		Frame frame = new Frame(800, 800, graph);
		frame.setTitle("Vertex Coloring");
		frame.setVisible(true);
		
		Cnet cnet = new Cnet();
		
		System.out.println("Generating constraints...");
		
		LoadingBar bar = new LoadingBar(50, graph.vertices.length);
		
		for (Vertex v1 : graph.vertices) {
			Edge e = v1.edgeList;
			
			while (e != null) {
				Vertex v2 = e.vertex;
				
				String constraint = v1.getName() + " != " + v2.getName();
				cnet.addConstraint(constraint);
				
				e = e.next;
			}
			
			bar.step();
		}
		
		bar.finish();
		
		System.out.println("Generated " + graph.vertices.length + " constraints");
		
		System.out.println("Adding domain values...");
		cnet.addToAllVariableDomains(range(0, K - 1));
		
		
		VertexColorState state = new VertexColorState(cnet.generateCspState(), graph);

		System.out.println("Solving GAC...");
		Gac gac = new Gac(state);
		gac.solve();

		Astar<CspState> astar = new Astar(Astar.BEST_FIRST, (AstarAuxiliary<CspState>) (Node<CspState> n) -> {
			frame.updateState(n.state);
			try {
				Thread.sleep(20);
			} catch (Exception e) {
			}
		});

		System.out.println("Starting search...");
		CspState solution = astar.search(state);

		if (solution == null) {
			System.out.println("No solution found.");
		}

		frame.updateState(solution);
			
		
		
		
		System.out.println();
		System.out.println("Unsatisfied constraints:\t" + solution.getNumberOfUnsatisfiedConstraints());
		System.out.println("Vertices w/o color assignment:\t" + (solution.getNumberOfVariables() - solution.getNumberOfSingletons()));
		System.out.println("Nodes generated:\t\t" + astar.getNodesGenerated());
		System.out.println("Nodes popped from agenda:\t" + astar.getPoppedNodes());
		System.out.println("Assumptions made:\t\t" + astar.getSolutionDepth());
		
		
	}
	

}
