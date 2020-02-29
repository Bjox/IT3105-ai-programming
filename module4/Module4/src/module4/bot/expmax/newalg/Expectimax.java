package module4.bot.expmax.newalg;

import module4.game.rep.Board;

/**
 *
 * @author Bjørnar W. Alvestad
 */
public class Expectimax {

	private Expectimax() {
	}
	
	private static int best_dir = -1;
	
	public static int expectimax(Board board, int depth) {
		best_dir = -1;
		double a = expectimax(new Node(board, true), depth, true);
		return best_dir;
	}
	
	
	private static double expectimax(Node node, int depth, boolean first) {
		if (node.board.isGameOver() || depth == 0) {
			return node.board.heuristic();
		}
		
		double a = 0;
		if (node.isMax) {
			for (int dir = Board.LEFT; dir <= Board.DOWN; dir++) {
				Board dir_succ = new Board(node.board);
				
				if (dir_succ.isLegalMove(dir)) {
					dir_succ.performAction(dir, false);
//					dir_succ.checkGameOver();
				}
				else {
					continue;
				}
				
				Node child = new Node(dir_succ, false);
				child.dir = dir;
				
				double old_a = a;
				a = Math.max(a, expectimax(child, depth - 1, false));
				if (first && a != old_a) {
					best_dir = dir;
				}
			}
		}
		else {  // is chance node
			double prob_acc = 0;
			for (int tile_i = 0; tile_i < 16; tile_i++) {
				if (node.board.getValue(tile_i) == 0) {
					for (int spawnVal = 1; spawnVal <= 2; spawnVal++) {
						Board spawn_succ = new Board(node.board);
						
						spawn_succ.setValue(tile_i, spawnVal);
//						spawn_succ.checkGameOver();
						
						Node child = new Node(spawn_succ, true);
						
						double prob = spawnVal == 1 ? 0.9f : 0.1f;
						prob_acc += prob;
						a += prob * expectimax(child, depth - 1, false);
					}
				}
			}
			a /= prob_acc;
		}
		
		return a;
	}
	
	
	public static class Node {
		
		public final Board board;
		public int dir = -1;
		public final boolean isMax;

		public Node(Board board, boolean isMax) {
			this.board = board;
			this.isMax = isMax;
		}
	}
}
