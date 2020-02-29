package module1;

import module1.conf.Config;
import java.awt.Point;

/**
 *
 * @author Bjox
 */
public class Board {
	
	public final int X, Y;
	public final Point start, goal;
	public final boolean[][] squares;
	
	public Board(Config config) {
		X = config.size.width;
		Y = config.size.height;
		start = config.start;
		goal = config.goal;
		
		squares = new boolean[X][Y];
		
		config.obstacles.stream().forEach((rect) -> {
			
			for (int i = 0; i < rect.height; i++) {
				for (int j = 0; j < rect.width; j++) {
					squares[rect.x + j][rect.y + i] = true;
				}
			}
			
		});
	}
	
	public boolean isFree(int x, int y) {
		return !squares[x][y];
	}

	@Override
	public String toString() {
		String str = "";
		
		for (int i = Y-1; i >= 0; i--) {
			for (int j = 0; j < X; j++) {
				if (j == start.x && i == start.y) {
					str += "S";
				}
				else if (j == goal.x && i == goal.y) {
					str += "G";
				}
				else {
					str += squares[j][i] ? "#" : ".";
				}
				str += " ";
			}
			str += "\n";
		}
		
		return str;
	}
	
}
