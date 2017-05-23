package pp.s1500376.q1_4;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
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
	
	/**
	 * Convert the text of a terminal node to an integer
	 * @param s The terminal node to be parsed
	 * @return The result of {@link Integer#parseInt})
	 */
	public int toInt(TerminalNode s) {
		return Integer.parseInt(s.getText());
	}

	public void set(ParseTree tree, boolean sorted, int lower, int upper) {
		values.put(tree, new Attribute(sorted, lower, upper));
	}

	public Attribute val(ParseTree tree) {
		return values.get(tree);
	}

	/**
	 * Calculates the maximum value of a number of integers
	 * @param val A number of integers
	 * @return The maximum value
	 */
	public int max(int... val) {
		int max = 0;
		for (int v : val) {
			if (v > max)
				max = v;
		}
		return max;
	}

	/**
	 * Calculates the minimum value of a number of integers
	 * @param val A number of integers
	 * @return The minimum value
	 */
	public int min(int... val) {
		int min = -1;
		for (int v : val) {
			if (min == -1 || v < min)
				min = v;
		}
		return min;
	}

	/**
	 * Class for storing the attribute values
	 */
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

	
}
