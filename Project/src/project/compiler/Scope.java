package project.compiler;

import java.util.LinkedHashMap;
import java.util.Map;

/** Class combining the information of a single scope level. */
public class Scope {
	protected final Map<String, Variable> variables;

	/** Constructs a fresh, initially empty scope. */
	public Scope() {
		this.variables = new LinkedHashMap<>();
	}

	/**
	 * Tests if a given identifier is declared in this scope.
	 * 
	 * @param id
	 *            The id in the source code.
	 */
	public boolean contains(String id) {
		return this.variables.containsKey(id);
	}

	/**
	 * Adds a new variable to the scope.
	 * 
	 * @param id
	 *            The id in the source code.
	 * @param t
	 *            The type of the variable.
	 * @param initialized
	 *            Indication whether the variable has been assigned a value to
	 *            or not.
	 * @return {@code false} when the scope does contain a variable with the
	 *         same name as id. It does not add this variable to the scope.
	 *         Otherwise the value is added the return value is {@code true}.
	 */
	public boolean addVariable(String id, Type t, boolean initialized) {
		boolean result = !this.variables.containsKey(id);
		if (result) {
			Variable var = new Variable();
			var.setOffset(this.getSize());
			var.setType(t);
			if (initialized)
				var.setInitalized();
			this.variables.put(id, var);
		}
		return result;
	}

	/**
	 * Gets the {@link Variable} associated with the id.
	 * @param id The id in the source code.
	 * @return The variable.
	 */
	public Variable getVariable(String id) {
		return variables.get(id);
	}
	
	/**
	 * Returns a map of all the variables defined in the scope
	 */
	public Map<String, Variable> getVariables() {
		return variables;
	}
	
	/**
	 * Returns the size of the scope
	 * @return The number of declared variables
	 */
	public int getSize() {
		return this.variables.size();
	}
	
	public String toString() {
		return this.variables.toString();
	}
}