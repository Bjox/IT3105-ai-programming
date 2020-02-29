package module4.bot.expmax.heuristic;

import module4.game.rep.Board;

/**
 *
 * @author Bjørnar W. Alvestad
 */
public class ExpHeuristic2 implements Heuristic {
	
	private static final int[] VALUES;
	
	static {
		VALUES = new int[7];
		for (int i = 0; i < VALUES.length; i++) {
			VALUES[i] = 1 << i * 4;
		}
	}
	
	@Override
	public double heuristic(Board board) {
		double h = 0.0f;
		
		long boardData = board.getBoardData();
		int n = 0;
		
		// vertically from (0,3) to (0,1)
		for (int i = 3; i >= 1; i--) {
			h += board.getValue(0, i) * VALUES[n++];
		}
		
		// horizontally from (0,0) to (3,0)
		for (int i = 0; i < 4; i++) {
			h += (boardData & 0xFL) * VALUES[n++];
			boardData >>>= 4;
		}
		
		return h;
	}

}
