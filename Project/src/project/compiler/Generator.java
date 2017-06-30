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
import project.antlr.GooseSpeakParser.AcquireStatContext;
import project.antlr.GooseSpeakParser.ArrayElemAssStatContext;
import project.antlr.GooseSpeakParser.ArrayElemExprContext;
import project.antlr.GooseSpeakParser.ArrayExprContext;
import project.antlr.GooseSpeakParser.AssignStatContext;
import project.antlr.GooseSpeakParser.BitExprContext;
import project.antlr.GooseSpeakParser.BlockStatContext;
import project.antlr.GooseSpeakParser.BoolExprContext;
import project.antlr.GooseSpeakParser.BraceExprContext;
import project.antlr.GooseSpeakParser.BreakStatContext;
import project.antlr.GooseSpeakParser.CharExprContext;
import project.antlr.GooseSpeakParser.CompExprContext;
import project.antlr.GooseSpeakParser.ConcurrentDeclContext;
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
import project.antlr.GooseSpeakParser.GlobalStatContext;
import project.antlr.GooseSpeakParser.IdExprContext;
import project.antlr.GooseSpeakParser.IfStatContext;
import project.antlr.GooseSpeakParser.JoinStatContext;
import project.antlr.GooseSpeakParser.LockDeclContext;
import project.antlr.GooseSpeakParser.MultExprContext;
import project.antlr.GooseSpeakParser.NumExprContext;
import project.antlr.GooseSpeakParser.PlusExprContext;
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
import project.antlr.GooseSpeakParser.StatContext;
import project.antlr.GooseSpeakParser.StringExprContext;
import project.antlr.GooseSpeakParser.ThreadDeclContext;
import project.antlr.GooseSpeakParser.TrueExprContext;
import project.antlr.GooseSpeakParser.WhileStatContext;
import project.sprockell.Instruction;
import project.sprockell.Instruction.InstructionType;
import project.sprockell.Program;
import project.sprockell.SPRILType;
import project.sprockell.SPRILType.AddrImmDI.ATypes;
import project.sprockell.SPRILType.Op;
import project.sprockell.SPRILType.Reg;
import project.sprockell.SPRILType.Target.TTypes;

public class Generator extends GooseSpeakBaseVisitor<Reg> {

	private Program p;
	private Reg lastReg;
	private Result result;
	private ParseTreeProperty<Integer> startWhiles;
	private ParseTreeProperty<IntegerReference> endWhiles;
	private int currentPC;
	private IntegerReference main; // Integer reference to main function
	private IntegerReference endOfProgram;

	public Program generate(ParseTree tree, Result r) {
		this.result = r;
		p = new Program();
		lastReg = null;
		startWhiles = new ParseTreeProperty<>();
		endWhiles = new ParseTreeProperty<>();
		currentPC = 0;
		main = new IntegerReference();
		endOfProgram = new IntegerReference();
		tree.accept(this);
		return this.p;
	}

	@Override
	public Reg visitProgram(ProgramContext ctx) {
		// Set the value of the Global Variable Pointer register
		emit(InstructionType.Compute, Op.Decr, Reg.regSP, Reg.reg0, Reg.regGVP);
		emit("Start global data section");
		for (GlobalStatContext globalCtx : ctx.globalStat())
			visit(globalCtx);
		for (ConcurrentDeclContext conCtx : result.getConcurrentDeclarations())
			visit(conCtx);
		emit("End global data section");
		// Set the value of the Activation Record Pointer register
		emit(InstructionType.Compute, Op.Decr, Reg.regSP, Reg.reg0, Reg.regARP);
		// Jump to main function after initialization of global variables
		Reg reg = getReg();
		emit(InstructionType.Compute, Op.Equal, Reg.regSprID, Reg.reg0, reg);
		emit(InstructionType.Branch, reg, target(TTypes.Abs, main));
		pollMemAddress();
		for (FuncContext f : ctx.func())
			visit(f);
		for (ThreadDeclContext t : result.getThreadDeclarations())
			visit(t);
		endOfProgram.setValue(currentPC);
		emit(InstructionType.Load, addr(ATypes.ImmValue, currentPC + 4), reg);
		for (int i = 0; i < Compiler.MAX_THREADS - 1; i++)
			emit(InstructionType.WriteInstr, reg, addr(ATypes.DirAddr, i));
		emit(InstructionType.EndProg);
		return null;
	}

	private void pollMemAddress() {
		// TODO one time decr?
		Reg reg = getReg();
		emit(InstructionType.Compute, Op.Decr, Reg.regSprID, Reg.reg0, reg);
		emit(InstructionType.ReadInstr, addr(ATypes.IndAddr, reg));
		emit(InstructionType.Receive, reg);
		Reg cmpReg = getReg();
		emit(InstructionType.Compute, Op.Equal, reg, Reg.reg0, cmpReg);
		emit(InstructionType.Branch, cmpReg, target(TTypes.Rel, -4));
		emit(InstructionType.Jump, target(TTypes.Ind, reg));
	}

	@Override
	public Reg visitGlobalDeclStat(GlobalDeclStatContext ctx) {
		allocateVariable(result.getVariable(ctx.ID()));
		return null;
	}

	@Override
	public Reg visitGlobalDeclAssStat(GlobalDeclAssStatContext ctx) {
		Variable var = result.getVariable(ctx.ID());
		if (var.isPointer()) {
			Reg reg = visit(ctx.expr());
			emit(InstructionType.Store, reg, addr(ATypes.IndAddr, Reg.regHP));
			emit(InstructionType.Push, Reg.regHP);
			emit(InstructionType.Compute, Op.Incr, Reg.regHP, Reg.reg0, Reg.regHP);
		} else if (var.isArray() && ctx.expr() instanceof ArrayExprContext) {
			allocateVariable(var);
			storeArray(ctx.expr(), var);
		} else if (var.isArray() && ctx.expr() instanceof StringExprContext) {
			allocateVariable(var);
			storeString(ctx.expr(), var);
		} else {
			Reg reg = visit(ctx.expr());
			emit(InstructionType.Push, reg);
		}
		// TODO release register reg
		return null;
	}

	@Override
	public Reg visitFunc(FuncContext ctx) {
		Function f = this.result.getFunction(ctx);
		f.setLocation(currentPC);
		emit("Start " + f.toString());
		if (f.isVoidMain()) {
			main.setValue(currentPC);
		}
		visit(ctx.stat());
		if (f.isVoidMain()) {
			emit(InstructionType.Jump, target(TTypes.Abs, endOfProgram));
		}
		if (!f.hasReturn()) {
			Reg returnAddrReg = getReg();
			emit(InstructionType.Compute, Op.Incr, Reg.regARP, Reg.reg0, Reg.regSP);
			emit(InstructionType.Pop, returnAddrReg);
			emit(InstructionType.Jump, target(TTypes.Ind, returnAddrReg));
		}
		return null;
	}

	@Override
	public Reg visitLockDecl(LockDeclContext ctx) {
		Variable v = result.getVariable(ctx.ID());
		emit(InstructionType.WriteInstr, Reg.reg0, addr(ATypes.DirAddr, v.getOffset()));
		return null;
	}

	@Override
	public Reg visitSharedDecl(SharedDeclContext ctx) {
		Reg reg = visit(ctx.expr());
		Variable v = result.getVariable(ctx.ID());
		emit(InstructionType.WriteInstr, reg, addr(ATypes.DirAddr, v.getOffset()));
		return null; // TODO release reg
	}

	@Override
	public Reg visitThreadDecl(ThreadDeclContext ctx) {
		Function f = this.result.getFunction(ctx);
		f.setLocation(currentPC);
		emit("Start " + f.toString());
		visit(ctx.stat());
		Reg sprIdAddr = getReg();
		emit(InstructionType.Compute, Op.Decr, Reg.regSprID, Reg.reg0, sprIdAddr);
		emit(InstructionType.WriteInstr, Reg.reg0, addr(ATypes.IndAddr, sprIdAddr));
		emit(InstructionType.EndProg);
		return null;
	}

	@Override
	public Reg visitBlockStat(BlockStatContext ctx) {
		for (StatContext stat : ctx.stat()) {
			visit(stat);
		}
		return null;
	}

	@Override
	public Reg visitPrintStat(PrintStatContext ctx) {
		Reg reg = visit(ctx.expr());
		Type type = result.getType(ctx.expr());
		if (type.equals(Type.Int))
			emit(InstructionType.WriteInstr, reg, Reg.numberIO);
		else if (type.equals(Type.Bool))
			emit(InstructionType.WriteInstr, reg, Reg.boolIO);
		else if (type.equals(Type.Char))
			emit(InstructionType.WriteInstr, reg, Reg.charIO);
		else
			emit(InstructionType.WriteInstr, reg, Reg.stringIO);
		return reg; // TODO needed?
	}

	@Override
	public Reg visitReadStat(ReadStatContext ctx) {
		Reg reg = getReg();
		emit(InstructionType.ReadInstr, Reg.numberIO);
		emit(InstructionType.Receive, reg);
		Reg reg2 = calculateOffset(result.getVariable(ctx));
		emit(InstructionType.Store, reg, addr(ATypes.IndAddr, reg2));
		reduceReg();
		return reg;
	}

	@Override
	public Reg visitDeclStat(DeclStatContext ctx) {
		allocateVariable(result.getVariable(ctx.ID()));
		return null;
	}

	@Override
	public Reg visitDeclAssStat(DeclAssStatContext ctx) {
		Variable var = this.result.getVariable(ctx.ID());
		if (var.isArray() && ctx.expr() instanceof ArrayExprContext) {
			allocateVariable(var);
			storeArray(ctx.expr(), var);
		} else if (var.isArray() && ctx.expr() instanceof StringExprContext) {
			allocateVariable(var);
			storeString(ctx.expr(), var);
		} else {
			Reg result = visit(ctx.expr());
			storeVariable(var, result);
			emit(InstructionType.Push, result);
		}
		return null;
	}

	private void storeString(ExprContext ctx, Variable var) {
		Reg offset = calculateOffset(var);
		String s = ((StringExprContext) ctx).STRING_LIT().getText();
		Reg heapAddress = getReg();
		emit(InstructionType.Load, addr(ATypes.IndAddr, offset), heapAddress);
		emit(InstructionType.Compute, Op.Incr, heapAddress, Reg.reg0, heapAddress);
		Reg charReg = getReg();
		for (int i = 1; i < s.length() - 1; i++) {
			emit(InstructionType.Load, addr(ATypes.ImmValue, s.charAt(i)), charReg);
			emit(InstructionType.Store, charReg, addr(ATypes.IndAddr, heapAddress));
			emit(InstructionType.Compute, Op.Incr, heapAddress, Reg.reg0, heapAddress);
		}
	}

	private void storeArray(ExprContext ctx, Variable var) {
		Reg offset = calculateOffset(var);
		ArrayExprContext exprCtx = (ArrayExprContext) ctx;
		Reg heapAddress = getReg();
		emit(InstructionType.Load, addr(ATypes.IndAddr, offset), heapAddress);
		emit(InstructionType.Compute, Op.Incr, heapAddress, Reg.reg0, heapAddress);
		for (ExprContext expr : exprCtx.expr()) {
			Reg result = visit(expr);
			emit(InstructionType.Store, result, addr(ATypes.IndAddr, heapAddress));
			emit(InstructionType.Compute, Op.Incr, heapAddress, Reg.reg0, heapAddress);
		}
	}

	@Override
	public Reg visitAssignStat(AssignStatContext ctx) {
		Reg result = visit(ctx.expr());
		Variable var = this.result.getVariable(ctx.ID());
		storeVariable(var, result);
		return null;
	}

	@Override
	public Reg visitArrayElemAssStat(ArrayElemAssStatContext ctx) {
		Reg result = visit(ctx.expr());
		Variable var = this.result.getVariable(ctx.ID());
		Reg offset = calculateOffset(var);
		emit(InstructionType.Load, addr(ATypes.IndAddr, offset), offset);
		Reg index = visit(ctx.array(0).expr());
		storeArrayElem(offset, result, index);
		return null;
	}

	@Override
	public Reg visitIfStat(IfStatContext ctx) {
		Reg compReg = visit(ctx.expr()); // Visit comparative expression
		IntegerReference endIf = new IntegerReference();
		IntegerReference thenPart = new IntegerReference(1);
		emit(InstructionType.Branch, compReg, target(TTypes.Abs, thenPart));
		if (ctx.stat(1) != null) {
			visit(ctx.stat(1)); // Emit else part
		}
		thenPart.setValue(currentPC + 1);
		emit(InstructionType.Jump, target(TTypes.Abs, endIf));
		visit(ctx.stat(0));
		endIf.setValue(currentPC);
		return null;
	}

	@Override
	public Reg visitWhileStat(WhileStatContext ctx) {
		// Start of the while loop
		int startWhile = currentPC;
		startWhiles.put(ctx, startWhile);
		// Visit the comparative expression
		Reg compReg = visit(ctx.expr());
		// End of while loop
		IntegerReference endWhile = new IntegerReference();
		endWhiles.put(ctx, endWhile);
		emit(InstructionType.Branch, compReg, target(TTypes.Rel, 2));
		emit(InstructionType.Jump, target(TTypes.Abs, endWhile));
		visit(ctx.stat());
		emit(InstructionType.Jump, target(TTypes.Abs, startWhile));
		endWhile.setValue(currentPC);
		return null;
	}

	@Override
	public Reg visitBreakStat(BreakStatContext ctx) {
		IntegerReference endWhile = endWhiles.get(getParentWhileStat(ctx));
		emit(InstructionType.Jump, target(TTypes.Abs, endWhile));
		return null;
	}

	@Override
	public Reg visitContinueStat(ContinueStatContext ctx) {
		int startWhile = startWhiles.get(getParentWhileStat(ctx));
		emit(InstructionType.Jump, target(TTypes.Abs, startWhile));
		return null;
	}

	@Override
	public Reg visitFunctionCall(FunctionCallContext ctx) {
		return functionCall(ctx);
	}

	@Override
	public Reg visitProcedureCall(ProcedureCallContext ctx) {
		return functionCall(ctx);
	}

	@Override
	public Reg visitReturnExpr(ReturnExprContext ctx) {
		Reg exprReg = visit(ctx.expr());
		Reg returnAddrReg = getReg();
		// Reset the stack pointer to ARP + 1
		emit(InstructionType.Compute, Op.Incr, Reg.regARP, Reg.reg0, Reg.regSP);
		emit(InstructionType.Pop, returnAddrReg);
		emit(InstructionType.Push, exprReg);
		emit(InstructionType.Jump, target(TTypes.Ind, returnAddrReg));
		return null; // TODO private function?
	}

	@Override
	public Reg visitReturnVoid(ReturnVoidContext ctx) {
		Function f = result.getFunction(ctx);
		if (f.isVoidMain()) {
			emit(InstructionType.Jump, target(TTypes.Abs, endOfProgram));
			return null;
		}
		Reg returnAddrReg = getReg();
		emit(InstructionType.Compute, Op.Incr, Reg.regARP, Reg.reg0, Reg.regSP);
		emit(InstructionType.Pop, returnAddrReg);
		emit(InstructionType.Jump, target(TTypes.Ind, returnAddrReg));
		return null; // TODO private function?
	}

	@Override
	public Reg visitForkStat(ForkStatContext ctx) {
		Function thread = result.getFunction(ctx);
		Variable var = result.getVariable(ctx.ID(0));
		Reg threadLoc = getReg();
		emit(InstructionType.Load, addr(ATypes.ImmValue, thread.getLocation()), threadLoc);
		emit(InstructionType.WriteInstr, threadLoc, addr(ATypes.DirAddr, var.getThreadId()));
		return null;
	}

	@Override
	public Reg visitJoinStat(JoinStatContext ctx) {
		Variable var = result.getVariable(ctx);
		Reg result = getReg();
		emit(InstructionType.ReadInstr, addr(ATypes.DirAddr, var.getThreadId()));
		emit(InstructionType.Receive, result);
		emit(InstructionType.Compute, Op.NEq, result, Reg.reg0, result);
		emit(InstructionType.Branch, result, target(TTypes.Rel, -3));
		return null;
	}

	@Override
	public Reg visitAcquireStat(AcquireStatContext ctx) {
		Variable var = result.getVariable(ctx);
		Reg result = getReg();
		emit(InstructionType.TestAndSet, addr(ATypes.DirAddr, var.getOffset()));
		emit(InstructionType.Receive, result);
		emit(InstructionType.Branch, result, target(TTypes.Rel, 2));
		emit(InstructionType.Jump, target(TTypes.Rel, -3));
		return null;
	}

	@Override
	public Reg visitReleaseStat(ReleaseStatContext ctx) {
		Variable var = result.getVariable(ctx);
		emit(InstructionType.WriteInstr, Reg.reg0, addr(ATypes.DirAddr, var.getOffset()));
		return null;
	}

	@Override
	public Reg visitPrfExpr(PrfExprContext ctx) {
		Reg expr = visit(ctx.expr());
		if (ctx.prfOp().LEN() != null)
			emit(InstructionType.Load, addr(ATypes.IndAddr, expr), expr);
		else if (ctx.prfOp().STR() != null)
			return null; // TODO STR
		else if (ctx.prfOp().LOG_NOT() != null)
			emit(InstructionType.Compute, Op.Not, expr, Reg.reg0, expr);
		else if (ctx.prfOp().BW_NOT() != null)
			emit(InstructionType.Compute, Op.BWNot, expr, Reg.reg0, expr);
		return expr;
	}

	@Override
	public Reg visitPowerExpr(PowerExprContext ctx) {
		Reg reg0 = visit(ctx.expr(0));
		Reg reg1 = visit(ctx.expr(1));
		emit(InstructionType.Compute, Op.Power, reg0, reg1, reg0);
		return reg0;
	}

	@Override
	public Reg visitMultExpr(MultExprContext ctx) {
		Reg reg0 = visit(ctx.expr(0));
		Reg reg1 = visit(ctx.expr(1));
		Op op = null;
		if (ctx.multOp().MULT() != null)
			op = Op.Mul;
		else if (ctx.multOp().DIV() != null)
			op = Op.Div;
		else if (ctx.multOp().MOD() != null)
			op = Op.Mod;
		emit(InstructionType.Compute, op, reg0, reg1, reg0); // TODO new reg
		return reg0;
	}

	@Override
	public Reg visitPlusExpr(PlusExprContext ctx) {
		Reg reg0 = visit(ctx.expr(0));
		Reg reg1 = visit(ctx.expr(1));
		Op op = null;
		if (ctx.plusOp().MINUS() == null)
			op = Op.Add;
		else
			op = Op.Sub;
		emit(InstructionType.Compute, op, reg0, reg1, reg0); // TODO new reg
		return reg0;
	}

	@Override
	public Reg visitCompExpr(CompExprContext ctx) {
		Reg reg0 = visit(ctx.expr(0));
		Reg reg1 = visit(ctx.expr(1));
		Op op = null;
		if (ctx.compOp().LOG_EQ() != null)
			op = Op.Equal;
		else if (ctx.compOp().LOG_GE() != null)
			op = Op.GtE;
		else if (ctx.compOp().LOG_GT() != null)
			op = Op.Gt;
		else if (ctx.compOp().LOG_LE() != null)
			op = Op.LtE;
		else if (ctx.compOp().LOG_LT() != null)
			op = Op.Lt;
		else if (ctx.compOp().LOG_NE() != null)
			op = Op.NEq;
		emit(InstructionType.Compute, op, reg0, reg1, reg0); // TODO new reg
		return reg0;
	}

	@Override
	public Reg visitBitExpr(BitExprContext ctx) {
		Reg reg0 = visit(ctx.expr(0));
		Reg reg1 = visit(ctx.expr(1));
		Op op = null;
		if (ctx.bitOp().BW_AND() != null)
			op = Op.And;
		else if (ctx.bitOp().BW_OR() != null)
			op = Op.Or;
		else if (ctx.bitOp().BW_XOR() != null)
			op = Op.Xor;
		else if (ctx.bitOp().BW_LSHFT() != null)
			op = Op.LShift;
		else if (ctx.bitOp().BW_RSHFT() != null)
			op = Op.RShift;
		emit(InstructionType.Compute, op, reg0, reg1, reg0);
		return reg0;
	}

	@Override
	public Reg visitBoolExpr(BoolExprContext ctx) {
		Reg reg0 = visit(ctx.expr(0));
		Reg reg1 = visit(ctx.expr(1));
		if (ctx.boolOp().LOG_AND() != null)
			emit(InstructionType.Compute, Op.And, reg0, reg1, reg0);
		else
			emit(InstructionType.Compute, Op.Or, reg0, reg1, reg0);
		return reg0;
	}

	@Override
	public Reg visitArrayExpr(ArrayExprContext ctx) {
		return null;
	}

	@Override
	public Reg visitArrayElemExpr(ArrayElemExprContext ctx) {
		Variable var = this.result.getVariable(ctx.ID());
		Reg index = visit(ctx.array(0).expr());
		return loadArrayElem(var, index);

	}

	@Override
	public Reg visitFunctionExpr(FunctionExprContext ctx) {
		return functionCall(ctx);
	}

	@Override
	public Reg visitProcedureExpr(ProcedureExprContext ctx) {
		return functionCall(ctx);
	}

	@Override
	public Reg visitTrueExpr(TrueExprContext ctx) {
		Reg reg = getReg();
		emit(InstructionType.Load, addr(ATypes.ImmValue, true), reg);
		return reg;
	}

	@Override
	public Reg visitFalseExpr(FalseExprContext ctx) {
		Reg reg = getReg();
		emit(InstructionType.Load, addr(ATypes.ImmValue, false), reg);
		return reg;
	}

	@Override
	public Reg visitCharExpr(CharExprContext ctx) {
		Reg reg = getReg();
		emit(InstructionType.Load, addr(ATypes.ImmValue, ctx.getText()), reg);
		return reg;
	}

	@Override
	public Reg visitStringExpr(StringExprContext ctx) {
		return null;
	}

	@Override
	public Reg visitNumExpr(NumExprContext ctx) {
		Reg reg = getReg();
		emit(InstructionType.Load, addr(ATypes.ImmValue, toInt(ctx)), reg);
		return reg;
	}

	@Override
	public Reg visitIdExpr(IdExprContext ctx) {
		Variable var = result.getVariable(ctx.ID());
		return loadVariable(var);
	}

	@Override
	public Reg visitBraceExpr(BraceExprContext ctx) {
		return visit(ctx.expr());
	}

	private int toInt(ParseTree ctx) {
		return Integer.parseInt(ctx.getText());
	}

	private void restorePointer(Reg pointer) {
		Reg oldPointer = getReg();
		emit(InstructionType.Pop, oldPointer);
		emit(InstructionType.Compute, Op.Add, oldPointer, Reg.reg0, pointer);
		reduceReg();
		// TODO free oldPointer
	}

	private Reg calculateOffset(Variable var) {
		// Pointer for calculating the absolute location on the stack
		Reg dataValuePointer;
		Reg reg = getReg();
		if (var.isGlobal())
			dataValuePointer = Reg.regGVP;
		else
			dataValuePointer = Reg.regARP;
		emit(InstructionType.Load, addr(ATypes.ImmValue, var.getOffset()), reg);
		emit(InstructionType.Compute, Op.Sub, dataValuePointer, reg, reg);
		return reg;
	}

	private void storeVariable(Variable var, Reg value) {
		if (var.isSharedInt()) {
			emit(InstructionType.WriteInstr, value, addr(ATypes.DirAddr, var.getOffset()));
		} else {
			Reg reg = calculateOffset(var);
			if (var.isPointer()) {
				emit(InstructionType.Load, addr(ATypes.IndAddr, reg), reg);
				emit(InstructionType.Store, value, addr(ATypes.IndAddr, reg));
				emit(InstructionType.Push, reg);
			} else {
				emit(InstructionType.Store, value, addr(ATypes.IndAddr, reg));
			}
		}
	}

	private void storeArrayElem(Reg heapAddress, Reg value, Reg index) {
		emit(InstructionType.Compute, Op.Incr, index, Reg.reg0, index);
		emit(InstructionType.Compute, Op.Add, heapAddress, index, heapAddress);
		emit(InstructionType.Store, value, addr(ATypes.IndAddr, heapAddress));
	}

	private void pushFunctionArguments(ParserRuleContext ctx) {
		List<ExprContext> exprs = new ArrayList<>();
		if (ctx instanceof FunctionCallContext)
			exprs = ((FunctionCallContext) ctx).arguments().expr();
		else if (ctx instanceof FunctionExprContext)
			exprs = ((FunctionExprContext) ctx).arguments().expr();
		for (ExprContext expr : exprs) {
			Reg exprReg;
			if (expr instanceof IdExprContext) {
				Variable var = result.getVariable(((IdExprContext) expr).ID());
				if (var.isPointer() || var.isArray()) {
					exprReg = calculateOffset(var);
					emit(InstructionType.Load, addr(ATypes.IndAddr, exprReg), exprReg);
					emit(InstructionType.Push, exprReg);
					return;
				}
			}
			exprReg = visit(expr);
			emit(InstructionType.Push, exprReg);
		}
		return;
	}

	private Reg functionCall(ParserRuleContext ctx) {
		Function f = result.getFunction(ctx);
		Reg reg = getReg();
		IntegerReference returnAddress = new IntegerReference();
		// Store old stack pointer
		emit(InstructionType.Push, Reg.regSP);
		// Store old activation record pointer
		emit(InstructionType.Push, Reg.regARP);
		// Store return address
		emit(InstructionType.Load, addr(ATypes.ImmValue, returnAddress), reg);
		emit(InstructionType.Push, reg);
		// Calculate new activation record pointer
		emit(InstructionType.Compute, Op.Decr, Reg.regSP, Reg.reg0, reg);
		// Push the function arguments
		pushFunctionArguments(ctx);
		// Store the new activation record pointer
		emit(InstructionType.Compute, Op.Add, reg, Reg.reg0, Reg.regARP);
		// Call the function
		emit(InstructionType.Jump, target(TTypes.Abs, f.getLocation()));
		returnAddress.setValue(currentPC);
		// When the called function has a return type, retrieve
		Reg returnReg = null;
		if (!f.getReturnType().equals(Type.Void)) {
			returnReg = getReg();
			emit(InstructionType.Pop, returnReg);
		}
		// Restore activation record pointer
		restorePointer(Reg.regARP);
		// Restore stack pointer
		restorePointer(Reg.regSP);
		return returnReg;
	}

	private RuleContext getParentWhileStat(RuleContext ctx) {
		RuleContext parent = ctx.parent;
		while (!(parent instanceof WhileStatContext)) {
			parent = parent.parent;
		}
		return parent;
	}

	private Reg loadVariable(Variable var) {
		Reg reg = getReg();
		if (var.isSharedInt()) {
			emit(InstructionType.ReadInstr, addr(ATypes.DirAddr, var.getOffset()));
			emit(InstructionType.Receive, reg);
		} else if (var.isPointer()) {
			reg = calculateOffset(var);
			emit(InstructionType.Load, addr(ATypes.IndAddr, reg), reg);
			emit(InstructionType.Load, addr(ATypes.IndAddr, reg), reg);

		} else {
			reg = calculateOffset(var);
			emit(InstructionType.Load, addr(ATypes.IndAddr, reg), reg);
		}
		return reg;
	}

	private Reg loadArrayElem(Variable var, Reg index) {
		Reg offset = calculateOffset(var);
		emit(InstructionType.Load, addr(ATypes.IndAddr, offset), offset);
		emit(InstructionType.Compute, Op.Incr, index, Reg.reg0, index);
		emit(InstructionType.Compute, Op.Add, offset, index, offset);
		emit(InstructionType.Load, addr(ATypes.IndAddr, offset), offset);
		return offset;
	}

	private void allocateVariable(Variable var) {
		if (var == null) {
			/* Skip next two checks */ } else if (var.isPointer())
			emit(InstructionType.Compute, Op.Incr, Reg.regHP, Reg.reg0, Reg.regHP);
		else if (var.isArray()) {
			Type.Array arr = (Type.Array) var.getType();
			Reg reg = getReg();
			emit(InstructionType.Load, addr(ATypes.ImmValue, arr.size()), reg);
			emit(InstructionType.Store, reg, addr(ATypes.IndAddr, Reg.regHP));
			emit(InstructionType.Push, Reg.regHP);
			emit(InstructionType.Compute, Op.Incr, reg, Reg.reg0, reg);
			emit(InstructionType.Compute, Op.Add, Reg.regHP, reg, Reg.regHP);
			return;
		}
		// Allocate space for the global variable
		emit(InstructionType.Push, Reg.reg0);
	}

	private void emit(InstructionType it, SPRILType... hwt) {
		this.p.addInstruction(new Instruction(it, hwt));
		this.currentPC++;
	}

	private void emit(String comment) {
		this.p.addInstruction(new Instruction(InstructionType.Comment, new SPRILType.Comment(comment)));
	}

	private Reg getReg() {
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
