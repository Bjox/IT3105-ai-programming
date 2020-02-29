package astargac.codechunk;

import java.util.Objects;


/**
 *
 * @author Bjox
 */
public class CodeChunk {
	
	private final CodeChunkCore core;
	
	/** The number of arguments accepted by the <code>exec(int...)</code> method. */
	public final int numArgs;
	
	/** The expression string for this CodeChunk. */
	public final String expression;
	
	/** Argument list used in exec(). */
	private final ArgList argList;
	
	/**
	 * Create a new CodeChunk.
	 * @param expression
	 * @throws Exception 
	 */
	public CodeChunk(String expression) throws Exception {
		CodeChunkGenerator generator = new CodeChunkGenerator(expression);
		
		this.expression = expression;
		this.core = generator.core;
		this.numArgs = generator.numArgs;
		this.argList = generator.argList;
	}
	
	/**
	 * Sets a variable in the argument list.
	 * @param var
	 * @param val 
	 */
	public void set(String var, int val) {
		argList.set(var, val);
	}
	
	/**
	 * Gets a variable from the argument list.
	 * @param var
	 * @return 
	 */
	public int get(String var) {
		return argList.get(var);
	}
	
	/**
	 * Returns an array containing the names of the arguments accepted by this CodeChunk.
	 * @return 
	 */
	public String[] getArgNames() {
		return argList.getArgNames();
	}
	
	/**
	 * Executes the statement with given arguments.
	 * @param args exec arguments
	 * @return 
	 */
	public boolean exec(int... args) {
		if (args.length != numArgs) {
			throw new IllegalArgumentException("Wrong number of arguments passed for expression \"" +
					expression + "\". Expected " + numArgs + ", got " + args.length + ".");
		}
			
		return core.exec(args);
	}
	
	/**
	 * Executes the statement using arguments from the argument list.
	 * @return 
	 */
	public boolean exec() {
		return exec(argList.getArgs());
	}

	
	@Override
	public int hashCode() {
		int hash = 7;
		hash = 13 * hash + Objects.hashCode(this.expression);
		return hash;
	}

	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final CodeChunk other = (CodeChunk) obj;
		if (!Objects.equals(this.expression, other.expression)) {
			return false;
		}
		return true;
	}

	
	
	
	
	
	
}
