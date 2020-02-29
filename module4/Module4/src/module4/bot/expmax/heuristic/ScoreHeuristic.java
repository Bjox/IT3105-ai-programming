package module4.bot.expmax.heuristic;

import module4.game.rep.Board;

/**
 *
 * @author Bjørnar W. Alvestad
 */
public class ScoreHeuristic implements Heuristic {

	@Override
	public double heuristic(Board board) {
		return board.getScore();
	}

}
