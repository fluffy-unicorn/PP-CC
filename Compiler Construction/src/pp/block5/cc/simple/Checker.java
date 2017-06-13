package pp.block5.cc.simple;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;

import pp.block5.cc.ParseException;
import pp.block5.cc.pascal.SimplePascalBaseListener;
import pp.block5.cc.pascal.SimplePascalParser.AssStatContext;
import pp.block5.cc.pascal.SimplePascalParser.BlockContext;
import pp.block5.cc.pascal.SimplePascalParser.BlockStatContext;
import pp.block5.cc.pascal.SimplePascalParser.BoolExprContext;
import pp.block5.cc.pascal.SimplePascalParser.CompExprContext;
import pp.block5.cc.pascal.SimplePascalParser.FalseExprContext;
import pp.block5.cc.pascal.SimplePascalParser.IdExprContext;
import pp.block5.cc.pascal.SimplePascalParser.IdTargetContext;
import pp.block5.cc.pascal.SimplePascalParser.IfStatContext;
import pp.block5.cc.pascal.SimplePascalParser.InStatContext;
import pp.block5.cc.pascal.SimplePascalParser.MultExprContext;
import pp.block5.cc.pascal.SimplePascalParser.NumExprContext;
import pp.block5.cc.pascal.SimplePascalParser.OutStatContext;
import pp.block5.cc.pascal.SimplePascalParser.ParExprContext;
import pp.block5.cc.pascal.SimplePascalParser.PlusExprContext;
import pp.block5.cc.pascal.SimplePascalParser.PrfExprContext;
import pp.block5.cc.pascal.SimplePascalParser.TrueExprContext;
import pp.block5.cc.pascal.SimplePascalParser.VarContext;
import pp.block5.cc.pascal.SimplePascalParser.VarDeclContext;
import pp.block5.cc.pascal.SimplePascalParser.WhileStatContext;

/** Class to type check and calculate flow entries and variable offsets. */
public class Checker extends SimplePascalBaseListener {
	/** Result of the latest call of {@link #check}. */
	private Result result;
	/** Variable scope for the latest call of {@link #check}. */
	private Scope scope;
	/** List of errors collected in the latest call of {@link #check}. */
	private List<String> errors;

	private Integer lastOffset;
	/**
	 * Runs this checker on a given parse tree, and returns the checker result.
	 * 
	 * @throws ParseException
	 *             if an error was found during checking.
	 */
	public Result check(ParseTree tree) throws ParseException {
		this.scope = new Scope();
		this.result = new Result();
		this.errors = new ArrayList<>();
		this.lastOffset = 0;
		new ParseTreeWalker().walk(this, tree);
		if (hasErrors()) {
			throw new ParseException(getErrors());
		}
		return this.result;
	}

	@Override
	public void exitIdTarget(IdTargetContext ctx) {
		Type type = this.scope.type(ctx.getText());
		Integer offset = this.scope.offset(ctx.getText());
		setType(ctx, type);
		setOffset(ctx, offset);
		setEntry(ctx, ctx);
	}
	
	@Override
	public void exitVarDecl(VarDeclContext ctx) {
		setEntry(ctx, entry(ctx.var(0)));
	}

	@Override
	public void exitVar(VarContext ctx) {
		Type type = parseType(ctx.type().getText());
		for (TerminalNode id : ctx.ID()) {
			setType(id, type);
			setOffset(id, getNextOffset());
			boolean putResult = this.scope.put(id.getText(), type);
			if (!putResult) {
				addError(ctx, "Id '%s' is already in scope", id.getText());
			}
		}
		setEntry(ctx, ctx);
	}

	@Override 
	public void exitAssStat(AssStatContext ctx) {
		checkType(ctx.target(), getType(ctx.expr()));
		setEntry(ctx, entry(ctx.expr()));
	}

	@Override
	public void exitIfStat(IfStatContext ctx) {
		checkType(ctx.expr(), Type.BOOL);
		setEntry(ctx, entry(ctx.stat(0)));
	}

	@Override
	public void exitWhileStat(WhileStatContext ctx) {
		checkType(ctx.expr(), Type.BOOL);
		setEntry(ctx, entry(ctx.stat()));
	}

	@Override
	public void exitBlockStat(BlockStatContext ctx) {
		setEntry(ctx, entry(ctx.block()));
	}

	
	@Override
	public void exitBlock(BlockContext ctx) {
		setEntry(ctx, entry(ctx.stat(0)));
	}
	@Override
	public void exitInStat(InStatContext ctx) {
		setEntry(ctx, entry(ctx.target()));
	}

	@Override
	public void exitOutStat(OutStatContext ctx) {
		setEntry(ctx, entry(ctx.expr()));
	}

	@Override
	public void exitBoolExpr(BoolExprContext ctx) {
		checkType(ctx.expr(0), Type.BOOL);
		checkType(ctx.expr(1), Type.BOOL);
		setType(ctx, Type.BOOL);
		setEntry(ctx, entry(ctx.expr(0)));
	}

	@Override
	public void exitCompExpr(CompExprContext ctx) {
		checkType(ctx.expr(0), Type.INT);
		checkType(ctx.expr(1), Type.INT);
		setType(ctx, Type.BOOL);
		setEntry(ctx, entry(ctx.expr(0)));
	}

	@Override
	public void exitFalseExpr(FalseExprContext ctx) {
		setType(ctx, Type.BOOL);
		setEntry(ctx, ctx);
	}

	@Override
	public void exitIdExpr(IdExprContext ctx) {
		String id = ctx.ID().getText();
		Type type = this.scope.type(id);
		if (type == null) {
			addError(ctx, "Variable '%s' not declared", id);
		} else {
			setType(ctx, type);
			setOffset(ctx, this.scope.offset(id));
			setEntry(ctx, ctx);
		}
	}

	@Override
	public void exitMultExpr(MultExprContext ctx) {
		checkType(ctx.expr(0), Type.INT);
		checkType(ctx.expr(1), Type.INT);
		setType(ctx, Type.INT);
		setEntry(ctx, entry(ctx.expr(0)));
	}

	@Override
	public void exitNumExpr(NumExprContext ctx) {
		setType(ctx, Type.INT);
		setEntry(ctx, ctx);
	}

	@Override
	public void exitParExpr(ParExprContext ctx) {
		setType(ctx, getType(ctx.expr()));
		setEntry(ctx, entry(ctx.expr()));
	}

	@Override
	public void exitPlusExpr(PlusExprContext ctx) {
		checkType(ctx.expr(0), Type.INT);
		checkType(ctx.expr(1), Type.INT);
		setType(ctx, Type.INT);
		setEntry(ctx, entry(ctx.expr(0)));
	}

	@Override
	public void exitPrfExpr(PrfExprContext ctx) {
		Type type;
		if (ctx.prfOp().MINUS() != null) {
			type = Type.INT;
		} else {
			assert ctx.prfOp().NOT() != null;
			type = Type.BOOL;
		}
		checkType(ctx.expr(), type);
		setType(ctx, type);
		setEntry(ctx, entry(ctx.expr()));
	}

	@Override
	public void exitTrueExpr(TrueExprContext ctx) {
		setType(ctx, Type.BOOL);
		setEntry(ctx, ctx);
	}

	/** Indicates if any errors were encountered in this tree listener. */
	public boolean hasErrors() {
		return !getErrors().isEmpty();
	}

	/** Returns the list of errors collected in this tree listener. */
	public List<String> getErrors() {
		return this.errors;
	}

	private Type parseType(String t) {
		if (t.equalsIgnoreCase("Integer")) {
			return Type.INT;
		} else if (t.equalsIgnoreCase("Boolean")) {
			return Type.BOOL;
		} else
			return null;
	}
	
	private int getNextOffset() {
		return this.lastOffset++;
	}

	/**
	 * Checks the inferred type of a given parse tree, and adds an error if it
	 * does not correspond to the expected type.
	 */
	private void checkType(ParserRuleContext node, Type expected) {
		Type actual = getType(node);
		if (actual == null) {
			throw new IllegalArgumentException("Missing inferred type of " + node.getText());
		}
		if (!actual.equals(expected)) {
			addError(node, "Expected type '%s' but found '%s'", expected, actual);
		}
	}

	/**
	 * Records an error at a given parse tree node.
	 * 
	 * @param ctx
	 *            the parse tree node at which the error occurred
	 * @param message
	 *            the error message
	 * @param args
	 *            arguments for the message, see {@link String#format}
	 */
	private void addError(ParserRuleContext node, String message, Object... args) {
		addError(node.getStart(), message, args);
	}

	/**
	 * Records an error at a given token.
	 * 
	 * @param token
	 *            the token at which the error occurred
	 * @param message
	 *            the error message
	 * @param args
	 *            arguments for the message, see {@link String#format}
	 */
	private void addError(Token token, String message, Object... args) {
		int line = token.getLine();
		int column = token.getCharPositionInLine();
		message = String.format(message, args);
		message = String.format("Line %d:%d - %s", line, column, message);
		this.errors.add(message);
	}

	/** Convenience method to add an offset to the result. */
	private void setOffset(ParseTree node, Integer offset) {
		this.result.setOffset(node, offset);
	}

	/** Convenience method to add a type to the result. */
	private void setType(ParseTree node, Type type) {
		this.result.setType(node, type);
	}

	/** Returns the type of a given expression or type node. */
	private Type getType(ParseTree node) {
		return this.result.getType(node);
	}

	/** Convenience method to add a flow graph entry to the result. */
	private void setEntry(ParseTree node, ParserRuleContext entry) {
		if (entry == null) {
			throw new IllegalArgumentException("Null flow graph entry");
		}
		this.result.setEntry(node, entry);
	}

	/** Returns the flow graph entry of a given expression or statement. */
	private ParserRuleContext entry(ParseTree node) {
		return this.result.getEntry(node);
	}
}
