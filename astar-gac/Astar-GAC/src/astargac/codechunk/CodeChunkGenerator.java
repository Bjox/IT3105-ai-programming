package astargac.codechunk;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.StringTokenizer;
import org.abstractmeta.toolbox.compilation.compiler.JavaSourceCompiler;
import org.abstractmeta.toolbox.compilation.compiler.impl.JavaSourceCompilerImpl;


/**
 *
 * @author Bjox
 */
class CodeChunkGenerator {
	
	private static final String pckgName = CodeChunkCore.class.getPackage().getName();
	private static final String wrapperClassName = "CodeChunkCoreWrapper";
	
	public final int numArgs;
	public final CodeChunkCore core;
	public final String expr;
	private final HashSet<String> varSet; // NB! NOT SORTED
	public final ArgList argList;
	
	public CodeChunkGenerator(String expr) throws Exception {
		this.expr = expr;
		
		final String wrapperTemplate =
				"package %pckg%;public class %wcname% implements CodeChunkCore{public boolean exec(int... args){%decl%return %expr%;}}";
		
		String e = expr.replaceAll(" ", "");
		
		StringTokenizer tokenizer = new StringTokenizer(e, " +-*/%=<>()!&|^~?:0123456789", false);
		varSet = new HashSet<>();
		
		while (tokenizer.hasMoreElements()) {
			String varName = tokenizer.nextToken();
			varSet.add(varName);
		}
		
		String[] varNameArr = varSet.toArray(new String[0]);
		java.util.Arrays.sort(varNameArr);
		
		argList = new ArgList(varNameArr, false);
		
		this.numArgs = varSet.size();
		String declString = "";
		
		if (numArgs > 0) {
			declString = "int ";
			
			int i = 0;
			for (String v : varNameArr) {
				declString += v + "=args[" + i++ + "],";
			}
			
			declString = declString.substring(0, declString.length() - 1) + ";";
		}
		
		
		String wrapper = wrapperTemplate;
		wrapper = wrapper.replaceAll("%expr%", e);
		wrapper = wrapper.replaceAll("%pckg%", pckgName);
		wrapper = wrapper.replaceAll("%wcname%", wrapperClassName);
		wrapper = wrapper.replaceAll("%decl%", declString);
		
		//System.out.println(wrapper);
		
		JavaSourceCompiler compiler = new JavaSourceCompilerImpl();
		JavaSourceCompiler.CompilationUnit compilationUnit = compiler.createCompilationUnit();
		
		compilationUnit.addJavaSource(pckgName + "." + wrapperClassName, wrapper);
		
		try {
			ClassLoader classLoader = compiler.compile(compilationUnit);
			Class c = classLoader.loadClass(pckgName + "." + wrapperClassName);
			this.core = CodeChunkCore.class.cast(c.newInstance());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
			throw new CodeChunkException(expr, ex);
			//throw ex;
		}
	}
	
	public boolean checkConstraint(int X, int Y) {
		return X > Y;
	}
	
}
