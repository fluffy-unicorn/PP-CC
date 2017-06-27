package project.compiler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Helper class for storing information of all the functions declared
 * in the source code
 */
public class FunctionTable {
	/** Set of functions that are declared */
	private Set<Function> functions = new HashSet<>();
	/** Whether the source code has a void main function or not */
	private boolean hasVoidMain = false;
	/** Singleton instance for the void main function */
	public static final Function voidMain = new Function(Type.Void, "main");
	
	/**
	 * Adds a function to the function table
	 * @param f The function to add
	 * @throws CompilerException When the function cannot be added to the function
	 * table because a function with the same signature already exists.
	 */
	public void addFunction(Function f) throws CompilerException {
		if(functions.add(f)) {
			if(voidMain.equals(f)) {
				hasVoidMain = true;
			}
		} else 
			throw new CompilerException("Cannot add function to function table");
	}
	
	/**
	 * Indicates whether the source code has a void main function or not
	 */
	public boolean hasVoidMain() {
		return hasVoidMain;
	}
	
	/**
	 * Checks whether the function table contains a function
	 * @param f The function to check
	 * @return True when one of the functions declared has the
	 * same hash code as the function f
	 */
	public boolean contains(Function f) {
		return functions.contains(f);
	}
	
	/**
	 * Get the list of functions with the specified name
	 * @param name The name used in the source code
	 * @return The list of functions, may be empty when no 
	 * function is defined with the name
	 */
	public List<Function> get(String name) {
		List<Function> result = new ArrayList<>();
		for (Function f : functions) {
			if(f.getName().equals(name))
				result.add(f);
		}
		return result;
	}
	
	/**
	 * Textual string representation for the function table.
	 */
	@Override
	public String toString() {
		String result = "";
		for(Function f : functions) {
			result += f.toString() + "\n";
		}
		return result;
	}

	public Function get(String text, Type... arguments) {
		for(Function f : get(text)) {
			if(Arrays.equals(f.getArguments(), arguments))
				return f;
		}
		return null;
	}
	
	
}
