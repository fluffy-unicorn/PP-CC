package project.compiler;

import java.util.Arrays;

/**
 * Wrapper class for a GooseSpeak function
 */
public class Function {
	/** The name defined in the source code */
	private final String name;
	/** The return type of the function */
	private final Type returnType;
	/** The list of argument types of this function, may be empty */
	private final Type[] args;
	/** The symbol table associated with this function */
	private final SymbolTable st = new SymbolTable();
	/** Indicates whether the function has a return statement */ // TODO
	private boolean hasReturn = false;
	/** Reference to the absolute program index, used for code generation */
	private final IntegerReference programLocation = new IntegerReference();

	/**
	 * Create a new function
	 * 
	 * @param returnType
	 *            The return type
	 * @param name
	 *            The name of the function
	 * @param args
	 *            A (possibly empty) list of argument types
	 */
	public Function(Type returnType, String name, Type... args) {
		this.returnType = returnType;
		this.name = name;
		this.args = args;
	}

	/**
	 * Calculates the hash code of the function. The return type is not used in
	 * the calculation because a function is determined by its name and argument
	 * types only.
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(args);
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	/**
	 * Equality test for two functions. Does compare the return types because a
	 * function is determined by its name and argument types only.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Function other = (Function) obj;
		if (!Arrays.equals(args, other.getArguments()))
			return false;
		if (name == null) {
			if (other.getName() != null)
				return false;
		} else if (!name.equals(other.getName()))
			return false;
		return true;
	}

	/**
	 * Textual string representation of the function
	 */
	@Override
	public String toString() {
		if (returnType.equals(Type.Thread))
			return "thread " + name + toString(args);
		else
			return "function " + returnType.toString() + " " + name + toString(args);
	}

	/**
	 * Getter for the argument type array
	 */
	public Type[] getArguments() {
		return this.args;
	}

	/**
	 * Getter for the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Getter for the return type
	 */
	public Type getReturnType() {
		return this.returnType;
	}

	/**
	 * Set the value of the {@link IntegerReference} program location
	 * 
	 * @param i
	 *            Value to set
	 */
	public void setLocation(int i) {
		this.programLocation.setValue(i);
	}

	/**
	 * Getter for the program location
	 */
	public IntegerReference getLocation() {
		return this.programLocation;
	}

	/**
	 * Private helper function for showing a textual representation of the
	 * argument types
	 * 
	 * @param args
	 *            The argument types
	 */
	private String toString(Type[] args) {
		StringBuilder sb = new StringBuilder();
		sb.append('(');
		if (args.length > 0) {
			int i = 0;
			for (; i < args.length - 1; i++) {
				sb.append(args[i].toString());
				sb.append(',');
			}
			sb.append(args[i].toString());
		}
		sb.append(')');
		return sb.toString();
	}

	/**
	 * Getter for the symbol table
	 */
	public SymbolTable getSymbolTable() {
		return st;
	}

	/**
	 * Set the value of hasReturn to true
	 */
	public void setReturn() {
		hasReturn = true;
	}

	/**
	 * Indicate whether a function has a return statement
	 */
	public boolean hasReturn() {
		return hasReturn;
	}

	/**
	 * Indicate whether a function is a void function
	 */
	public boolean isVoid() {
		return this.returnType.getKind() == TypeKind.Void;
	}
	
	/**
	 * Indicate whether the function is the void main function
	 */
	public boolean isVoidMain() {
		return this.name.equals("main") && this.args.length == 0;
	}
}
