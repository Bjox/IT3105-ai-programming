package module4.bot.expmax;

/**
 *
 * @author Bjørnar W. Alvestad
 */
public class Max extends Node {
	
	@Override
	public double calcValue() {
		if (childs.size() > 0) {
			value = childs.get(0).calcValue();
			dir = childs.get(0).dir;
			
			for (int i = 1; i < childs.size(); i++) {
				double c_value = childs.get(i).calcValue();
				if (c_value > value) {
					value = c_value;
					dir = childs.get(i).dir;
				}
			}
			
			value *= odds;
		} else {
			value = board.heuristic();
		}
		
		return value;
	}

	
	@Override
	public String toString() {
		return super.toString() + ", " + odds;
	}
	
}
