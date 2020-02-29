package module1.gui;

import module1.Board;
import module1.Main;
import module1.MazeState;
import module1.SimpleMouseListener;
import astar.Node;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Bjox
 */
public class Frame extends JFrame {
		
	private PaintPanel pp;
	private int cell_x, cell_y, x, y;
	private Board board;
	
	public Frame() throws HeadlessException {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		
		BorderLayout layout = new BorderLayout();
		setLayout(layout);
		
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
		if (Main.EDIT_MODE) add(buttonPanel, BorderLayout.NORTH);
		
		
		JButton startButt = new JButton("START");
		buttonPanel.add(startButt);
		
		startButt.addMouseListener(new SimpleMouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				synchronized (Main.lock) {
					Main.lock.notifyAll();
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {}
		});
		
		
		JButton resetButt = new JButton("RESET");
		buttonPanel.add(resetButt);
		
		resetButt.addMouseListener(new SimpleMouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				updateTrail(null);
			}

			@Override
			public void mousePressed(MouseEvent e) {}
		});
		
		
		String[] modes = { "BEST_FIRST", "DEPTH_FIRST", "BREADTH_FIRST" };
		JComboBox<String> modeSelect = new JComboBox<>(modes);
		buttonPanel.add(modeSelect);
		
		modeSelect.addActionListener((ActionEvent e) -> {
			String sel = (String)((JComboBox)e.getSource()).getSelectedItem();
			Main.parseMode(sel);
		});
		
		
		SpinnerNumberModel snm = new SpinnerNumberModel(50, 0, 1000, 1);
		JSpinner delaySpinner = new JSpinner(snm);
		buttonPanel.add(delaySpinner);
		
		delaySpinner.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				int delay = (int)((JSpinner)e.getSource()).getValue();
				Main.delay = delay;
			}
		});
		
	}
	
	public void init(Dimension preferredDim, Dimension gridSize, Board board) {
		this.board = board;
		
		x = gridSize.width;
		y = gridSize.height;
		cell_x = preferredDim.width / x;
		cell_y = preferredDim.height / y;
		
		pp = new PaintPanel();
		pp.setPreferredSize(new Dimension(x * cell_x, y * cell_y));
		add(pp, BorderLayout.CENTER);
		
		if (Main.EDIT_MODE) {
			pp.addMouseListener(new SimpleMouseListener() {
				@Override
				public void mouseClicked(MouseEvent e) {
				}

				@Override
				public void mousePressed(MouseEvent e) {
					mouse(e);
				}
			});

			pp.addMouseMotionListener(new MouseMotionListener() {

				@Override
				public void mouseDragged(MouseEvent e) {
					mouse(e);
				}

				@Override
				public void mouseMoved(MouseEvent e) {}

			});
		}

		pack();
	}
	
	private void mouse(MouseEvent e) {
		boolean remove = (e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) != 0;
		int xx = e.getPoint().x / cell_x;
		int yy = y - e.getPoint().y / cell_y - 1;
		
		if (xx < 0 || xx >= x || yy < 0 || yy >= y) return;

		if (board.squares[xx][yy] != remove) {
			board.squares[xx][yy] = remove;
			pp.repaint();
		}
	}

	public void updateTrail(Node n) {
		pp.node = n;
		pp.repaint();
	}
	
	
	class PaintPanel extends JPanel {
		
		private Node node;

		@Override
		public void paint(Graphics g) {
			super.paint(g);
			resetTrail(g);
			
			Node n = node;
			MazeState ms;
			while (n != null) {
				ms = (MazeState)n.state;
				paintCell(ms.position.x, ms.position.y, Color.RED, g);
				n = n.parent;
			}
			
		}
		
		private void paintCell(int xx, int yy, Color color, Graphics g) {
			g.setColor(color);
			g.fillRect(xx * cell_x, (y - yy - 1) * cell_y, cell_x, cell_y);
		}
		
		private void resetTrail(Graphics g) {
			if (board == null) return;
			
			for (int i = 0; i < board.X; i++) {
				for (int j = 0; j < board.Y; j++) {
					paintCell(i, j, board.isFree(i, j) ? Color.WHITE : Color.DARK_GRAY, g);
				}
			}

			paintCell(board.start.x, board.start.y, Color.YELLOW, g);
			paintCell(board.goal.x, board.goal.y, Color.GREEN, g);
		}
		
		
		
		
	}
	
}
