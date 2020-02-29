package module4.game;

import javax.swing.JOptionPane;
import module4.game.rep.Board;
import module4.game.gui.Frame;
import module4.bot.expmax.*;

/**
 *
 * @author Bjørnar W. Alvestad
 */
public class Bot {
	
	public static boolean BOT = true;
	public static int runs = 0;
	public static boolean NEW_ALG = true;
	public static int DEPTH = 6;
	
	public static void main(String[] args) {
		if (args.length > 0) {
			try {
				DEPTH = Integer.parseInt(args[0]);
			} catch (Exception e) {
				System.out.println("Invalid argument \"" + args[0] + "\". Expected integer.");
			}
		}
		
		if (args.length > 1) {
			BOT = false;
		}
		
		System.out.println("Search depth: " + DEPTH);
		System.out.println("Human player: " + (BOT ? "No" : "Yes"));
		
		Frame frame = new Frame(600, 600);
		frame.setVisible(true);
		
		do {
			runs++;
			
			Board board = new Board();
			board.spawnTileRnd();
			board.spawnTileRnd();

			frame.updateBoard(board);

			if (BOT) {
				while (!board.isGameOver()) {
					if (NEW_ALG) {
						try {
							board.performAction(module4.bot.expmax.newalg.Expectimax.expectimax(board, DEPTH));
						} catch (Exception e) {
							break;
						}
					} else {
						Expectimax exp = new Expectimax(board, DEPTH);
						Node root = exp.expand();
						board.performAction(root.dir);
					}
					
					board.checkGameOver();
					frame.updateBoard(board);
				}

				System.out.println("Run: " + runs + "\tMax tile: " + (1 << board.getHighestValue()));
			}
			else break;
		} while (JOptionPane.showConfirmDialog(frame, "Rerun?", "Rerun", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION);
		
	}
	
	
	
	
	private static void debug(Board board) {
		board.setValue(1, 0, 1);
		board.setValue(2, 0, 2);
		board.setValue(3, 0, 3);
		board.setValue(0, 1, 4);
		board.setValue(1, 1, 5);
		board.setValue(2, 1, 6);
		board.setValue(3, 1, 7);
		board.setValue(0, 2, 8);
		board.setValue(1, 2, 9);
		board.setValue(2, 2, 10);
		board.setValue(3, 2, 11);
		board.setValue(0, 3, 12);
		board.setValue(1, 3, 13);
		board.setValue(2, 3, 14);
		board.setValue(3, 3, 15);
	}
	
	
}
