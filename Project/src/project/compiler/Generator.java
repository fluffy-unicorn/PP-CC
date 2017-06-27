package project.compiler;

import static project.sprockell.SPRILType.AddrImmDI.addr;
import static project.sprockell.SPRILType.Target.target;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

import project.antlr.GooseSpeakBaseVisitor;
import project.antlr.GooseSpeakParser.AssignStatContext;
import project.antlr.GooseSpeakParser.BlockStatContext;
import project.antlr.GooseSpeakParser.BoolExprContext;
import project.antlr.GooseSpeakParser.BraceExprContext;
import project.antlr.GooseSpeakParser.BreakStatContext;
import project.antlr.GooseSpeakParser.CompExprContext;
import project.antlr.GooseSpeakParser.ContinueStatContext;
import project.antlr.GooseSpeakParser.DeclAssStatContext;
import project.antlr.GooseSpeakParser.DeclStatContext;
import project.antlr.GooseSpeakParser.ExprContext;
import project.antlr.GooseSpeakParser.FalseExprContext;
import project.antlr.GooseSpeakParser.FuncContext;
import project.antlr.GooseSpeakParser.FunctionCallContext;
import project.antlr.GooseSpeakParser.FunctionExprContext;
import project.antlr.GooseSpeakParser.GlobalDeclAssStatContext;
import project.antlr.GooseSpeakParser.GlobalDeclStatContext;
import project.antlr.GooseSpeakParser.GlobalStatContext;
import project.antlr.GooseSpeakParser.IdExprContext;
import project.antlr.GooseSpeakParser.IfStatContext;
import project.antlr.GooseSpeakParser.MultExprContext;
import project.antlr.GooseSpeakParser.NumExprContext;
import project.antlr.GooseSpeakParser.PlusExprContext;
import project.antlr.GooseSpeakParser.PrintStatContext;
import project.antlr.GooseSpeakParser.ProcedureCallContext;
import project.antlr.GooseSpeakParser.ProcedureExprContext;
import project.antlr.GooseSpeakParser.ProgramContext;
import project.antlr.GooseSpeakParser.ReadStatContext;
import project.antlr.GooseSpeakParser.ReturnExprContext;
import project.antlr.GooseSpeakParser.ReturnVoidContext;
import project.antlr.GooseSpeakParser.StatContext;
import project.antlr.GooseSpeakParser.TrueExprContext;
import project.antlr.GooseSpeakParser.WhileStatContext;
import project.sprockell.SPRILType;
import project.sprockell.SPRILType.AddrImmDI.ATypes;
import project.sprockell.SPRILType.Op;
import project.sprockell.SPRILType.Reg;
import project.sprockell.SPRILType.Target.TTypes;
import project.sprockell.Instruction;
import project.sprockell.Instruction.InstructionType;
import project.sprockell.Program;

public class Generator extends GooseSpeakBaseVisitor<Integer> {

	private Program p = new Program();
	private ParseTreeProperty<SPRILType.Reg> registers = new ParseTreeProperty<>();
	private Reg lastReg = null;
	private Result result;
	private ParseTreeProperty<IntegerReference> endOfWhiles = new ParseTreeProperty<>();
	private int currentPC = 0;
	private IntegerReference main = new IntegerReference(); // Integer reference
															// to main function

	public Program generate(ParseTree tree, Result r) {
		this.result = r;
		tree.accept(this);
		return this.p;
	}

	@Override
	public Integer visitProgram(ProgramContext ctx) {
		emit("Start global data section");
		int count = 0;
		for (GlobalStatContext s : ctx.globalStat()) {
			count += visit(s);
		}
		emit("End global data section");
		// Jump to main function after initialisation of global variables
		emit(InstructionType.Jump, target(TTypes.Abs, main));
		for (FuncContext f : ctx.func()) {
			count += visit(f);
		}
		emit(InstructionType.EndProg);
		return count + 2;
	}

	private Integer increaseDataSizeRegister() {
		emit(InstructionType.Compute, Op.Incr, Reg.regDSZ, Reg.reg0, Reg.regDSZ);
		return 1;
	}

	@Override
	public Integer visitGlobalDeclStat(GlobalDeclStatContext ctx) {
		return increaseDataSizeRegister();
	}

	@Override
	public Integer visitGlobalDeclAssStat(GlobalDeclAssStatContext ctx) {
		int offset = this.result.getVariable(ctx.ID()).getOffset();
		visit(ctx.expr());
		Reg reg = getReg(ctx.expr());
		emit(InstructionType.Store, reg, addr(ATypes.DirAddr, offset));
		return increaseDataSizeRegister();
	}

	@Override
	public Integer visitFunc(FuncContext ctx) {
		Function f = this.result.getFunction(ctx);
		f.setLocation(currentPC);
		emit("Start " + f.toString());
		if (FunctionTable.voidMain.equals(f)) {
			main.setValue(currentPC);
		}
		return visit(ctx.stat());
	}

	@Override
	public Integer visitBlockStat(BlockStatContext ctx) {
		int result = 0;
		for (StatContext stat : ctx.stat()) {
			Integer r = visit(stat);
			if (r != null)
				result += r;
		}
		return result;
	}

	@Override
	public Integer visitPrintStat(PrintStatContext ctx) {
		int lineCount = visit(ctx.expr());
		Reg reg0 = getReg(ctx.expr());
		Type type = result.getType(ctx.expr());
		if(type.equals(Type.Int))
			emit(InstructionType.WriteInstr, reg0, Reg.numberIO);
		else if(type.equals(Type.Bool))
			emit(InstructionType.WriteInstr, reg0, Reg.boolIO);
		else if(type.equals(Type.Char))
			emit(InstructionType.WriteInstr, reg0, Reg.charIO);
		else
			emit(InstructionType.WriteInstr, reg0, Reg.stringIO);
		setReg(ctx, reg0);
		return lineCount + 1;
	}
	
	@Override
	public Integer visitReturnExpr(ReturnExprContext ctx) {
		visit(ctx.expr());
		Reg exprReg = getReg(ctx.expr());
		Reg arpReg = getReg();
		emit(InstructionType.Pop, arpReg);
		emit(InstructionType.Compute, Op.Add, arpReg, Reg.reg0, Reg.regARP);
		Reg returnAddrReg = getReg(); // TODO reuse reg
		emit(InstructionType.Pop, returnAddrReg);
		emit(InstructionType.Push, exprReg);
		emit(InstructionType.Jump, target(TTypes.Ind, returnAddrReg));
		return 5; // TODO private function for return
	}

	@Override
	public Integer visitReturnVoid(ReturnVoidContext ctx) {
		Function f = result.getFunction(ctx);
		if (FunctionTable.voidMain.equals(f))
			return 0;
		Reg arpReg = getReg();
		emit(InstructionType.Pop, arpReg);
		emit(InstructionType.Compute, Op.Add, arpReg, Reg.reg0, Reg.regARP);
		Reg returnAddrReg = getReg(); // TODO reuse reg
		emit(InstructionType.Pop, returnAddrReg);
		emit(InstructionType.Jump, target(TTypes.Ind, returnAddrReg));
		return 4;
	}

	@Override
	public Integer visitIfStat(IfStatContext ctx) {
		int result = visit(ctx.expr()); // Visit comparative expression
		Reg compReg = getReg(ctx.expr());
		IntegerReference endIf = new IntegerReference();
		IntegerReference thenPart = new IntegerReference(1); // Branch over jump
																// statement
		emit(InstructionType.Branch, compReg, target(TTypes.Rel, thenPart));
		if (ctx.stat(1) != null) {
			int lineCountElse = visit(ctx.stat(1)); // Emit else part
			thenPart.setValue(lineCountElse + 1);
			result += lineCountElse + 1;
		}
		emit(InstructionType.Jump, target(TTypes.Rel, endIf));
		thenPart.increment();
		result++;
		int lineCount = visit(ctx.stat(0));
		endIf.setValue(lineCount + 1);
		return result + lineCount + 1;
	}

	

	@Override
	public Integer visitReadStat(ReadStatContext ctx) {
		Reg reg = getReg();
		emit(InstructionType.ReadInstr, Reg.numberIO);
		emit(InstructionType.Receive, reg);
		Reg reg2 = getReg();
		int lineCount = calculateOffset(result.getVariable(ctx), reg2);
		emit(InstructionType.Store, reg, addr(ATypes.IndAddr, reg2));
		reduceReg();
		setReg(ctx, reg);
		return 3 + lineCount;
	}

	private Integer calculateOffset(Variable var, Reg reg) {
		if (var.isGlobal()) {
			emit(InstructionType.Load, addr(ATypes.ImmValue, var.getOffset()), reg);
		} else {
			emit(InstructionType.Load, addr(ATypes.ImmValue, var.getOffset()), reg);
			emit(InstructionType.Compute, Op.Add, Reg.regARP, reg, reg);
		}
		return var.isGlobal() ? 1 : 2;
	}

	@Override
	public Integer visitDeclStat(DeclStatContext ctx) {
		return increaseDataSizeRegister();
	}

	@Override
	public Integer visitDeclAssStat(DeclAssStatContext ctx) {
		int lineCount = visit(ctx.expr());
		Reg result = getReg(ctx.expr());
		Reg reg = getReg();
		Variable var = this.result.getVariable(ctx.ID());
		lineCount += calculateOffset(var, reg);
		lineCount += storeVariable(var, result, reg);
		reduceReg();
		return lineCount + 1 + increaseDataSizeRegister();
	}

	@Override
	public Integer visitAssignStat(AssignStatContext ctx) {
		int lineCount = visit(ctx.expr());
		Reg result = getReg(ctx.expr());
		Reg reg = getReg();
		Variable var = this.result.getVariable(ctx.ID());
		lineCount += calculateOffset(var, reg);
		lineCount += storeVariable(var, result, reg);
		reduceReg();
		return lineCount + 1;
	}

	private Integer storeVariable(Variable var, Reg value, Reg reg) {
		emit(InstructionType.Store, value, addr(ATypes.IndAddr, reg));
		return 1;
	}
	@Override
	public Integer visitFunctionExpr(FunctionExprContext ctx) {
		return functionCall(ctx);
	}

	@Override
	public Integer visitProcedureExpr(ProcedureExprContext ctx) {
		return functionCall(ctx);
	}

	private Integer storeFunctionArguments(ParserRuleContext ctx) {
		int result = 0;

		List<ExprContext> exprs = new ArrayList<>();
		if (ctx instanceof FunctionCallContext)
			exprs = ((FunctionCallContext) ctx).arguments().expr();
		else if (ctx instanceof FunctionExprContext)
			exprs = ((FunctionExprContext) ctx).arguments().expr();
		int p = 0;
		for (ExprContext expr : exprs) {
			result += visit(expr);
			Reg exprReg = getReg(expr);
			Reg parReg = getReg();
			emit(InstructionType.Load, addr(ATypes.ImmValue, p), parReg);
			emit(InstructionType.Compute, Op.Add, Reg.regDSZ, parReg, parReg);
			emit(InstructionType.Store, exprReg, addr(ATypes.IndAddr, parReg));
			result += 3;
			p++;
		}
		return result;
	}

	private Integer functionCall(ParserRuleContext ctx) {
		Function f = result.getFunction(ctx);
		Reg reg = getReg();
		IntegerReference returnAddress = new IntegerReference();
		emit(InstructionType.Load, addr(ATypes.ImmValue, returnAddress), reg);
		emit(InstructionType.Push, reg);
		int result = 2;
		emit("call " + ctx.getText());
		// Arguments TODO better
		result += storeFunctionArguments(ctx);
		emit(InstructionType.Push, Reg.regARP);
		emit(InstructionType.Compute, Op.Add, Reg.regDSZ, Reg.reg0, Reg.regARP);
		returnAddress.setValue(currentPC + 1);
		emit(InstructionType.Jump, target(TTypes.Abs, f.getLocation()));
		result += 3;
		if (!f.getReturnType().equals(Type.Void)) {
			Reg returnValue = getReg();
			emit(InstructionType.Pop, returnValue);
			result += 1;
			setReg(ctx, returnValue);
		}
		return result;
	}

	@Override
	public Integer visitFunctionCall(FunctionCallContext ctx) {
		return functionCall(ctx);
	}

	@Override
	public Integer visitProcedureCall(ProcedureCallContext ctx) {
		return functionCall(ctx);
	}

	@Override
	public Integer visitBreakStat(BreakStatContext ctx) {
		RuleContext parent = ctx.parent;
		while (!(parent instanceof WhileStatContext)) {
			parent = parent.parent;
		}
		IntegerReference endOfWhile = endOfWhiles.get(parent);
		emit(InstructionType.Jump, target(TTypes.Abs, endOfWhile));
		return 1;
	}

	@Override
	public Integer visitContinueStat(ContinueStatContext ctx) {
		return null; // TODO
	}

	@Override
	public Integer visitWhileStat(WhileStatContext ctx) {
		int exprSize = visit(ctx.expr());
		Reg compReg = getReg(ctx.expr());
		IntegerReference endWhile = new IntegerReference();
		IntegerReference startWhile = new IntegerReference();
		IntegerReference endOfWhile = new IntegerReference();
		endOfWhiles.put(ctx, endOfWhile);
		emit(InstructionType.Branch, compReg, target(TTypes.Rel, 2));
		emit(InstructionType.Jump, target(TTypes.Rel, endWhile));
		int whileSize = visit(ctx.stat());
		emit(InstructionType.Jump, target(TTypes.Rel, startWhile));
		endWhile.setValue(whileSize + 2);
		startWhile.setValue(-whileSize - exprSize - 2);
		endOfWhile.setValue(currentPC);
		return whileSize + 3;
	}

	@Override
	public Integer visitBraceExpr(BraceExprContext ctx) {
		int result = visit(ctx.expr());
		setReg(ctx, getReg(ctx.expr()));
		return result;
	}

	@Override
	public Integer visitTrueExpr(TrueExprContext ctx) {
		return visitChildren(ctx);
	}

	@Override
	public Integer visitCompExpr(CompExprContext ctx) {
		int lineCount0 = visit(ctx.expr(0));
		int lineCount1 = visit(ctx.expr(1));
		Reg reg0 = getReg(ctx.expr(0));
		Reg reg1 = getReg(ctx.expr(1));
		Op op;
		switch (ctx.compOp().getText()) {
		case "<=":
			op = Op.LtE;
			break;
		case "<":
			op = Op.Lt;
			break;
		case ">=":
			op = Op.Gt;
			break;
		case ">":
			op = Op.GtE;
			break;
		case "==":
			op = Op.Equal;
			break;
		case "<>":
			op = Op.Neq;
			break;
		default:
			op = null;
		}
		emit(InstructionType.Compute, op, reg0, reg1, reg0); // TODO new reg
		setReg(ctx, reg0);
		return lineCount0 + lineCount1 + 1;
	}

	@Override
	public Integer visitFalseExpr(FalseExprContext ctx) {
		return visitChildren(ctx);
	}

	@Override
	public Integer visitBoolExpr(BoolExprContext ctx) {
		int lineCount0 = visit(ctx.expr(0));
		int lineCount1 = visit(ctx.expr(1));
		Reg reg0 = getReg(ctx.expr(0));
		Reg reg1 = getReg(ctx.expr(1));
		switch (ctx.boolOp().getText()) {
		case "&&":
			emit(InstructionType.Compute, Op.And, reg0, reg1, reg0); // TODO
																		// new
																		// reg
			break;
		case "||":
			emit(InstructionType.Compute, Op.Or, reg0, reg1, reg0); // TODO
																	// new
																	// reg
			break;
		}
		setReg(ctx, reg0);
		return lineCount0 + lineCount1 + 1;
	}

	@Override
	public Integer visitMultExpr(MultExprContext ctx) {
		int lineCount0 = visit(ctx.expr(0));
		int lineCount1 = visit(ctx.expr(1));
		Reg reg0 = getReg(ctx.expr(0));
		Reg reg1 = getReg(ctx.expr(1));
		Op op = null;
		if (ctx.multOp().DIV() == null) {
			op = Op.Mul;
		} else {
			op = Op.Div;
		}
		emit(InstructionType.Compute, op, reg0, reg1, reg0); // TODO new reg
		setReg(ctx, reg0);
		return lineCount0 + lineCount1 + 1;
	}

	@Override
	public Integer visitNumExpr(NumExprContext ctx) {
		Reg reg = getReg();
		setReg(ctx, reg);
		emit(InstructionType.Load, addr(ATypes.ImmValue, toInt(ctx)), reg);
		return 1;
	}

	private int toInt(ParseTree ctx) {
		return Integer.parseInt(ctx.getText());
	}

	@Override
	public Integer visitPlusExpr(PlusExprContext ctx) {
		int lineCount0 = visit(ctx.expr(0));
		int lineCount1 = visit(ctx.expr(1));
		Reg reg0 = getReg(ctx.expr(0));
		Reg reg1 = getReg(ctx.expr(1));
		Op op = null;
		if (ctx.plusOp().MINUS() == null) {
			op = Op.Add;
		} else {
			op = Op.Sub;
		}
		emit(InstructionType.Compute, op, reg0, reg1, reg0); // TODO new reg
		setReg(ctx, reg0);
		return lineCount0 + lineCount1 + 1;
	}

	@Override
	public Integer visitIdExpr(IdExprContext ctx) {
		Reg reg = getReg();
		Variable var = result.getVariable(ctx.ID());
		int lineCount = calculateOffset(var, reg);
		lineCount += loadVariable(var, reg);
		setReg(ctx, reg);
		return lineCount + 1;
	}

	private Integer loadVariable(Variable var, Reg reg) {
		emit(InstructionType.Load, addr(ATypes.IndAddr, reg), reg);
		if(var.isPointer()) { // TODO
			emit(InstructionType.ReadInstr, addr(ATypes.IndAddr, reg));
			emit(InstructionType.Receive, reg);
		}
		return 1;
	}

	private void emit(InstructionType it, SPRILType... hwt) {
		this.p.addInstruction(new Instruction(it, hwt));
		this.currentPC++;
	}

	private void emit(String comment) {
		this.p.addInstruction(new Instruction(InstructionType.Comment, new SPRILType.Comment(comment)));
	}

	private SPRILType.Reg getReg(ParseTree node) {
		return this.registers.get(node);
	}

	private SPRILType.Reg getReg() {
		if (lastReg == null) {
			lastReg = SPRILType.Reg.regA;
			return lastReg;
		}
		switch (lastReg) {
		case regA:
			lastReg = SPRILType.Reg.regB;
			break;
		case regB:
			lastReg = SPRILType.Reg.regC;
			break;
		case regC:
			lastReg = SPRILType.Reg.regD;
			break;
		case regD:
			lastReg = SPRILType.Reg.regE;
			break;
		case regE:
			lastReg = SPRILType.Reg.regF;
			break;
		case regF:
			lastReg = SPRILType.Reg.regA;
			break;
		default:
			lastReg = null;
		}
		return lastReg;
	}

	private void setReg(ParseTree t, Reg reg) {
		this.registers.put(t, reg);
	}

	private void reduceReg() {
		switch (lastReg) {
		case regA:
			lastReg = null;
			break;
		case regB:
			lastReg = SPRILType.Reg.regA;
			break;
		case regC:
			lastReg = SPRILType.Reg.regB;
			break;
		case regD:
			lastReg = SPRILType.Reg.regC;
			break;
		case regE:
			lastReg = SPRILType.Reg.regD;
			break;
		case regF:
			lastReg = SPRILType.Reg.regE;
			break;
		default:
			lastReg = null;
		}
	}

}
