package module4.bot.expmax;

import java.util.ArrayList;
import module4.game.rep.Board;

/**
 *
 * @author Bjørnar W. Alvestad
 */
public abstract class Node {

	public ArrayList<Node> childs;
	public double value;
	public byte dir = -1;
	public double odds = 1.0f;
	
	public Board board;

	
	public Node() {
		childs = new ArrayList<>();
	}
	
	
	public void addChild(Node child) {
		childs.add(child);
	}

	
	@Override
	public String toString() {
		return value + ", " + getClass().getSimpleName();
	}
	
	
	
	public abstract double calcValue();
	
	
}
