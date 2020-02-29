package module4.bot.expmax.heuristic;

import module4.game.rep.Board;

/**
 *
 * @author Bjørnar W. Alvestad
 */
public class SnakeHeuristic implements Heuristic {
	
	private static final long SNAKE_MASK = 0x1248L;

	@Override
	public double heuristic(Board board) {
		long boardData = board.getBoardData();
		long mask = SNAKE_MASK;
		
		double h = 0.0f;
		
		
		while (boardData != 0) {
			long boardCellValue = boardData & 0xFL;
			long maskCellValue = mask & 0xFL;
			long score = boardCellValue * maskCellValue;
			
			h += score;
			boardData >>>= 4;
			mask >>>= 4;
		}
		
		return h;
	}

}
