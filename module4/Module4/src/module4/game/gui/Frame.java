package module4.game.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import module4.game.Bot;
import module4.game.rep.Board;

/**
 *
 * @author Bjørnar W. Alvestad
 */
public class Frame extends JFrame {
	
	
	private static Color[] colors = {
		Color.LIGHT_GRAY,//new Color(255, 255, 255),	// 0
		new Color(170, 240, 255),	// 2
		new Color(80, 220, 255),	// 4
		new Color(0, 160, 255),		// 8
		new Color(150, 255, 200),	// 16
		new Color(0, 230, 120),		// 32
		new Color(50, 240, 70),		// 64
		new Color(255, 200, 170),	// 128
		new Color(255, 140, 80),	// 256
		new Color(240, 60, 20),		// 512
		new Color(255, 10, 10),		// 1024
		new Color(255, 220, 0),		// 2048
	};
	
	static {
		for (int i = 0; i < colors.length; i++) {
			int c = (int)(255 * i / (double)colors.length);
			colors[i] = new Color(255, 255-c, 255-c);
		}
	}
	
	private static final Font font = new Font("Arial Rounded MT Bold", Font.PLAIN, 40);
	
	
	private PaintPanel pp;
	private Board board;

	private final int w, h, cell_x, cell_y;
	
	public static Point spawnPoint;
	
	public Frame(int w, int h) throws HeadlessException {
		this.cell_x = w / Board.SIZE;
		this.cell_y = h / Board.SIZE;
		
		this.w = cell_x * Board.SIZE;
		this.h = cell_y * Board.SIZE;
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setTitle("2048");
		
		pp = new PaintPanel();
		pp.setPreferredSize(new Dimension(w, h));
		pp.setBackground(Color.WHITE);
		
		add(pp);
		pack();
		
		setLocationRelativeTo(null);
		
		
		if (!Bot.BOT) {
			addKeyListener(new KeyListener() {

				@Override
				public void keyTyped(KeyEvent e) {
				}

				@Override
				public void keyPressed(KeyEvent e) {
					int key = e.getKeyCode();

					if (key >= 37 && key <= 40 && !board.isGameOver()) {
						int dir = -1;

						switch (key) {
							case KeyEvent.VK_UP: dir = Board.UP; break;
							case KeyEvent.VK_DOWN: dir = Board.DOWN; break;
							case KeyEvent.VK_LEFT: dir = Board.LEFT; break;
							case KeyEvent.VK_RIGHT: dir = Board.RIGHT; break;
						}

						boolean legal = board.isLegalMove(dir);

						if (legal)
							board.performAction(dir);

						if (board.isGameOver()) {
							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									JOptionPane.showMessageDialog(null, "Game over!");
								}
							});
						}

						updateBoard(board);
					}

				}

				@Override
				public void keyReleased(KeyEvent e) {}
			});
		}
		
		
	}
	
	public void updateBoard(Board board) {
		this.board = board;
		pp.repaint();
		setTitle("2048 - Score: " + formatIntVal(board.getScore()));
	}
	
	
	 static String formatIntVal(int val) {
			String valStr = Integer.toString(val);
			
			for (int i = valStr.length() - 3; i >= 1; i -= 3) {
				valStr = valStr.substring(0, i) + "," + valStr.substring(i);
			}
			
			return valStr;
		}
	
	
	class PaintPanel extends JPanel {

		@Override
		public void paint(Graphics g) {
			Graphics2D g2 = (Graphics2D)g;
			RenderingHints rh = new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setRenderingHints(rh);
			
			super.paint(g);
			
			if (board == null)
				return;
			
			// paint squares
			for (int x = 0; x < Board.SIZE; x++) {
				for (int y = 0; y < Board.SIZE; y++) {
					int val = board.getValue(x, Board.SIZE - y - 1);
					if (val == 0) continue;
					
					g.setColor(val >= colors.length ? Color.DARK_GRAY : colors[val]);
					paintCell(x, y, g);
					
					// draw label
					if (val > 0) {
						g.setColor(val >= 9 ? Color.WHITE : Color.BLACK);
						g.setFont(font);
						String label = formatIntVal(1 << val);
						g.drawString(label, x * cell_x + cell_x / 2 - label.length() * 11, y * cell_y + cell_y / 2 + 10);
					}
					
				}
			}
			
			if (spawnPoint != null) {
				g.setColor(Color.RED);
				g.drawRoundRect(spawnPoint.x * cell_x + 5, (3-spawnPoint.y) * cell_y + 5, cell_x - 10, cell_y - 10, 0, 0);
			}
			
			// paint grid
			g.setColor(Color.WHITE);
			g2.setStroke(new BasicStroke(6.0f));
			
			
			for (int i = 0; i <= Board.SIZE; i++)
				g.drawLine(i * cell_x, 0, i * cell_x, h);
			for (int i = 0; i <= Board.SIZE; i++)
				g.drawLine(0, i * cell_y, w, i * cell_y);
			
		}
		
		private void paintCell(int xx, int yy, Graphics g) {
			//g.fillRect(xx * cell_x, yy * cell_y, cell_x, cell_y);
			g.fillRoundRect(xx * cell_x, yy * cell_y, cell_x, cell_y, 30, 30);
		}
		
	}
	
}
