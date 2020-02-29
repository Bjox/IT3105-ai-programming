package module1.conf;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

/**
 *
 * @author Bjox
 */
public class ConfigReader {

	@Deprecated
	public static Config readFileOld(String path) throws FileNotFoundException, IOException {
		BufferedReader reader = new BufferedReader(new FileReader(path));
		Config conf = new Config();

		String file = "";
		String line = reader.readLine();

		while (line != null) {
			file += line;
			line = reader.readLine();
		}
		
		file = file.replaceAll(" ", "");

		StringTokenizer tokenizer = new StringTokenizer(file, "() ", false);

		String[] a = tokenizer.nextToken().split(",");
		conf.size = new Dimension(Integer.parseInt(a[0]), Integer.parseInt(a[1]));

		a = tokenizer.nextToken().split(",");
		conf.start = new Point(Integer.parseInt(a[0]), Integer.parseInt(a[1]));

		a = tokenizer.nextToken().split(",");
		conf.goal = new Point(Integer.parseInt(a[0]), Integer.parseInt(a[1]));

		while (tokenizer.hasMoreTokens()) {
			a = tokenizer.nextToken().split(",");
			
			conf.obstacles.add(
					new Rectangle(Integer.parseInt(a[0]), Integer.parseInt(a[1]), Integer.parseInt(a[2]), Integer.parseInt(a[3])));
		}

		return conf;
	}
	
	
	public static Config readFile(String path) throws FileNotFoundException, IOException {
		BufferedReader reader = new BufferedReader(new FileReader(path));
		Config conf = new Config();

		StringTokenizer st = new StringTokenizer(reader.readLine());

		conf.size = new Dimension(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()));

		st = new StringTokenizer(reader.readLine());
		
		conf.start = new Point(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()));
		conf.goal = new Point(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()));

		String line = reader.readLine();	
		while (line != null) {
			st = new StringTokenizer(line);
			
			conf.obstacles.add(new Rectangle(
					Integer.parseInt(st.nextToken()),	// x pos
					Integer.parseInt(st.nextToken()),	// y pos
					Integer.parseInt(st.nextToken()),	// width
					Integer.parseInt(st.nextToken())	// height
			));
			
			line = reader.readLine();
		}

		return conf;
	}

}
