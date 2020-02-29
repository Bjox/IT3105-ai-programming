package astargac.codechunk;

/**
 *
 * @author Bjox
 */
public class CodeChunkException extends Exception {

	/**
	 * Creates a new instance of <code>CodeChunkException</code> without detail message.
	 */
	public CodeChunkException() {
	}

	/**
	 * Constructs an instance of <code>CodeChunkException</code> with the specified detail message.
	 *
	 * @param msg the detail message.
	 */
	public CodeChunkException(String expr, Exception parent) {
		super("Invalid expression: \"" + expr + "\"\n" + parent.getClass().getSimpleName());
	}
}
