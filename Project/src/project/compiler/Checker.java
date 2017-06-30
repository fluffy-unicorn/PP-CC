package project.compiler;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;

import project.antlr.GooseSpeakBaseListener;
import project.antlr.GooseSpeakParser.AcquireStatContext;
import project.antlr.GooseSpeakParser.ArgumentDeclContext;
import project.antlr.GooseSpeakParser.ArgumentsContext;
import project.antlr.GooseSpeakParser.ArrayContext;
import project.antlr.GooseSpeakParser.ArrayElemAssStatContext;
import project.antlr.GooseSpeakParser.ArrayElemExprContext;
import project.antlr.GooseSpeakParser.ArrayExprContext;
import project.antlr.GooseSpeakParser.ArrayTypeContext;
import project.antlr.GooseSpeakParser.AssignStatContext;
import project.antlr.GooseSpeakParser.BasicTypeContext;
import project.antlr.GooseSpeakParser.BitExprContext;
import project.antlr.GooseSpeakParser.BlockStatContext;
import project.antlr.GooseSpeakParser.BoolExprContext;
import project.antlr.GooseSpeakParser.BraceExprContext;
import project.antlr.GooseSpeakParser.BreakStatContext;
import project.antlr.GooseSpeakParser.CharExprContext;
import project.antlr.GooseSpeakParser.CompExprContext;
import project.antlr.GooseSpeakParser.ContinueStatContext;
import project.antlr.GooseSpeakParser.DeclAssStatContext;
import project.antlr.GooseSpeakParser.DeclStatContext;
import project.antlr.GooseSpeakParser.ExprContext;
import project.antlr.GooseSpeakParser.FalseExprContext;
import project.antlr.GooseSpeakParser.ForkStatContext;
import project.antlr.GooseSpeakParser.FuncContext;
import project.antlr.GooseSpeakParser.FunctionCallContext;
import project.antlr.GooseSpeakParser.FunctionExprContext;
import project.antlr.GooseSpeakParser.GlobalDeclAssStatContext;
import project.antlr.GooseSpeakParser.GlobalDeclStatContext;
import project.antlr.GooseSpeakParser.IdExprContext;
import project.antlr.GooseSpeakParser.IfStatContext;
import project.antlr.GooseSpeakParser.JoinStatContext;
import project.antlr.GooseSpeakParser.LockDeclContext;
import project.antlr.GooseSpeakParser.MultExprContext;
import project.antlr.GooseSpeakParser.NumExprContext;
import project.antlr.GooseSpeakParser.PlusExprContext;
import project.antlr.GooseSpeakParser.PointerTypeContext;
import project.antlr.GooseSpeakParser.PowerExprContext;
import project.antlr.GooseSpeakParser.PrfExprContext;
import project.antlr.GooseSpeakParser.PrintStatContext;
import project.antlr.GooseSpeakParser.ProcedureCallContext;
import project.antlr.GooseSpeakParser.ProcedureExprContext;
import project.antlr.GooseSpeakParser.ProgramContext;
import project.antlr.GooseSpeakParser.ReadStatContext;
import project.antlr.GooseSpeakParser.ReleaseStatContext;
import project.antlr.GooseSpeakParser.ReturnExprContext;
import project.antlr.GooseSpeakParser.ReturnVoidContext;
import project.antlr.GooseSpeakParser.SharedDeclContext;
import project.antlr.GooseSpeakParser.StringExprContext;
import project.antlr.GooseSpeakParser.ThreadDeclContext;
import project.antlr.GooseSpeakParser.TrueExprContext;
import project.antlr.GooseSpeakParser.TypeContext;
import project.antlr.GooseSpeakParser.WhileStatContext;

public class Checker extends GooseSpeakBaseListener {
	private SymbolTable symbolTable;
	private Result result;
	private FunctionTable functionTable;
	private List<String> errors;
	private GlobalScope globalScope;
	private ParseTreeProperty<Integer> stringSize;
	private int lastThreadId;

	public Result check(List<String> errors, FunctionTable functionTable, ParseTree tree) throws ParseException {
		this.result = new Result(functionTable);
		this.functionTable = result.getFunctionTable();
		this.errors = errors;
		this.globalScope = new GlobalScope();
		this.stringSize = new ParseTreeProperty<>();
		this.lastThreadId = 0;
		new ParseTreeWalker().walk(this, tree);
		return this.result;
	}

	@Override
	public void exitProgram(ProgramContext ctx) {
		if (!this.functionTable.hasVoidMain())
			addError(ctx, "Program has no void main");
		Map<String, Variable> globalVars = globalScope.getVariables();
		for (String s : globalVars.keySet()) {
			Variable v = globalVars.get(s);
			// if(!v.isInitalized())
			// addError(ctx, s + " is not initialized");
		}

	}

	@Override
	public void exitGlobalDeclStat(GlobalDeclStatContext ctx) {
		Type type = parseType(ctx.type());
		String id = ctx.ID().getText();
		if (!globalScope.addVariable(id, type, false))
			addError(ctx, id + " is already declared at the global scope.");
		else {
			Variable v = globalScope.getVariable(id);
			v.setGlobal();
			setVariable(ctx.ID(), v);
		}
	}

	@Override
	public void exitGlobalDeclAssStat(GlobalDeclAssStatContext ctx) {
		Type type = parseType(ctx.type());
		String id = ctx.ID().getText();

		if (!globalScope.addVariable(id, type, true))
			addError(ctx, id + " is already declared at the global scope.");
		else if (!checkType(type, getType(ctx.expr())))
			addError(ctx, id + " is of type " + type + " but its right hand side is not.");
		else {
			Variable v = globalScope.getVariable(id);
			if (checkType(type, Type.String))
				v.setType(new Type.Array(getSize(ctx.expr()), Type.Char));
			v.setGlobal();
			setVariable(ctx.ID(), v);
		}
	}

	@Override
	public void enterFunc(FuncContext ctx) {
		ArgumentDeclContext argumentCtx = ctx.argumentDecl();
		List<Type> types = new ArrayList<>();
		List<TerminalNode> ids = new ArrayList<>();
		if (argumentCtx != null) {
			ids = argumentCtx.ID();
			for (TypeContext type : argumentCtx.type())
				types.add(parseType(type));
		}
		Function f = functionTable.get(ctx.ID().getText(), types.toArray(new Type[0]));
		setFunction(ctx, f);
		symbolTable = f.getSymbolTable();

		openScope();
		for (int i = 0; i < ids.size(); i++) {
			TerminalNode ID = ids.get(i);
			Type type = types.get(i);
			String id = ID.getText();
			if (!symbolTable.add(id, type, true))
				addError(ctx, id + " is already decleared as parameter.");
		}

	}

	@Override
	public void exitFunc(FuncContext ctx) {
		if (!getFunction(ctx).hasReturn() && !getFunction(ctx).isVoid())
			addError(ctx, ctx.ID().getText() + " has no return statement");
		closeScope(ctx);
	}

	@Override
	public void exitLockDecl(LockDeclContext ctx) {
		String id = ctx.ID().getText();
		if (!globalScope.addVariable(id, Type.Lock, true))
			addError(ctx, id + " is already declared at the global scope.");
		else {
			Variable v = globalScope.getVariable(id);
			v.setGlobal();
			setVariable(ctx.ID(), v);
			result.setConcurrentDeclaration(ctx);
		}
	}

	@Override
	public void exitSharedDecl(SharedDeclContext ctx) {
		String id = ctx.ID().getText();
		Type type = getType(ctx.expr());
		if (!(type.getKind() == TypeKind.Int))
			addError(ctx, "Shared integer can only be of type integer.");
		if (!globalScope.addVariable(id, Type.SharedInt, true))
			addError(ctx, id + " is already declared at the global scope.");
		else {
			Variable v = globalScope.getVariable(id);
			v.setGlobal();
			setVariable(ctx.ID(), v);
			result.setConcurrentDeclaration(ctx);
		}
	}

	@Override
	public void enterThreadDecl(ThreadDeclContext ctx) {
		Function f = functionTable.get(ctx.ID().getText(), new Type[0]);
		setFunction(ctx, f);
		symbolTable = f.getSymbolTable();
		openScope();
	}

	@Override
	public void exitThreadDecl(ThreadDeclContext ctx) {
		closeScope(ctx);
	}

	@Override
	public void enterBlockStat(BlockStatContext ctx) {
		openScope();
	}

	@Override
	public void exitBlockStat(BlockStatContext ctx) {
		closeScope(ctx);
	}

	@Override
	public void exitPrintStat(PrintStatContext ctx) {
		setType(ctx, getType(ctx.expr()));
	}

	@Override
	public void exitReadStat(ReadStatContext ctx) {
		String id = ctx.ID().getText();
		Variable var = getDeclaredVariable(id);
		if (var == null) {
			addError(ctx, id + " is not declared in this scope.");
		}
		// TODO strings/arrays?
		Type[] acceptedTypes = { Type.Int, Type.Bool, Type.Char };

		boolean isAccepted = false;
		for (Type t : acceptedTypes) {
			if (checkType(var.getType(), t)) {
				isAccepted = true;
				break;
			}
		}
		if (isAccepted)
			setVariable(ctx, var);
		else
			addError(ctx, var.getType() + " is not accepted as a type for read");
	}

	@Override
	public void exitDeclStat(DeclStatContext ctx) {
		Type type = parseType(ctx.type());
		String id = ctx.ID().getText();
		if (!symbolTable.add(id, type, false))
			addError(ctx, id + " is already decleared in this scope.");
	}

	@Override
	public void exitDeclAssStat(DeclAssStatContext ctx) {
		Type type = parseType(ctx.type());
		String id = ctx.ID().getText();
		if (!symbolTable.add(id, type, true))
			addError(ctx, id + " is already declared in this scope.");
		else if (!checkType(type, getType(ctx.expr())))
			addError(ctx, id + " is of type " + type + " but its right hand side is not.");
		else {
			Variable v = getDeclaredVariable(id);
			if (checkType(type, Type.String))
				v.setType(new Type.Array(getSize(ctx.expr()), Type.Char));
			setVariable(ctx.ID(), v);
		}
	}

	@Override
	public void exitAssignStat(AssignStatContext ctx) {
		String id = ctx.ID().getText();
		Variable var = getDeclaredVariable(id);
		if (var == null)
			addError(ctx, id + " is not declared in this scope.");
		else if (!checkType(var.getType(), getType(ctx.expr())))
			addError(ctx, id + " is of type " + var.getType() + " but its right hand side is of type "
					+ getType(ctx.expr()) + ".");
		else if (checkType(var.getType(), Type.String))
			var.setType(new Type.Array(getSize(ctx.expr()), Type.Char));
		var.setInitalized();
		setVariable(ctx.ID(), var);
	}

	@Override
	public void exitArrayElemAssStat(ArrayElemAssStatContext ctx) {
		String id = ctx.ID().getText();
		Variable var = getDeclaredVariable(id);
		if (var == null)
			addError(ctx, id + " is not declared in this scope.");
		else if (!(var.getType() instanceof Type.Array))
			addError(ctx, id + " is not an array");
		else {
			Type type = getDeclaredVariable(ctx.ID().getText()).getType();
			for (int i = 0; i < ctx.array().size(); i++)
				type = ((Type.Array) type).getElemType();
			if (!checkType(type, getType(ctx.expr())))
				addError(ctx, id + " is of type " + var.getType() + ", however its right hand side is of type "
						+ getType(ctx.expr()));
		}
		setVariable(ctx.ID(), var);
	}

	@Override
	public void enterIfStat(IfStatContext ctx) {
		openScope();
	}

	@Override
	public void exitIfStat(IfStatContext ctx) {
		if (!checkType(Type.Bool, getType(ctx.expr())))
			addError(ctx, "The expression in the if-statement is not of type bool");
		closeScope(ctx);
	}

	@Override
	public void enterWhileStat(WhileStatContext ctx) {
		openScope();
	}

	@Override
	public void exitWhileStat(WhileStatContext ctx) {
		if (!checkType(Type.Bool, getType(ctx.expr())))
			addError(ctx, "The expression in the while-statement is not of type bool");
		closeScope(ctx);
	}

	@Override
	public void exitBreakStat(BreakStatContext ctx) {
		RuleContext parent = getParentWhileCtx(ctx);
		if (parent == null)
			addError(ctx, "A break statement must reside in a while statement");
	}

	@Override
	public void exitContinueStat(ContinueStatContext ctx) {
		RuleContext parent = getParentWhileCtx(ctx);
		if (parent == null)
			addError(ctx, "A continue statement must reside in a while statement");
	}

	@Override
	public void exitFunctionCall(FunctionCallContext ctx) {
		checkFunction(ctx, ctx.ID().getText(), false, ctx.arguments());
	}

	@Override
	public void exitProcedureCall(ProcedureCallContext ctx) {
		checkFunction(ctx, ctx.ID().getText(), false);
	}

	@Override
	public void exitReturnExpr(ReturnExprContext ctx) {
		Type type = getType(ctx.expr());
		if (type == null)
			return; // An earlier error occurred
		RuleContext parent = getParentFunctionCtx(ctx);
		if (parent == null) {
			addError(ctx, "Return statements can only occur inside functions.");
			return;
		}
		Function func = getFunction(parent);
		if (type.equals(Type.Void))
			addError(ctx, "Cannot return a call to a procedure");
		else if (func.getReturnType().equals(Type.Void))
			addError(ctx, "Cannot return a value because " + func.getName() + " is a procedure.");
		else if (!checkType(func.getReturnType(), type))
			addError(ctx, "Return value of function is " + func.getReturnType() + ", however the expression is of type "
					+ type);
		else
			func.setReturn();
	}

	@Override
	public void exitReturnVoid(ReturnVoidContext ctx) {
		Type type = Type.Void;
		RuleContext parent = getParentFunctionCtx(ctx);
		if (parent == null) {
			addError(ctx, "Return statements can only occur inside functions.");
			return;
		}
		Function func = getFunction(parent);
		setFunction(ctx, func);
		if (!checkType(func.getReturnType(), type) && !checkType(func.getReturnType(), Type.Thread))
			addError(ctx, "Function " + getFunction(parent).getName() + " expects a return value.");
		else
			func.setReturn();
	}

	@Override
	public void exitForkStat(ForkStatContext ctx) {
		String id = ctx.ID(0).getText();
		if (!symbolTable.add(id, Type.Thread, true))
			addError(ctx, id + " is already declared in this scope.");
		else {
			Variable v = getDeclaredVariable(id);
			// TODO proper exception
			assert lastThreadId < Compiler.MAX_THREADS;
			v.setThreadId(lastThreadId);
			lastThreadId++;
			setVariable(ctx.ID(0), v);
			checkFunction(ctx, ctx.ID(1).getText(), false);
		}

	}

	@Override
	public void exitJoinStat(JoinStatContext ctx) {
		String id = ctx.ID().getText();
		Variable var = getDeclaredVariable(id);
		if (!checkType(var.getType(), Type.Thread))
			addError(ctx, id + " is not a thread variable.");
		setVariable(ctx, var);
	}

	@Override
	public void exitAcquireStat(AcquireStatContext ctx) {
		String id = ctx.ID().getText();
		Variable var = getDeclaredVariable(id);
		if (!checkType(var.getType(), Type.Lock))
			addError(ctx, "Acquire is only valid on a lock");
		setVariable(ctx, var);
	}

	@Override
	public void exitReleaseStat(ReleaseStatContext ctx) {
		String id = ctx.ID().getText();
		Variable var = getDeclaredVariable(id);
		if (!checkType(var.getType(), Type.Lock))
			addError(ctx, "Release is only valid on a lock");
		setVariable(ctx, var);
	}

	@Override
	public void exitPrfExpr(PrfExprContext ctx) {
		Type type = getType(ctx.expr());
		if (ctx.prfOp().LEN() != null) {
			if(type.getKind() != TypeKind.Array && type.getKind() != TypeKind.String)
				addError(ctx, "expected an array type, but got " + type);
			type = Type.Int;
		}
		else if (ctx.prfOp().STR() != null) {
			if(!checkType(type, Type.Int))
				addError(ctx, "expected an integer type, but got " + type);
			type = Type.String;
		}
		else if (ctx.prfOp().LOG_NOT() != null && !checkType(type, Type.Bool))
			addError(ctx, "Boolean operator on non-boolean expression");
		else if (ctx.prfOp().BW_NOT() != null && !checkType(type, Type.Int))
			addError(ctx, "Bitwise operator on non-integer expression");
		setType(ctx, type);
	}

	@Override
	public void exitPowerExpr(PowerExprContext ctx) {
		Type type0 = getType(ctx.expr(0));
		Type type1 = getType(ctx.expr(1));
		String errorMessage = "Raising to a power is only possible for two integers";
		if (!checkType(type0, type1))
			addError(ctx, errorMessage);
		else if (!checkType(type0, Type.Int))
			addError(ctx, errorMessage);
		setType(ctx, type0);

	}

	@Override
	public void exitMultExpr(MultExprContext ctx) {
		Type type0 = getType(ctx.expr(0));
		Type type1 = getType(ctx.expr(1));
		String errorMessage = "Multiplication and division is only possible for two integers";
		if (!checkType(type0, type1))
			addError(ctx, errorMessage);
		else if (!checkType(type0, Type.Int))
			addError(ctx, errorMessage);
		setType(ctx, type0);
	}

	@Override
	public void exitPlusExpr(PlusExprContext ctx) {
		Type type0 = getType(ctx.expr(0));
		Type type1 = getType(ctx.expr(1));
		String errorMessagePlus = "Addition/concatenation is only possible for two integers or strings";
		String errorMessageMinus = "Subtraction is only possible for two integers";
		if (ctx.plusOp().PLUS() != null) {
			boolean bothString = checkType(type0, Type.String) && checkType(type1, Type.String);
			boolean bothInt = checkType(type0, type1) && checkType(type0, Type.Int);
			if (!bothString && !bothInt)
				addError(ctx, errorMessagePlus);
		} else {
			if (!checkType(type0, type1))
				addError(ctx, errorMessageMinus);
			else if (!checkType(type0, Type.Int))
				addError(ctx, errorMessageMinus);
		}
		setType(ctx, type0);
	}

	@Override
	public void exitCompExpr(CompExprContext ctx) {
		Type type0 = getType(ctx.expr(0));
		Type type1 = getType(ctx.expr(1));
		String errorMessage = "Comparison is only possible for two identical types";
		if (!checkType(type0, type1))
			addError(ctx, errorMessage);
		setType(ctx, Type.Bool);
	}

	@Override
	public void exitBitExpr(BitExprContext ctx) {
		Type type0 = getType(ctx.expr(0));
		Type type1 = getType(ctx.expr(1));
		String errorMessage = "Bitwise operators are only possible for two integer types";
		if (!checkType(type0, type1) || !checkType(type0, Type.Int))
			addError(ctx, errorMessage);
		setType(ctx, Type.Int);
	}

	@Override
	public void exitBoolExpr(BoolExprContext ctx) {
		Type type0 = getType(ctx.expr(0));
		Type type1 = getType(ctx.expr(1));
		String errorMessage = "Boolean expression is only possible for two booleans";
		if (!checkType(type0, type1) || !(checkType(type0, Type.Bool)))
			addError(ctx, errorMessage);
		setType(ctx, Type.Bool);
	}

	@Override
	public void exitArrayExpr(ArrayExprContext ctx) {
		if (ctx.expr() == null)
			setType(ctx, new Type.Array(0, Type.Void));
		else {
			Type type = getType(ctx.expr(0));
			for (ExprContext expr : ctx.expr()) {
				if (!type.equals(getType(expr))) {
					addError(ctx, "Not all types are the same in array initialisation");
					break;
				}
			}
			setType(ctx, new Type.Array(ctx.expr().size(), type));
		}
	}

	@Override
	public void exitArrayElemExpr(ArrayElemExprContext ctx) {
		Variable var = getDeclaredVariable(ctx.ID().getText());
		Type type = var.getType();
		for (ArrayContext array : ctx.array()) {
			type = ((Type.Array) type).getElemType();
			if (!checkType(getType(array.expr()), Type.Int))
				addError(array, "Array element retrieval without integer.");
		}
		setType(ctx, type);
		setVariable(ctx.ID(), var);
	}

	@Override
	public void exitFunctionExpr(FunctionExprContext ctx) {
		checkFunction(ctx, ctx.ID().getText(), false, ctx.arguments());

	}

	@Override
	public void exitProcedureExpr(ProcedureExprContext ctx) {
		checkFunction(ctx, ctx.ID().getText(), false);
	}

	@Override
	public void exitTrueExpr(TrueExprContext ctx) {
		setType(ctx, Type.Bool);
	}

	@Override
	public void exitFalseExpr(FalseExprContext ctx) {
		setType(ctx, Type.Bool);
	}

	@Override
	public void exitCharExpr(CharExprContext ctx) {
		setType(ctx, Type.Char);
	}

	@Override
	public void exitStringExpr(StringExprContext ctx) {
		int size = ctx.STRING_LIT().getText().length() - 2;
		this.stringSize.put(ctx, size);
		setType(ctx, new Type.Array(size, Type.Char));
	}

	@Override
	public void exitNumExpr(NumExprContext ctx) {
		setType(ctx, Type.Int);
	}

	@Override
	public void exitIdExpr(IdExprContext ctx) {
		String id = ctx.ID().getText();
		Variable var = getDeclaredVariable(id);
		if (var != null) {
			setType(ctx, var.getType());
			setVariable(ctx.ID(), var);
		} else
			addError(ctx, id + " is not declared.");
	}

	@Override
	public void exitBraceExpr(BraceExprContext ctx) {
		setType(ctx, getType(ctx.expr()));
	}

	private Type[] getArguments(ArgumentsContext ctx) {
		List<Type> result = new ArrayList<>();
		for (ExprContext expr : ctx.expr())
			result.add(getType(expr));
		return result.toArray(new Type[result.size()]);
	}

	private void checkFunction(ParserRuleContext ctx, String name, boolean isThread) {
		checkFunction(ctx, name, isThread, null);
	}

	private void checkFunction(ParserRuleContext ctx, String name, boolean isThread, ArgumentsContext args) {
		Type[] types = (args == null ? new Type[0] : getArguments(args));
		Function f = functionTable.get(name, types);
		if (f == null) {
			if (types.length == 0)
				addError(ctx, (isThread ? "Thread " : "Procedure ") + name + " is not defined.");
			else
				addError(ctx, (isThread ? "Thread " : "Function ") + name + " with arguments " + Arrays.toString(types)
						+ " is not defined.");
			return;
		}
		Type returnType = f.getReturnType();
		setType(ctx, returnType);
		setFunction(ctx, f);
	}

	private Type parseType(TypeContext ctx) {
		return parseType(errors, ctx);
	}

	/**
	 * Parses the TypeContext to a Type
	 */
	protected static Type parseType(List<String> errorList, TypeContext ctx) {
		if (ctx instanceof BasicTypeContext) {
			return parseType(((BasicTypeContext) ctx).BASICTYPE().getText());
		} else if (ctx instanceof PointerTypeContext) {
			return parseType(((PointerTypeContext) ctx).PTRTYPE().getText());
		} else if (ctx instanceof ArrayTypeContext) {
			ArrayTypeContext arrayTypeCtx = (ArrayTypeContext) ctx;
			int size = parseInt(errorList, ctx, arrayTypeCtx.NUM().getText());
			if (size <= 0)
				addError(errorList, ctx, "Array has invalid length");
			Type type = parseType(errorList, arrayTypeCtx.type());
			return new Type.Array(size, type);
		} else
			return null;
	}

	/**
	 * Parses an string to an integer Adds an error to the error list when the
	 * string cannot be parsed. Default return value is 0
	 */
	private static int parseInt(List<String> errorList, ParserRuleContext ctx, String s) {
		int result = 0;
		try {
			result = Integer.parseInt(s);
		} catch (NumberFormatException e) {
			addError(errorList, ctx, e.getMessage());
		}
		return result;
	}

	/**
	 * Parses a String into a Type
	 */
	private static Type parseType(String s) {
		switch (s) {
		case "int":
			return Type.Int;
		case "char":
			return Type.Char;
		case "bool":
			return Type.Bool;
		case "string":
			return Type.String;
		case "void":
			return Type.Void;
		case "int*":
			return Type.IntPtr;
		case "char*":
			return Type.CharPtr;
		case "bool*":
			return Type.BoolPtr;
		default:
			return null;
		}
	}

	/**
	 * Associates the parse tree node with the variable in the result
	 * 
	 * @param node
	 * @param var
	 */
	private void setVariable(ParseTree node, Variable var) {
		this.result.setVariable(node, var);
	}

	/**
	 * Returns the variable by name if it is declared in this or a higher scope
	 * 
	 * @param id
	 * @return
	 */
	private Variable getDeclaredVariable(String id) {
		Variable var = symbolTable.getVariable(id);
		if (var == null)
			var = globalScope.getVariable(id);
		return var;

	}

	private boolean checkType(Type a, Type b) {
		if (a == null)
			return false;
		return a.equals(b);
	}

	/**
	 * Indication if the parent has already opened a scope
	 * 
	 * @param ctx
	 *            The block statement context that opens a scope only when the
	 *            parent has not opened a scope yet.
	 * @return True when parent has opened a scope
	 */
	private boolean parentOpensScope(BlockStatContext ctx) {
		return ctx.parent instanceof FuncContext || ctx.parent instanceof IfStatContext
				|| ctx.parent instanceof WhileStatContext || ctx.parent instanceof ThreadDeclContext;
	}

	private void setType(ParseTree node, Type type) {
		this.result.setType(node, type);
	}

	private Type getType(ParseTree node) {
		return this.result.getType(node);
	}

	private void setFunction(ParseTree node, Function func) {
		this.result.setFunction(node, func);
	}

	private Function getFunction(ParseTree node) {
		return this.result.getFunction(node);
	}

	private void addError(ParserRuleContext node, String message) {
		addError(errors, node, message);
	}

	protected static void addError(List<String> errorList, ParserRuleContext node, String message) {
		int line = node.getStart().getLine();
		int column = node.getStart().getCharPositionInLine();
		errorList.add("At ln " + line + ", col " + column + ": " + message);

	}

	private Integer getSize(ParseTree node) {
		return this.stringSize.get(node);
	}

	private void openScope() {
		symbolTable.openScope();
	}

	private void closeScope(ParserRuleContext ctx) {
		Scope scope = symbolTable.getHead();
		int offset = symbolTable.getSize();
		Map<String, Variable> variables = scope.getVariables();
		for (String s : variables.keySet()) {
			Variable v = variables.get(s);
			v.setOffset(v.getOffset() + offset);
			// if(!v.isInitalized())
			// addError(ctx, s + " is not initialized");
		}
		symbolTable.closeScope();
	}

	private RuleContext getParentFunctionCtx(RuleContext ctx) {
		RuleContext parent = ctx.getParent();
		while (parent != null && !(parent instanceof FuncContext || parent instanceof ThreadDeclContext)) {
			parent = parent.getParent();
		}
		return parent;
	}

	private RuleContext getParentWhileCtx(RuleContext ctx) {
		RuleContext parent = ctx.getParent();
		while (parent != null && !(parent instanceof WhileStatContext)) {
			parent = parent.getParent();
		}
		return parent;
	}

}
