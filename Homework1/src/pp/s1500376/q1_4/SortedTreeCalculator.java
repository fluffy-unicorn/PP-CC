package pp.s1500376.q1_4;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;

import pp.s1500376.q1_4.TreeParser.FullContext;
import pp.s1500376.q1_4.TreeParser.LeftContext;
import pp.s1500376.q1_4.TreeParser.NumContext;
import pp.s1500376.q1_4.TreeParser.RightContext;
import pp.s1500376.q1_4.TreeParser.TopContext;

public class SortedTreeCalculator extends TreeBaseListener {

	public ParseTreeProperty<Attribute> values = new ParseTreeProperty<>();

	@Override
	public void exitTop(TopContext ctx) {
		Attribute node = val(ctx.node());
		set(ctx, node.isSorted, node.lowerBound, node.upperBound);
	}

	@Override
	public void exitFull(FullContext ctx) {
		Attribute left = val(ctx.node(0));
		Attribute right = val(ctx.node(1));
		int num = toInt(ctx.NUM());
		boolean sorted = (left.isSorted && right.isSorted && num > left.upperBound && num < right.lowerBound);
		int upper = max(num, left.upperBound, right.upperBound);
		int lower = min(num, left.lowerBound, left.lowerBound);
		set(ctx, sorted, lower, upper);
	}

	@Override
	public void exitLeft(LeftContext ctx) {
		Attribute left = val(ctx.node());
		int num = toInt(ctx.NUM());
		boolean sorted = (left.isSorted && num > left.upperBound);
		int upper = max(num, left.upperBound);
		int lower = min(num, left.lowerBound);
		set(ctx, sorted, lower, upper);
	}

	@Override
	public void exitRight(RightContext ctx) {
		Attribute right = val(ctx.node());
		int num = toInt(ctx.NUM());
		boolean sorted = (right.isSorted && num < right.lowerBound);
		int upper = max(num, right.upperBound);
		int lower = min(num, right.lowerBound);
		set(ctx, sorted, lower, upper);

	}

	@Override
	public void exitNum(NumContext ctx) {
		int num = toInt(ctx.NUM());
		set(ctx, true, num, num);
	}
	
	public int toInt(TerminalNode s) {
		return Integer.parseInt(s.getText());
	}

	public void set(ParseTree tree, boolean sorted, int lower, int upper) {
		values.put(tree, new Attribute(sorted, lower, upper));
	}

	public Attribute val(ParseTree tree) {
		return values.get(tree);
	}

	public int max(int... val) {
		int max = 0;
		for (int v : val) {
			if (v > max)
				max = v;
		}
		return max;
	}

	public int min(int... val) {
		int min = -1;
		for (int v : val) {
			if (min == -1 || v < min)
				min = v;
		}
		return min;
	}

	public class Attribute {
		public boolean isSorted;
		public int lowerBound;
		public int upperBound;

		public Attribute(boolean sorted, int lower, int upper) {
			isSorted = sorted;
			lowerBound = lower;
			upperBound = upper;
		}
	}

	public static void main(String[] args) {
		String input1 = "((1 < 3) < 5 > ((6 < 8) < 10 > 15))";
		String input2 = "((1 < 5) < 3 > 2)";
		test(input1);
		test(input2);
	}

	public static void test(String input) {
		CharStream chars = CharStreams.fromString(input);
		Lexer lexer = new TreeLexer(chars);
		TokenStream tokens = new CommonTokenStream(lexer);
		TreeParser parser = new TreeParser(tokens);
		ParseTreeWalker walker = new ParseTreeWalker();
		SortedTreeCalculator calc = new SortedTreeCalculator();
		ParseTree tree = parser.top();
		walker.walk(calc, tree);
		String result =  calc.val(tree).isSorted ? "sorted" : "not sorted";
		System.out.println("\"" + input + "\" is " +result);
	}
}
