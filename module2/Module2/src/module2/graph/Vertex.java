package module2.graph;

/**
 *
 * @author Bjox
 */
public class Vertex {
	
	public Edge edgeList;
	
	/** The vertex position. */
	public float X, Y;
	
	/** Vertex id. */
	public final int id;
	
	public float screen_x, screen_y;
	
	private String name = "";
	
	public Vertex(int i) {
		id = i;

		do {
			name += (char)('A' + (i % 26));
			i /= 26;
		} while (i != 0);
	}
	
	
	public int getColorId() {
		return 0;
	}
	
	
	public String getName() {
		return name;
	}
	

	@Override
	public String toString() {
		return "Vertex " + Integer.toString(id) + " (" + X + ", " + Y + ")";
	}
	
}
