package astargac.codechunk;

/**
 *
 * @author Bjox
 */
public class ArgList {
	
	private final String[] argNames;
	private final int[] values;

	/**
	 * Creates a new, sorted Argument list.
	 * @param argNames 
	 */
	public ArgList(String[] argNames) {
		this(argNames, true);
	}
	
	/**
	 * Creates a new Argument list.
	 * @param argNames
	 * @param sort true: sort the arguments, false: do not sort.
	 */
	public ArgList(String[] argNames, boolean sort) {
		this.argNames = new String[argNames.length];
		this.values = new int[argNames.length];
		
		System.arraycopy(argNames, 0, this.argNames, 0, argNames.length);
		if (sort) java.util.Arrays.sort(this.argNames);
	}
	
	
	public void set(String argName, int val) {
		int i = getIndex(argName);
		if (i >= 0) values[i] = val;
	}
	
	
	public int get(String argName) {
		int i = getIndex(argName);
		if (i < 0) throw new RuntimeException("Argument \"" + argName + "\" not found.");
		return values[getIndex(argName)];
	}
	
	
	public int[] getArgs() {
		return values;
	}
	
	
	public String[] getArgNames() {
		String[] ret = new String[argNames.length];
		System.arraycopy(argNames, 0, ret, 0, argNames.length);
		return ret;
	}
	
	
	private int getIndex(String argName) {
		for (int i = 0; i < argNames.length; i++) {
			if (argNames[i].equals(argName)) return i;
		}
		
		return -1;
		//throw new RuntimeException("Argument \"" + argName + "\" not found.");
	}
}
