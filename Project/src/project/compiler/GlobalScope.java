package project.compiler;

/** Class combining the information of a single scope level. */
public class GlobalScope extends Scope {
	private int globalVariableSize;
	private int globalMemorySize;

	public GlobalScope() {
		super();
		globalVariableSize = 0;
		globalMemorySize = Compiler.MAX_THREADS;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean addVariable(String id, Type t, boolean initialized) {
		boolean result = !this.variables.containsKey(id);
		if (result) {
			Variable var = new Variable();
			if (t.equals(Type.Lock)
					|| (!(t.getKind() == TypeKind.IntPtr) 
							&& t.equals(Type.Int) && ((Type.Int) t).isShared())) {
				var.setOffset(globalMemorySize);
				globalMemorySize++;
			} else {
				var.setOffset(globalVariableSize);
				globalVariableSize++;
			}
			var.setType(t);
			if (initialized)
				var.setInitalized();
			this.variables.put(id, var);
		}
		return result;
	}
}