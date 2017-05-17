package pp.block3.cc.symbol;

import java.util.ArrayList;
import java.util.List;

import pp.block3.cc.symbol.DeclUseParser.DeclContext;
import pp.block3.cc.symbol.DeclUseParser.ProgramContext;
import pp.block3.cc.symbol.DeclUseParser.SeriesContext;
import pp.block3.cc.symbol.DeclUseParser.UseContext;

public class CheckDeclUse extends DeclUseBaseListener {
	private SymbolTable st;
	private List<String> errors;
	private String filename;

	public void init(String filename) {
		st = new MySymbolTable();
		errors = new ArrayList<String>();
		this.filename = filename;
	}

	@Override
	public void enterProgram(ProgramContext ctx) {
		st.openScope();
	}

	@Override
	public void enterSeries(SeriesContext ctx) {
		st.openScope();
	}

	@Override
	public void exitProgram(ProgramContext ctx) {
		st.closeScope();
	}

	@Override
	public void exitSeries(SeriesContext ctx) {
		st.closeScope();
	}

	@Override
	public void exitDecl(DeclContext ctx) {
		String id = ctx.ID().getText();
		boolean result = st.add(id);
		if (!result) {
			int line = ctx.getStart().getLine();
			int column = ctx.getStart().getCharPositionInLine();
			String error = "is already declared in this scope";
			errors.add(filename + " at ln " + line + ", col " + column + ": '" + id + "' " + error);
		}
	}

	@Override
	public void exitUse(UseContext ctx) {
		String id = ctx.ID().getText();
		boolean result = st.contains(id);
		if (!result) {
			int line = ctx.getStart().getLine();
			int column = ctx.getStart().getCharPositionInLine();
			String error = "is not declared";
			errors.add(filename + " at ln " + line + ", col " + column + ": '" + id + "' " + error);
		}
	}

	public List<String> getErrors() {
		return errors;
	}
}
