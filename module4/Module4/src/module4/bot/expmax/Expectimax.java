package module4.bot.expmax;

import java.util.ArrayList;
import module4.game.rep.Board;
import module4.bot.expmax.heuristic.*;

/**
 *
 * @author Bjørnar W. Alvestad
 */
public class Expectimax {
	
	public static final Class<? extends Heuristic> DEFAULT_HEURISTIC = ExpHeuristic.class;
	
	public final int max_depth;

	private final Max root;
	
	
	public Expectimax(Board rootData, int maxDepth) {
		root = new Max();
		root.board = rootData;
		this.max_depth = maxDepth;
	}
	
	
	public Node expand() {
		exp(root, 0);
		return root;
	}
	
	
	private void exp(Node n, int depth) {
		// if game over or reached max search depth
		if (n.board.isGameOver() || depth == max_depth) {
			// calculate heuristic at bottom
			n.value = n.board.heuristic();
			return;
		}
		
		// expand max node
		if (n instanceof Max) {
			ArrayList<Chance> chanceNodes = new ArrayList<>();
			
			// for each dir
			for (int dir = Board.LEFT; dir <= Board.DOWN; dir++) {
				// create board copy
				Board dir_succ = new Board(n.board);

				// if move is legal
				if (dir_succ.isLegalMove(dir)) {
					dir_succ.performAction(dir, false);
					dir_succ.checkGameOver();
				} // move dir
				else continue; // try next dir if illegal
				
				// create a new chance node for this dir
				Chance chance_c = new Chance();
				// assign the successor board
				chance_c.board = dir_succ;
				
				chance_c.dir = (byte)dir;
				
				chanceNodes.add(chance_c);
				
				exp(chance_c, depth + 1);
			}
			
			if (!chanceNodes.isEmpty()) {
				double bestValue = chanceNodes.get(0).value;
				n.dir = chanceNodes.get(0).dir;
				
				for (int i = 1; i < chanceNodes.size(); i++) {
					double testValue = chanceNodes.get(i).value;
					if (testValue > bestValue) {
						bestValue = testValue;
						n.dir = chanceNodes.get(i).dir;
					}
				}
				
				n.value = bestValue;
			}
			else {
				n.value = n.board.heuristic();
			}
			
		}
		
		// expand chance node
		else if (n instanceof Chance) {
			long boardData = n.board.getBoardData();
			ArrayList<Max> maxNodes = new ArrayList<>();
			
			// iterate over all squares
			for (int y = 0; y < Board.SIZE; y++) {
				for (int x = 0; x < Board.SIZE; x++) {
					// if free square
					if (n.board.getValue(x, y) == 0) {
						
						// for spawn val 2 and 4
						for (int spawnVal = 1; spawnVal <= 2; spawnVal++) {
							// copy dir board
							Board spawn_succ = new Board(n.board);
							// add spawn tile
							spawn_succ.setValue(x, y, spawnVal); 
							// check for game over
							spawn_succ.checkGameOver();
							
							// create max node
							Max max_c = new Max(); 
							// add board to max node
							max_c.board = spawn_succ; 
							// 90% for 2, 10% for 4
							max_c.odds = spawnVal == 1 ? 0.9f : 0.1f; 
							
							maxNodes.add(max_c);
							
							
							exp(max_c, depth + 1);
						}
						
					}
				}
			}
			
			if (!maxNodes.isEmpty()) {
				double chanceVal = 0.0;
				double odds_acc = 0.0;
				
				for (Max maxNode : maxNodes) {
					chanceVal += maxNode.value * maxNode.odds;
					odds_acc += maxNode.odds;
				}
				
				chanceVal /= odds_acc;
				n.value = chanceVal;
			}
			else {
				n.value = n.board.heuristic();
			}
			
		}
		
	}
	
	
}
