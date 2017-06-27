package project.compiler;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

/**
 * Class for storing the result of the checker phase.
 */
public class Result {
	/** Mapping of parse tree nodes to variables */
	private final ParseTreeProperty<Variable> variables = new ParseTreeProperty<>();
	/** Mapping of parse tree nodes to types of expressions */
	private final ParseTreeProperty<Type> types = new ParseTreeProperty<>();
	/** Mapping of parse tree node to functions */
	private final ParseTreeProperty<Function> functions = new ParseTreeProperty<>();
	/** The complete function table */
	private final FunctionTable functionTable;

	public Result(FunctionTable functionTable) {
		this.functionTable = functionTable;
	}
	/**
	 * Associates a {@link Variable} to the node.
	 * @param node
	 * @param var
	 */
	public void setVariable(ParseTree node, Variable var) {
		variables.put(node, var);
	}
	
	/**
	 * Returns the {@link Variable} associated with the node. 
	 * @param node
	 * @return The variable
	 */
	public Variable getVariable(ParseTree node) {
		return variables.get(node);
	}

	/**
	 * Associates a {@link Type} to the node.
	 * @param node
	 * @param var
	 */
	public void setType(ParseTree node, Type type) {
		types.put(node, type);
	}
	
	/**
	 * Returns the {@link Type} associated with the node. 
	 * @param node
	 * @return The type
	 */
	public Type getType(ParseTree node) {
		return types.get(node);
	}
	
	/**
	 * Associates a {@link Function} to the node.
	 * @param node
	 * @param var
	 */
	public void setFunction(ParseTree node, Function func) {
		functions.put(node, func);
	}
	
	/**
	 * Returns the {@link Function} associated with the node. 
	 * @param node
	 * @return The function
	 */
	public Function getFunction(ParseTree node) {
		return functions.get(node);
	}
	
	/**
	 * Returns the {@link FunctionTable}.
	 * @param node
	 * @return The function table
	 */
	public FunctionTable getFunctionTable() {
		return functionTable;
	}
}
