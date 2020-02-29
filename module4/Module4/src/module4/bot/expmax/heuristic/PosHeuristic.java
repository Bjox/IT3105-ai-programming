package module4.bot.expmax.heuristic;

import module4.game.rep.Board;

/**
 *
 * @author Bjørnar W. Alvestad
 */
public class PosHeuristic implements Heuristic{

	/**
	* Mask:<br>
	* |2|1|1|2|<br>
	* |1|0|0|1|<br>
	* |1|0|0|1|<br>
	* |2|1|1|2|
	*/
	private static final long POS_SCORE_MASK = 0x2112100110012112L;
	
	@Override
	public double heuristic(Board board) {
		long boardData = board.getBoardData();
		
		double h = 0.0f;
		
		// pos score for all squares
		for (int i = 0; i < Long.SIZE; i += 4) {
			 long score = (boardData >>> i & 0xFL) * (POS_SCORE_MASK >>> i & 0xFL);
			 h += score;
		}
		
		h += board.getFreeSquares();
		
		return h;
	}

}
