package module4.bot.expmax;

import module4.game.rep.Board;

/**
 *
 * @author Bjørnar W. Alvestad
 */
public class Chance extends Node {

	@Override
	public double calcValue() {
		for (Node c : childs) {
			value += c.calcValue();
		}
		
		return value;
	}
	

	@Override
	public String toString() {
		return super.toString() + ", " + (dir == -1 ? "-1" : Board.DIR_STRING[dir]);
	}

}
