
package module2.io;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;
import module2.graph.Edge;
import module2.graph.Graph;
import module2.graph.Vertex;

/**
 *
 * @author Bjox
 */
public final class GraphReader {
	
	public final int NV;
	public final int NE;
	
	private float max_x = Float.MIN_VALUE, max_y = Float.MIN_VALUE, min_x = Float.MAX_VALUE, min_y = Float.MAX_VALUE;
	
	private final Vertex[] vertices;

	public GraphReader(String filename) throws IOException {
		System.out.println("Reading graph file \"" + filename + "\"...");
		
		BufferedReader br = new BufferedReader(new FileReader(filename));
		StringTokenizer st = new StringTokenizer(br.readLine());
		
		NV = Integer.parseInt(st.nextToken());
		NE = Integer.parseInt(st.nextToken());
		
		System.out.println("Number of vertices:\t" + NV + "\nNumber of edges:\t" + NE);
		
		vertices = new Vertex[NV];
		
		for (int i = 0; i < NV; i++) {
			st = new StringTokenizer(br.readLine());
			if (Integer.parseInt(st.nextToken()) != i)
				System.err.println("WARNING! Inconsistent vertex indices detected during parsing.");
			
			Vertex v = new Vertex(i);
			v.X = Float.parseFloat(st.nextToken());
			v.Y = Float.parseFloat(st.nextToken());
			
			if (v.X > max_x) max_x = v.X;
			if (v.Y > max_y) max_y = v.Y;
			
			if (v.X < min_x) min_x = v.X;
			if (v.Y < min_y) min_y = v.Y;
			
			vertices[i] = v;
		}
		
		for (int i = 0; i < NE; i++) {
			st = new StringTokenizer(br.readLine());
			
			int v1 = Integer.parseInt(st.nextToken());
			int v2 = Integer.parseInt(st.nextToken());
			
			addEdge(v1, v2, vertices);
			//addEdge(v2, v1, vertices); // uncomment for undirected graph
		}
	}
	
	
	public Graph getGraph() {
		Graph g = new Graph(vertices);
		
		g.max_x = max_x;
		g.max_y = max_y;
		g.min_x = min_x;
		g.min_y = min_y;
		
		return g;
	}
	
	
	private void addEdge(int from, int to, Vertex[] vertices) {
		Edge e  = new Edge();
		e.vertex = vertices[to];
		e.next = vertices[from].edgeList;
		vertices[from].edgeList = e;
	}
	
	
}
