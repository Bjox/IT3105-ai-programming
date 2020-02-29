package module1;

import astar.Astar;
import astar.AstarAuxiliary;
import astar.Node;
import module1.conf.Config;
import module1.conf.ConfigReader;
import module1.gui.Frame;
import java.awt.Dimension;
import java.io.FileNotFoundException;

/**
 *
 * @author Bjox
 */
public class Main {
	
	public static final Object lock = new Object();
	private static int mode = -1;
	public static int delay = 50;
	
	public static final boolean EDIT_MODE = false;

	public static void main(String[] args) throws Exception {
		
		if (args.length < 2) {
			System.out.println("This program takes 3 arguments:");
			System.out.println("Module1.jar [SEARCH_MODE] [config file] [gui delay = 50]");
			System.out.println("Where:\n[SEARCH_MODE] is one of the following:");
			System.out.println("\t\"BEST_FIRST\", \"DEPTH_FIRST\", \"BREADTH_FIRST\"");
			System.out.println("[config file] is a file containing the specifications of the navigation grid.");
			System.out.println("[gui delay] (OPTIONAL) is the delay between every frame update, in ms. (Default 50 ms).");
			return;
		}
		
		if (args.length > 2) {
			try {
				delay = Integer.parseInt(args[2]);
			} catch (Exception e) {
				System.out.println("Error parsing argument \"" + args[2] + "\": is not a number.");
				return;
			}
		}
		
		parseMode(args[0]);
		if (mode == -1)
			return;
		
		Config conf;
		try {
			conf = ConfigReader.readFile(args[1]);
		} catch (FileNotFoundException e) {
			System.out.println("File not found (\"" + args[1] + "\").");
			return;
		} catch (Exception e) {
			System.out.println("An error occurred during config file parsing.");
			System.out.println(e.getClass().getSimpleName() + ": " + e.getMessage());
			return;
		}
		
		System.out.println(args[0].toUpperCase() + " mode.");
		
		
		Board board = new Board(conf);
		
		Frame frame = new Frame();
		frame.setTitle("A* visualization");
		frame.init(new Dimension(400, 400), new Dimension(board.X, board.Y), board);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		class FrameAux implements AstarAuxiliary {
			@Override
			public void poppedNode(Node node) {
				frame.updateTrail(node);
				try {
					Thread.sleep(delay);
				} catch (Exception e) {}
			}
		}
		
		final FrameAux frameAux = new FrameAux();
		
		while (true) {
			synchronized (lock) {
				if (EDIT_MODE) lock.wait();
			}
			
			MazeState initState = new MazeState();
			MazeState.board = board;
			initState.position = board.start;	


			Astar<MazeState> astar = new Astar<>(mode, frameAux);
			astar.search(initState);
			Node goal = astar.getSolutionNode();
					


			if (goal != null) {
				System.out.println("Nodes generated:\t" + astar.getNodesGenerated());
				System.out.println("Solution length:\t" + goal.depth());

				frame.updateTrail(goal);
			}
			else {
				System.out.println("No solution found.");
			}
			
			if (!EDIT_MODE) break;
		}
	}
	
	public static void parseMode(String str) {
		switch (str.toUpperCase()) {
			case "BEST_FIRST":
				mode = Astar.BEST_FIRST;
				break;
			case "BREADTH_FIRST":
				mode = Astar.BREADTH_FIRST;
				break;
			case "DEPTH_FIRST":
				mode = Astar.DEPTH_FIST;
				break;
			default:
				System.out.println("Invalid search mode \"" + str + "\". Valid inputs are \"BEST_FIRST\", \"DEPTH_FIRST\" and \"BREADTH_FIRST\".");
		}
	}
	
}
