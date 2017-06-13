package pp.block5.cc.simple;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

import pp.block5.cc.pascal.SimplePascalBaseVisitor;
import pp.block5.cc.pascal.SimplePascalParser.AssStatContext;
import pp.block5.cc.pascal.SimplePascalParser.BlockContext;
import pp.block5.cc.pascal.SimplePascalParser.BlockStatContext;
import pp.block5.cc.pascal.SimplePascalParser.BodyContext;
import pp.block5.cc.pascal.SimplePascalParser.BoolExprContext;
import pp.block5.cc.pascal.SimplePascalParser.CompExprContext;
import pp.block5.cc.pascal.SimplePascalParser.FalseExprContext;
import pp.block5.cc.pascal.SimplePascalParser.IdExprContext;
import pp.block5.cc.pascal.SimplePascalParser.IfStatContext;
import pp.block5.cc.pascal.SimplePascalParser.InStatContext;
import pp.block5.cc.pascal.SimplePascalParser.MultExprContext;
import pp.block5.cc.pascal.SimplePascalParser.NumExprContext;
import pp.block5.cc.pascal.SimplePascalParser.OutStatContext;
import pp.block5.cc.pascal.SimplePascalParser.ParExprContext;
import pp.block5.cc.pascal.SimplePascalParser.PlusExprContext;
import pp.block5.cc.pascal.SimplePascalParser.PrfExprContext;
import pp.block5.cc.pascal.SimplePascalParser.ProgramContext;
import pp.block5.cc.pascal.SimplePascalParser.TrueExprContext;
import pp.block5.cc.pascal.SimplePascalParser.WhileStatContext;
import pp.iloc.Simulator;
import pp.iloc.model.Label;
import pp.iloc.model.Num;
import pp.iloc.model.Op;
import pp.iloc.model.OpCode;
import pp.iloc.model.Operand;
import pp.iloc.model.Program;
import pp.iloc.model.Reg;
import pp.iloc.model.Str;

/** Class to generate ILOC code for Simple Pascal. */
public class Generator extends SimplePascalBaseVisitor<Op> {
	/** The representation of the boolean value <code>false</code>. */
	public final static Num FALSE_VALUE = new Num(Simulator.FALSE);
	/** The representation of the boolean value <code>true</code>. */
	public final static Num TRUE_VALUE = new Num(Simulator.TRUE);

	/** The base register. */
	private Reg arp = new Reg("r_arp");
	/** The outcome of the checker phase. */
	private Result checkResult;
	/** Association of statement nodes to labels. */
	private ParseTreeProperty<Label> labels;
	/** The program being built. */
	private Program prog;
	/** Register count, used to generate fresh registers. */
	private int regCount;
	/** Association of expression and target nodes to registers. */
	private ParseTreeProperty<Reg> regs;

	/**
	 * Generates ILOC code for a given parse tree, given a pre-computed checker
	 * result.
	 */
	public Program generate(ParseTree tree, Result checkResult) {
		this.prog = new Program();
		this.checkResult = checkResult;
		this.regs = new ParseTreeProperty<>();
		this.labels = new ParseTreeProperty<>();
		this.regCount = 0;
		tree.accept(this);
		return this.prog;
	}

	@Override
	public Op visitProgram(ProgramContext ctx) {
		return visit(ctx.body());
	}

	@Override
	public Op visitBody(BodyContext ctx) {
		return visit(ctx.block());

	}

	@Override
	public Op visitBlock(BlockContext ctx) {
		Op first = visit(ctx.stat(0));
		for (int i = 1; i < ctx.stat().size(); i++) {
			visit(ctx.stat(i));
		}
		return first;
	}

	@Override
	public Op visitAssStat(AssStatContext ctx) {
		visit(ctx.expr());
		return emit(OpCode.storeAI, reg(ctx.expr()), arp, offset(ctx.target()));
	}

	@Override
	public Op visitPrfExpr(PrfExprContext ctx) {
		visit(ctx.expr());
		OpCode opcode = opcode(ctx.prfOp());
		if(opcode == OpCode.cmp_NE) {
			Reg trueReg = reg(ctx.prfOp());
			emit(OpCode.loadI, TRUE_VALUE, trueReg); 
			return emit(opcode, trueReg, reg(ctx.expr()), reg(ctx)); // not x == true <> x
		}
		else {
			return emit(opcode, new Num(0), reg(ctx.expr()), reg(ctx));
		}
	}

	@Override
	public Op visitMultExpr(MultExprContext ctx) {
		visit(ctx.expr(0));
		visit(ctx.expr(1));
		return emit(opcode(ctx.multOp()), reg(ctx.expr(0)), reg(ctx.expr(1)), reg(ctx));
	}

	@Override
	public Op visitPlusExpr(PlusExprContext ctx) {
		visit(ctx.expr(0));
		visit(ctx.expr(1));
		return emit(opcode(ctx.plusOp()), reg(ctx.expr(0)), reg(ctx.expr(1)), reg(ctx));
	}

	@Override
	public Op visitCompExpr(CompExprContext ctx) {
		visit(ctx.expr(0));
		visit(ctx.expr(1));
		return emit(opcode(ctx.compOp()), reg(ctx.expr(0)), reg(ctx.expr(1)), reg(ctx));
	}

	@Override
	public Op visitBoolExpr(BoolExprContext ctx) {
		visit(ctx.expr(0));
		visit(ctx.expr(1));
		return emit(opcode(ctx.boolOp()), reg(ctx.expr(0)), reg(ctx.expr(1)), reg(ctx));
	}

	@Override
	public Op visitParExpr(ParExprContext ctx) {
		Op result = visit(ctx.expr());
		setReg(ctx, reg(ctx.expr()));
		return result;
	}

	@Override
	public Op visitIdExpr(IdExprContext ctx) {
		return emit(OpCode.loadAI, arp, offset(ctx), reg(ctx));
	}

	@Override
	public Op visitNumExpr(NumExprContext ctx) {
		return emit(OpCode.loadI, num(ctx.NUM()), reg(ctx));
	}

	@Override
	public Op visitTrueExpr(TrueExprContext ctx) {
		return emit(OpCode.loadI, TRUE_VALUE, reg(ctx));
	}

	@Override
	public Op visitFalseExpr(FalseExprContext ctx) {
		return emit(OpCode.loadI, FALSE_VALUE, reg(ctx));
	}

	@Override
	public Op visitIfStat(IfStatContext ctx) {
		boolean withElse = ctx.stat(1) != null;
		Label elsePart = null;
		Label start = createLabel(ctx, "if");
		Label then = createLabel(ctx, "then");
		if (withElse)
			elsePart = createLabel(ctx, "else");
		Label end = createLabel(ctx, "end");
		emit(start, OpCode.nop);
		visit(ctx.expr());
		Label elseOrEnd = withElse ? elsePart : end;
		emit(OpCode.cbr, reg(ctx.expr()), then, elseOrEnd);
		emit(then, OpCode.nop);
		visit(ctx.stat(0));
		emit(OpCode.jumpI, end);
		if(withElse) {
			emit(elsePart, OpCode.nop);
			visit(ctx.stat(1));
		}
		return emit(end, OpCode.nop);
	}

	@Override
	public Op visitWhileStat(WhileStatContext ctx) {
		Label start = createLabel(ctx, "while");
		Label then = createLabel(ctx, "then");
		Label end = createLabel(ctx, "end");
		emit(start, OpCode.nop);
		visit(ctx.expr());
		emit(OpCode.cbr, reg(ctx.expr()), then, end);
		emit(then, OpCode.nop);
		visit(ctx.stat());
		emit(OpCode.jumpI, start);
		return emit(end, OpCode.nop);
	}

	@Override
	public Op visitBlockStat(BlockStatContext ctx) {
		return visit(ctx.block());
	}

	@Override
	public Op visitInStat(InStatContext ctx) {
		Reg targetReg = reg(ctx.target());
		emit(OpCode.in, str(ctx.STR()), targetReg);
		return emit(OpCode.storeAI, targetReg, arp, offset(ctx.target()));
	}

	@Override
	public Op visitOutStat(OutStatContext ctx) {
		visit(ctx.expr());
		return emit(OpCode.out, str(ctx.STR()), reg(ctx.expr()));
	}
	// Override the visitor methods

	/**
	 * Constructs an operation from the parameters and adds it to the program
	 * under construction.
	 */
	private Op emit(Label label, OpCode opCode, Operand... args) {
		Op result = new Op(label, opCode, args);
		this.prog.addInstr(result);
		return result;
	}

	/**
	 * Constructs an operation from the parameters and adds it to the program
	 * under construction.
	 */
	private Op emit(OpCode opCode, Operand... args) {
		return emit((Label) null, opCode, args);
	}

	/**
	 * Looks up the label for a given parse tree node, creating it if none has
	 * been created before. The label is actually constructed from the entry
	 * node in the flow graph, as stored in the checker result.
	 */
	@SuppressWarnings("unused")
	private Label label(ParserRuleContext node) {
		Label result = this.labels.get(node);
		if (result == null) {
			ParserRuleContext entry = this.checkResult.getEntry(node);
			result = createLabel(entry, "n");
			this.labels.put(node, result);
		}
		return result;
	}

	/** Creates a label for a given parse tree node and prefix. */
	private Label createLabel(ParserRuleContext node, String prefix) {
		Token token = node.getStart();
		int line = token.getLine();
		int column = token.getCharPositionInLine();
		String result = prefix + "_" + line + "_" + column;
		return new Label(result);
	}

	/**
	 * Retrieves the offset of a variable node from the checker result, wrapped
	 * in a {@link Num} operand.
	 */
	private Num offset(ParseTree node) {
		return new Num(this.checkResult.getOffset(node));
	}

	private Num num(ParseTree node) {
		return new Num(Integer.parseInt(node.getText()));
	}

	private Str str(ParseTree node) {
		return new Str(node.getText().replaceAll("\"", ""));
	}

	private OpCode opcode(ParseTree node) {
		return opcode(node, false);
	}

	private OpCode opcode(ParseTree node, boolean unary) {
		switch (node.getText().toUpperCase()) {
		case "+":
			return OpCode.add;
		case "-":
			return (unary ? OpCode.rsubI : OpCode.sub);
		case "*":
			return OpCode.mult;
		case "/":
			return OpCode.div;
		case "=":
			return OpCode.cmp_EQ;
		case "<":
			return OpCode.cmp_LT;
		case ">":
			return OpCode.cmp_GT;
		case "<>":
			return OpCode.cmp_NE;
		case "<=":
			return OpCode.cmp_LE;
		case ">=":
			return OpCode.cmp_GE;
		case "NOT":
			return OpCode.cmp_NE;
		case "AND":
			return OpCode.and;
		case "OR":
			return OpCode.or;
		default:
			throw new UnsupportedOperationException(node.getText() + " cannot be recognized as an opcode");
		}
	}

	/**
	 * Returns a register for a given parse tree node, creating a fresh register
	 * if there is none for that node.
	 */
	private Reg reg(ParseTree node) {
		Reg result = this.regs.get(node);
		if (result == null) {
			result = new Reg("r_" + this.regCount);
			this.regs.put(node, result);
			this.regCount++;
		}
		return result;
	}

	/** Assigns a register to a given parse tree node. */
	private void setReg(ParseTree node, Reg reg) {
		this.regs.put(node, reg);
	}
}
