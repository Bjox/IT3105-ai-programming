package astargac.codechunk;

/**
 *
 * @author Bjox
 */
public interface CodeChunkCore {
	
	/**
	 * Executes the statement with given arguments.
	 * @param args exec arguments
	 * @return 
	 */
	public boolean exec(int... args);
	
}
