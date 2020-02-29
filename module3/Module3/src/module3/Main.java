package module3;

import astar.Astar;
import astar.AstarAuxiliary;
import astar.Node;
import astargac.astar.Cnet;
import astargac.astar.CspState;
import astargac.gac.Gac;
import java.util.HashMap;
import module3.gui.Frame;
import module3.io.NonogramReader;
import module3.nonogram.Nonogram;
import module3.nonogram.Strip;


/**
 *
 * @author Bjox
 */
public class Main {
	
	public static HashMap<String, VarLocation> varData = new HashMap<>();
	private static int gui_delay = 200;

	public static void main(String[] args) throws Exception {
		
		if (args.length < 1) {
			System.out.println("This program takes 2 arguments:");
			System.out.println("Module3.jar [config file] [gui delay = 200ms]");
			System.out.println("Where:");
			System.out.println("[config file] is a file containing the nonogram specifications.");
			System.out.println("[gui delay] (OPTIONAL) is the delay between each gui update, in ms.");
			return;
		}
		
		try {
			gui_delay = Integer.parseInt(args[1]);
			System.out.println("gui_delay = " + gui_delay);
		} catch (Exception e) {
		}
		
		NonogramReader nonReader = new NonogramReader(args[0]);
		Nonogram nonogram = nonReader.getNonogram();
		
		if (nonogram.X > 32 || nonogram.Y > 32) {
			System.out.println("Error. Unsupported nonogram size. Max dimensions are 32x32.");
			return;
		}
		
		Cnet cnet = new Cnet();
		
		Frame frame = new Frame(nonogram.X * 25, nonogram.Y * 25, nonogram);
		frame.setTitle("Nonogram solver");
		frame.setVisible(true);
		
		System.out.println("Generating constraints...");
			
		LoadingBar bar = new LoadingBar(50, nonogram.X * nonogram.Y);
		
		for (int y = 0; y < nonogram.Y; y++) {
			
			
			for (int x = 0; x < nonogram.X; x++) {			
				String row = toStringBase(y + nonogram.COLUMNS);
				String column = toStringBase(x);
				
				// ((Var1 >> Var2i XOR Var2 >> Var1i) & 1) == 0
				String constraint = "((" + column + " >>> " + y + " ^ " + row + " >>> " + x + ") & 1) == 0";
				
				//System.out.println(column + ", " + row + "\t(" + x + ", " + y + ")  \t" + constraint);
				
				cnet.addConstraint(constraint);
				
				bar.step();
			}
		}
		
		bar.finish();
		
		
		System.out.println("Generated " + nonogram.X * nonogram.Y + " constraints");
		
		// iterate over all columns
		for (int i = 0; i < nonogram.COLUMNS; i++) {
			Strip column = new Strip(nonogram.columnspec[i], nonogram.Y);
			String name = toStringBase(i);
			
			varData.put(name, new VarLocation(VarLocation.VarType.COLUMN, i));
			
			for (Strip perm : column.getPermutations()) {
				int bitField = perm.getBitField(true);
				cnet.addToVariableDomain(name, bitField);
			}
		}
		
		// iterate over all rows
		for (int i = 0; i < nonogram.ROWS; i++) {
			Strip row = new Strip(nonogram.rowspec[i], nonogram.X);
			String name = toStringBase(i + nonogram.COLUMNS);
			
			varData.put(name, new VarLocation(VarLocation.VarType.ROW, i));
			
			for (Strip perm : row.getPermutations()) {
				int bitField = perm.getBitField(false);
				cnet.addToVariableDomain(name, bitField);
			}
		}
		
		CspState state = cnet.generateCspState();
		
		System.out.println("Solve GAC...");
		
		CspState.max_generated_childs = 1000;
		Gac gac = new Gac(state);
		gac.solve();
		
		System.out.println("Starting search...");
		
		Astar<CspState> astar = new Astar<>(Astar.BEST_FIRST, (AstarAuxiliary<CspState>) (Node<CspState> node) -> {
			frame.updateState(node.state);
			try {
				Thread.sleep(gui_delay);
			} catch (Exception e) {
			}
		});
		
		CspState solution = astar.search(state);
		
		if (solution == null) {
			System.out.println("No solution found.");
			solution = astar.getSolutionNode().state;
		} else {
			System.out.println("Solution found.");
		}
		
		frame.updateState(solution);
		
		System.out.println();
		System.out.println("Nodes generated:\t" + astar.getNodesGenerated());
		System.out.println("Expanded nodes: \t" + astar.getPoppedNodes());
		System.out.println("Solution length:\t" + astar.getSolutionDepth());
		
		
	}
	
	
	public static String toStringBase(int i) {
		String ret = "";
		
		do {
			ret += (char)('A' + (i % 26));
			i /= 26;
		} while (i != 0);
		
		return ret;
	}
}
