package module3.gui;

import astargac.astar.CspState;
import astargac.astar.VariableInstance;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.RenderingHints;
import javax.swing.JFrame;
import javax.swing.JPanel;
import module3.VarLocation;
import module3.nonogram.Nonogram;

/**
 *
 * @author Bjox
 */
public class Frame extends JFrame {
	
	
	private final PaintPanel pp;
	private CspState state;
	
	private final int width, height;
	private final int cell_width, cell_height;
	
	private final int X, Y;
	
	private static final Color FILLED_COLOR = new Color(0, 94, 125);
	private static final Color UNFILLED_COLOR = new Color(255, 255, 255);
	private static final Color GRID_COLOR = new Color(200, 200, 200);
	private static final Color UNKNOWN_COLOR = new Color(150, 150, 150);
	
	public Frame(int width, int height, Nonogram nonogram) throws HeadlessException {
		this.width = width;
		this.height = height;
		
		this.X = nonogram.X;
		this.Y = nonogram.Y;
		
		this.cell_width = width / X;
		this.cell_height = height / Y;
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		
		BorderLayout layout = new BorderLayout();
		setLayout(layout);
		
		pp = new PaintPanel();
		pp.setPreferredSize(new Dimension(cell_width * X, cell_height * Y));
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
			
			if (state != null) {			
				int[][] cellStatus = new int[X][Y];

				for (VariableInstance vi : state.getVariableInstances()) {
					if (vi.isSingleton()) {
						int bitField = vi.getDomain()[0];
						VarLocation varLoc = module3.Main.varData.get(vi.getName());
						VarLocation.VarType type = varLoc.type;
						int index = varLoc.index;

						int xx, yy;
						if (type == VarLocation.VarType.COLUMN) {
							xx = index;

							for (int i = 0; i < Y; i++) {
								cellStatus[xx][i] += readBit(bitField, i) ? 2 : 1;
							}

						} else {
							yy = index;

							for (int i = 0; i < X; i++) {
								cellStatus[i][yy] += readBit(bitField, i) ? 8 : 4;
							}
						}
					}
				}

				for (int xx = 0; xx < X; xx++) {
					for (int yy = 0; yy < Y; yy++) {
						switch (cellStatus[xx][yy]) {
							// Agree on filled
							case 10: paintCell(xx, yy, FILLED_COLOR, g); break;
							// Agree in not filled
							case 5: paintCell(xx, yy, UNFILLED_COLOR, g); break;
							// Filled and filled disagreement
							case 9: case 6: paintCell(xx, yy, Color.red, g); break;
							// Row and column does not agree
							default: paintCell(xx, yy, UNKNOWN_COLOR, g); break;
						}
					}
				}
			} else {
				g.setColor(UNKNOWN_COLOR);
				g.fillRect(0, 0, width, height);
			}
			
			//paintGrid(g);
		}
		
		private boolean readBit(int val, int i) {
			return (val & (1 << i)) != 0;
		}
		
		private void paintCell(int xx, int yy, Color c, Graphics g) {
			g.setColor(c);
			g.fillRect(xx * cell_width, (Y - yy - 1) * cell_height, cell_width, cell_height);
		}
		
		private void paintGrid(Graphics g) {
			g.setColor(GRID_COLOR);
			
			for (int i = 1; i < X; i++) {
				g.drawLine(i * cell_width, 0, i * cell_width, height);
			}
			
			for (int i = 1; i < Y; i++) {
				g.drawLine(0, i * cell_height, width, i * cell_height);
			}
		}
		
	}
	
}
