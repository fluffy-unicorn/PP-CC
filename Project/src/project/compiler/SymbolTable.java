package project.compiler;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.Iterator;

/**
 * SymbolTable for GooseSpeak
 */
public class SymbolTable {

	/** The stack with scopes */
	private Deque<Scope> scopeStack = new ArrayDeque<>();
	
	private int size = 0;
	/**
	 * Open a new scope
	 */
	public void openScope() {
		Scope topScope = scopeStack.peek();
		if(topScope != null)
			size += topScope.getSize();
		scopeStack.push(new Scope());
	}

	/**
	 * Close the last opened scope
	 */
	public void closeScope() {
		scopeStack.pop();
		Scope topScope = scopeStack.peek();
		if(topScope != null)
			size -= topScope.getSize();
	}

	/**
	 * Add a variable to the last opened scope
	 * 
	 * @param id
	 *            The id used in the source code
	 * @param t
	 *            The type of the variable
	 * @param initialized
	 *            Whether the variable has a value assigned to it or is only
	 *            declared
	 * @return True when the current scope does not contain a variable with the
	 *         same id
	 */
	public boolean add(String id, Type t, boolean initialized) {
		Scope head = scopeStack.peek();
		if (head != null) {
			if (head.contains(id)) {
				return false;
			} else {
				return head.addVariable(id, t, initialized);
			}
		}
		return false;
	}

	/**
	 * Indicates that a variable is declared in the symbol table
	 * 
	 * @param id
	 *            The id used in the source code
	 * @return True when the variable is declared in one of the scopes
	 */
	public boolean contains(String id) {
		Iterator<Scope> it = scopeStack.iterator();
		while (it.hasNext()) {
			if (it.next().contains(id)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Return the {@link Variable} associated with a id
	 * 
	 * @param id
	 *            The id used in the source code
	 * @return The {@link Variable} of the top most (most recently used) scope.
	 *         When multiple variables are defined within this symbol table only
	 *         the most recent one will be returned. Returns null when no
	 *         variable can be found.
	 */
	public Variable getVariable(String id) {
		Iterator<Scope> it = scopeStack.iterator();
		while (it.hasNext()) {
			Scope next = it.next();
			if (next.contains(id)) {
				return next.getVariable(id);
			}
		}
		return null;

	}
	
	/**
	 * Returns the top most (most recently used) scope.
	 */
	public Scope getHead() {
		return scopeStack.peek();
	}
	
	/**
	 * Get the current size of the symbol table
	 */
	public int getSize() {
		return size;
	}
	
	public String toString() {
		return Arrays.toString(scopeStack.toArray());
	}
}
