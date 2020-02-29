package module4.bot.expmax.heuristic;

import module4.game.rep.Board;

/**
 *
 * @author Bjørnar W. Alvestad
 */
public class ExpHeuristic implements Heuristic {
	
	private static final int EXP = 4;
	
	@Override
	public double heuristic(Board board) {
		double h = 0.0;
		
		long boardData = board.getBoardData();
		long exp = 1;
		
		// vertically from (0,3) to (0,1)
		for (int i = 3; i >= 1; i--) {
			h += board.getValue(0, i) * exp;
			exp <<= EXP;
		}
		
		// horizontally from (0,0) to (3,0)
		for (int i = 0; i < 4; i++) {
			h += (boardData & 0xFL) * exp;
			boardData >>>= 4;
			exp <<= EXP;
		}
		
		return h;
	}
	
	
}
