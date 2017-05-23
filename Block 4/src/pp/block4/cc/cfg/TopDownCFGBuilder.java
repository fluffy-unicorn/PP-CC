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

/** Template top-down CFG builder. */
public class TopDownCFGBuilder extends FragmentBaseListener {
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
			ProgramContext tree = parser.program();
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
	public Graph build(ProgramContext tree) {
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
	public void enterProgram(ProgramContext ctx) {
		Node n = addNode(ctx, "Program");
		// First statement
		Node startFirst = addNode(ctx.stat(0), "Start statement 0");
		Node endFirst = addNode(ctx.stat(0), "End statement 0");
		nodes.put(ctx.stat(0), new Tuple(startFirst, endFirst));
		n.addEdge(startFirst);
		// Remaining statements
		int i = 0;
		for (; i < ctx.stat().size() - 1; i++) {
			Node startNext = addNode(ctx.stat(i + 1), "Start statement " + (i + 1));
			Node endNext = addNode(ctx.stat(i + 1), "End statement " + (i + 1));
			Node endPrev = nodes.get(ctx.stat(i)).exit;
			endPrev.addEdge(startNext);
			nodes.put(ctx.stat(i + 1), new Tuple(startNext, endNext));
		}
	}

	@Override
	public void enterPrintStat(PrintStatContext ctx) {
		Node start = nodes.get(ctx).enter;
		Node end = nodes.get(ctx).exit;
		start.setId("Print");
		start.addEdge(end);
	}

	@Override
	public void enterWhileStat(WhileStatContext ctx) {
		Node start = nodes.get(ctx).enter;
		Node end = nodes.get(ctx).exit;
		Node statStart = addNode(ctx.stat(), "Start while statement");
		Node statEnd = addNode(ctx.stat(), "End while statement");
		nodes.put(ctx.stat(), new Tuple(statStart, statEnd));
		start.setId("Start while");
		end.setId("End while");
		start.addEdge(end);
		start.addEdge(statStart);
		statEnd.addEdge(end);
		statEnd.addEdge(start);
	};

	@Override
	public void enterIfStat(IfStatContext ctx) {
		Node start = nodes.get(ctx).enter;
		Node end = nodes.get(ctx).exit;
		Node ifStatStart = addNode(ctx.stat(0), "Start if statement");
		Node ifStatEnd = addNode(ctx.stat(0), "End if statement");
		nodes.put(ctx.stat(0), new Tuple(ifStatStart, ifStatEnd));
		start.setId("Start if");
		end.setId("End if");
		start.addEdge(ifStatStart);
		ifStatEnd.addEdge(end);
		if (ctx.stat(1) != null) {
			Node elseStatStart = addNode(ctx.stat(1), "Start else statement");
			Node elseStatEnd = addNode(ctx.stat(1), "End else statement");
			nodes.put(ctx.stat(1), new Tuple(elseStatStart, elseStatEnd));
			start.addEdge(elseStatStart);
			elseStatEnd.addEdge(end);
		} else {
			start.addEdge(end);
		}
	};

	@Override
	public void enterBlockStat(BlockStatContext ctx) {
		Node start = nodes.get(ctx).enter;
		Node end = nodes.get(ctx).exit;
		// First statement
		Node startFirst = addNode(ctx.stat(0), "Start block statement 0");
		Node endFirst = addNode(ctx.stat(0), "End block statement 0");
		nodes.put(ctx.stat(0), new Tuple(startFirst, endFirst));
		start.addEdge(startFirst);
		// Remaining statements
		int i = 0;
		for (; i < ctx.stat().size() - 1; i++) {
			Node startNext = addNode(ctx.stat(i + 1), "Start block statement " + (i + 1));
			Node endNext = addNode(ctx.stat(i + 1), "End block statement " + (i + 1));
			Node endPrev = nodes.get(ctx.stat(i)).exit;
			endPrev.addEdge(startNext);
			nodes.put(ctx.stat(i + 1), new Tuple(startNext, endNext));
		}
		nodes.get(ctx.stat(i)).exit.addEdge(end);
	};

	@Override
	public void enterAssignStat(AssignStatContext ctx) {
		Node start = nodes.get(ctx).enter;
		Node end = nodes.get(ctx).exit;
		start.setId(beautify(ctx));
		start.addEdge(end);
	}

	@Override
	public void enterDecl(DeclContext ctx) {
		Node start = nodes.get(ctx).enter;
		Node end = nodes.get(ctx).exit;
		start.setId(beautify(ctx));
		start.addEdge(end);
	}

	private String beautify(ParserRuleContext node) {
		StringBuilder nodeText = new StringBuilder();
		for (ParseTree child : node.children) {
			nodeText.append(" " + child.getText());
		}
		return node.getStart().getLine() + ": " + nodeText.toString();
	}

	/**
	 * Adds a node to he CGF, based on a given parse tree node. Gives the CFG
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
		TopDownCFGBuilder builder = new TopDownCFGBuilder();
		final String BASENAME = "src/pp/block4/cc/cfg/";
		for (String filename : args) {
			File file = new File(BASENAME + filename + ".prog");
			System.out.println(filename);
			Graph g = builder.build(file);
			System.out.println(g);
			try {
				g.writeDOT(BASENAME + filename + "TD.dot", true);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
