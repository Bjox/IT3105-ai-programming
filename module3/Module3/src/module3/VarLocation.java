package module3;

/**
 *
 * @author Bjox
 */
public class VarLocation {
	
	public enum VarType { ROW, COLUMN; }
	
	public final VarType type;
	public final int index;

	public VarLocation(VarType type, int index) {
		this.type = type;
		this.index = index;
	}
	
	
	
}
