package module1.conf;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;

/**
 *
 * @author Bjox
 */
public class Config {
	
	public Dimension            size;
	public Point                start;
	public Point                goal;
	public ArrayList<Rectangle> obstacles;

	public Config() {
		this.obstacles = new ArrayList<>();
	}

	@Override
	public String toString() {
		String str = "";
		str += "size:\t[" + size.width + "x" + size.height + "]\n";
		str += "start:\t(" + start.x + "," + start.y + ")\n";
		str += "goal:\t(" + goal.x + "," + goal.y + ")\n";
		str += obstacles.size() + " obstacles";
		
		return str;
	}
	
}
