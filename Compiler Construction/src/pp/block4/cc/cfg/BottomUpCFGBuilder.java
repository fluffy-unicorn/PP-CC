package pp.block4.cc.cfg;

import java.io.File;
import java.io.IOException;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import pp.block4.cc.ErrorListener;
import pp.block4.cc.cfg.FragmentParser.AssignStatContext;
import pp.block4.cc.cfg.FragmentParser.BlockStatContext;
import pp.block4.cc.cfg.FragmentParser.BreakStatContext;
import pp.block4.cc.cfg.FragmentParser.ContStatContext;
import pp.block4.cc.cfg.FragmentParser.DeclContext;
import pp.block4.cc.cfg.FragmentParser.IfStatContext;
import pp.block4.cc.cfg.FragmentParser.PrintStatContext;
import pp.block4.cc.cfg.FragmentParser.ProgramContext;
import pp.block4.cc.cfg.FragmentParser.WhileStatContext;

/** Template bottom-up CFG builder. */
public class BottomUpCFGBuilder extends FragmentBaseListener {
	public static class Tuple {
		public Node enter;
		public Node exit;

		Tuple(Node enter, Node exit) {
			this.enter = enter;
			this.exit = exit;
		}
	}

	/** The CFG being built. */
	private Graph graph;
	private ParseTreeProperty<Tuple> nodes = new ParseTreeProperty<>();

	/** Builds the CFG for a program contained in a given file. */
	public Graph build(File file) {
		Graph result = null;
		ErrorListener listener = new ErrorListener();
		try {
			CharStream chars = CharStreams.fromPath(file.toPath());
			Lexer lexer = new FragmentLexer(chars);
			lexer.removeErrorListeners();
			lexer.addErrorListener(listener);
			TokenStream tokens = new CommonTokenStream(lexer);
			FragmentParser parser = new FragmentParser(tokens);
			parser.removeErrorListeners();
			parser.addErrorListener(listener);
			ParseTree tree = parser.program();
			if (listener.hasErrors()) {
				System.out.printf("Parse errors in %s:%n", file.getPath());
				for (String error : listener.getErrors()) {
					System.err.println(error);
				}
			} else {
				result = build(tree);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	/** Builds the CFG for a program given as an ANTLR parse tree. */
	public Graph build(ParseTree tree) {
		this.graph = new Graph();
		ParseTreeWalker walker = new ParseTreeWalker();
		walker.walk(this, tree);
		return this.graph;
	}

	@Override
	public void enterBreakStat(BreakStatContext ctx) {
		throw new IllegalArgumentException("Break not supported");
	}

	@Override
	public void enterContStat(ContStatContext ctx) {
		throw new IllegalArgumentException("Continue not supported");
	}

	@Override
	public void exitDecl(DeclContext ctx) {
		Node n = addNode(ctx, toSimpleString(ctx));
		nodes.put(ctx, new Tuple(n, n));
	}

	@Override
	public void exitAssignStat(AssignStatContext ctx) {
		Node n = addNode(ctx, toSimpleString(ctx));
		nodes.put(ctx, new Tuple(n, n));
	}

	@Override
	public void exitIfStat(IfStatContext ctx) {
		Node start = addNode(ctx, "Start if");
		Node end = addNode(ctx, "End if");
		Tuple ifPart = nodes.get(ctx.stat(0));
		start.addEdge(ifPart.enter);
		ifPart.exit.addEdge(end);
		Tuple elsePart;
		if ((elsePart = nodes.get(ctx.stat(1))) != null) {
			start.addEdge(elsePart.enter);
			elsePart.exit.addEdge(end);
		} else {
			start.addEdge(end);
		}

		nodes.put(ctx, new Tuple(start, end));
	}

	@Override
	public void exitWhileStat(WhileStatContext ctx) {
		Node start = addNode(ctx, "Start while");
		Node end = addNode(ctx, "End while");

		Tuple stat = nodes.get(ctx.stat());

		start.addEdge(stat.enter);
		start.addEdge(end);
		stat.exit.addEdge(start);

		nodes.put(ctx, new Tuple(start, end));
	}

	@Override
	public void exitPrintStat(PrintStatContext ctx) {
		Node n = addNode(ctx, "Print");
		nodes.put(ctx, new Tuple(n, n));
	}

	@Override
	public void exitBlockStat(BlockStatContext ctx) {
		Node start = addNode(ctx, "Start block");
		Node end = addNode(ctx, "End block");
		if (ctx.stat().size() > 0) {
			start.addEdge(nodes.get(ctx.stat(0)).enter);
		}
		int i = 0;
		for (; i < ctx.stat().size() - 1; i++) {
			Node previousEnd = nodes.get(ctx.stat(i)).exit;
			Node nextStart = nodes.get(ctx.stat(i + 1)).enter;
			previousEnd.addEdge(nextStart);
		}
		nodes.get(ctx.stat(i)).exit.addEdge(end);
		nodes.put(ctx, new Tuple(start, end));

	}

	@Override
	public void exitProgram(ProgramContext ctx) {
		Node n = addNode(ctx, "Program");
		n.addEdge(nodes.get(ctx.stat(0)).enter);
		for (int i = 0; i < ctx.stat().size() - 1; i++) {
			Node previousEnd = nodes.get(ctx.stat(i)).exit;
			Node nextStart = nodes.get(ctx.stat(i + 1)).enter;
			previousEnd.addEdge(nextStart);
		}
	}
	
	private String toSimpleString(ParserRuleContext ctx) {
		StringBuffer sb = new StringBuffer();
		for(ParseTree p : ctx.children) {
			sb.append(" " + p.getText());
		}
		return sb.toString();
	}
	

	/**
	 * Adds a node to the CGF, based on a given parse tree node. Gives the CFG
	 * node a meaningful ID, consisting of line number and a further indicator.
	 */
	private Node addNode(ParserRuleContext node, String text) {
		return this.graph.addNode(node.getStart().getLine() + ": " + text);
	}

	/** Main method to build and print the CFG of a simple Java program. */
	public static void main(String[] args) {
		if (args.length == 0) {
			System.err.println("Usage: [filename]+");
			return;
		}
		BottomUpCFGBuilder builder = new BottomUpCFGBuilder();
		final String BASENAME = "src/pp/block4/cc/cfg/";
		for (String filename : args) {
			File file = new File(BASENAME + filename + ".prog");
			System.out.println(filename);
			Graph g = builder.build(file);
			System.out.println(g);
			try {
				g.writeDOT(BASENAME + filename + "BU.dot", true);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
}
