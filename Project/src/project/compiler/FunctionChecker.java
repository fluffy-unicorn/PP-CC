package project.compiler;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.tree.ParseTree;

import project.antlr.GooseSpeakBaseVisitor;
import project.antlr.GooseSpeakParser.ArgumentDeclContext;
import project.antlr.GooseSpeakParser.ConcurrentDeclContext;
import project.antlr.GooseSpeakParser.FuncContext;
import project.antlr.GooseSpeakParser.ProgramContext;
import project.antlr.GooseSpeakParser.ThreadDeclContext;
import project.antlr.GooseSpeakParser.TypeContext;

public class FunctionChecker extends GooseSpeakBaseVisitor<Function> {

	private FunctionTable functionTable;
	private List<String> errors;

	public FunctionTable check(List<String> errors, ParseTree tree) throws ParseException {
		this.functionTable = new FunctionTable();
		this.errors = errors;
		tree.accept(this);
		return functionTable;
	}

	@Override
	public Function visitProgram(ProgramContext ctx) {
		for (FuncContext f : ctx.func()) {
			Function result = visit(f);
			try {
				functionTable.addFunction(result);
			} catch (CompilerException e) {
				Checker.addError(errors, f, result.toString() + " is already declared.");
			}
		}
		for (ConcurrentDeclContext conCtx : ctx.concurrentDecl()) {
			if(conCtx instanceof ThreadDeclContext) {
				ThreadDeclContext t = (ThreadDeclContext)conCtx;
				Function result = visit(t);
				try {
					functionTable.addThread(result, t);
				} catch (CompilerException e) {
					Checker.addError(errors, t, result.toString() + " is already declared.");
				}
			}
		}
		return null;
	}

	@Override
	public Function visitFunc(FuncContext ctx) {
		ArgumentDeclContext argumentCtx = ctx.argumentDecl();
		List<Type> types = new ArrayList<>();
		Type returnType;
		if (argumentCtx != null)
			for (TypeContext type : argumentCtx.type())
				types.add(Checker.parseType(errors, type));
		if (ctx.type() != null)
			returnType = Checker.parseType(errors, ctx.type());
		else
			returnType = Type.Void;
		return new Function(returnType, ctx.ID().getText(), types.toArray(new Type[0]));
	}
	
	@Override
	public Function visitThreadDecl(ThreadDeclContext ctx) {
		return new Function(Type.Thread, ctx.ID().getText(), new Type[0]);
	}

}
