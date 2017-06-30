package project.compiler;

import java.util.HashSet;
import java.util.Set;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

import project.antlr.GooseSpeakParser.ConcurrentDeclContext;
import project.antlr.GooseSpeakParser.ThreadDeclContext;

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
	/** All Parse Tree nodes associated with Lock and Shared Int declarations */ 
	private final Set<ConcurrentDeclContext> concurrentDeclarations = new HashSet<>();
	
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
	
	/**
	 * Get all parse tree nodes associated with thread declarations
	 * @return The set of parse tree nodes
	 */
	public Set<ThreadDeclContext> getThreadDeclarations() {
		return this.functionTable.getThreadDeclarations();
	}
	
	/**
	 * Associate a lock or a shared int declaration with a parse tree node
	 * @param node The parse tree node
	 */
	public void setConcurrentDeclaration(ConcurrentDeclContext node) {
		this.concurrentDeclarations.add(node);
	}
	
	/**
	 * Get all parse tree nodes associated with lock and shared int declarations
	 * @return The set of parse tree nodes
	 */
	public Set<ConcurrentDeclContext> getConcurrentDeclarations() {
		return concurrentDeclarations;
	}
	
}
