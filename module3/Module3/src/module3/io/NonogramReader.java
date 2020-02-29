package module3.io;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import module3.nonogram.Nonogram;

/**
 *
 * @author Bjox
 */
public class NonogramReader {
	
	private final Nonogram nono;
	
	public NonogramReader(String filename) throws FileNotFoundException, IOException {
		System.out.println("Reading nonogram file \"" + filename + "\"...");
		
		BufferedReader br = new BufferedReader(new FileReader(filename));
		
		StringTokenizer st = new StringTokenizer(br.readLine());
		
		final int X = Integer.parseInt(st.nextToken());
		final int Y = Integer.parseInt(st.nextToken());
		
		nono = new Nonogram(X, Y);
		
		System.out.println("Board width:\t" + X);
		System.out.println("Board height:\t" + Y);
		
		System.out.print("Row spec:\t");
		
		for (int i = 0; i < nono.ROWS; i++) {
			st = new StringTokenizer(br.readLine());
			ArrayList<Integer> buff = new ArrayList<>();
			
			while (st.hasMoreTokens()) {
				buff.add(Integer.parseInt(st.nextToken()));
			}
			
			System.out.print(buff.toString());
			
			int[] spec = new int[buff.size()];
			for (int j = 0; j < buff.size(); j++) spec[j] = buff.get(j);
			nono.addRowSpec(spec);
		}
		
		System.out.print("\nColumn spec:\t");
		
		for (int i = 0; i < nono.COLUMNS; i++) {
			st = new StringTokenizer(br.readLine());
			ArrayList<Integer> buff = new ArrayList<>();
			
			while (st.hasMoreTokens()) {
				buff.add(Integer.parseInt(st.nextToken()));
			}
			
			System.out.print(buff.toString());
			
			int[] spec = new int[buff.size()];
			for (int j = 0; j < buff.size(); j++) spec[j] = buff.get(j);
			nono.addColumnSpec(spec);
		}
		
		System.out.println();
	}
	
	
	public Nonogram getNonogram() {
		return nono;
	}
	
}
