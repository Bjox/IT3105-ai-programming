package module2.gui;

import astargac.astar.CspState;
import astargac.astar.VariableInstance;
import java.awt.BorderLayout;
import java.awt.Color;
import static java.awt.Color.*;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.RenderingHints;
import javax.swing.JFrame;
import javax.swing.JPanel;
import module2.graph.Edge;
import module2.graph.Graph;
import module2.graph.Vertex;

/**
 *
 * @author Bjox
 */
public class Frame extends JFrame {
	
	public static final Color[] COLOR_IDS = {
		RED,
		YELLOW,
		BLUE,
		GREEN,
		ORANGE,
		CYAN,
		PINK,
		MAGENTA,
		BLACK,
		WHITE
	};
	
	public static final int VERTEX_SIZE = 15;
	public static final Color LINE_COLOR = GRAY;
		
	
	private final PaintPanel pp;
	private final Graph graph;
	
	private final int width, height;
	
	private CspState state;
	
	
	public Frame(int width, int height, Graph graph) throws HeadlessException {
		this.graph = graph;
		this.width = width;
		this.height = height;
		
		graph.max_x += Math.abs(graph.min_x);
		graph.max_y += Math.abs(graph.min_y);

		
		for (Vertex v : graph.vertices) {
			v.X += Math.abs(graph.min_x);
			v.Y += Math.abs(graph.min_y);
			
			v.screen_x = (width - VERTEX_SIZE * 2) * v.X / graph.max_x + VERTEX_SIZE;
			v.screen_y = (height - VERTEX_SIZE * 2) * v.Y / graph.max_y + VERTEX_SIZE;
		}
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		
		BorderLayout layout = new BorderLayout();
		setLayout(layout);
		
		pp = new PaintPanel();
		pp.setPreferredSize(new Dimension(width, height));
		pp.setBackground(DARK_GRAY.darker().darker());
		add(pp, BorderLayout.CENTER);
		
		pack();
		setLocationRelativeTo(null);
	}
	

	public void updateState(CspState state) {
		this.state = state;
		pp.repaint();
	}
	
	
	class PaintPanel extends JPanel {

		@Override
		public void paint(Graphics g) {
			Graphics2D g2 = (Graphics2D)g;
			RenderingHints rh = new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setRenderingHints(rh);
			
			super.paint(g);

			for (Vertex v : graph.vertices) {
				Edge e = v.edgeList;
				while (e != null) {
					paintEdge(e, v, g);
					e = e.next;
				}
			}
			
			for (Vertex v : graph.vertices) {
				int x = (int)v.screen_x - VERTEX_SIZE / 2;
				int y = (int)v.screen_y - VERTEX_SIZE / 2;
				
				g.setColor(getBackground());
				g.fillOval(x, y, VERTEX_SIZE, VERTEX_SIZE);
			}
			
			for (Vertex v : graph.vertices)
				paintVertex(v, g);
			
		}
		
		private void paintVertex(Vertex v, Graphics g) {			
			int x = (int)v.screen_x - VERTEX_SIZE / 2;
			int y = (int)v.screen_y - VERTEX_SIZE / 2;
			
			//g.setColor(LINE_COLOR);
			
			g.setColor(new Color(100, 100, 100, 50));
			if (state != null) {
				VariableInstance vi = state.getVi(v.getName());
				if (vi.isSingleton())
					g.setColor(COLOR_IDS[vi.getDomain()[0]]);
			}
			g.fillOval(x, y, VERTEX_SIZE, VERTEX_SIZE);
			
			g.setColor(LINE_COLOR);
			g.drawOval(x, y, VERTEX_SIZE, VERTEX_SIZE);
		}
		
		private void paintEdge(Edge e, Vertex from, Graphics g) {
			g.setColor(LINE_COLOR);
			
			g.drawLine((int)e.vertex.screen_x, (int)e.vertex.screen_y, (int)from.screen_x, (int)from.screen_y);
		}
		
	}
	
}
