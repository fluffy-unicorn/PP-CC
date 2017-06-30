package pp.s1500376.q1_4;

import static org.junit.Assert.assertEquals;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.Test;


public class SortedTreeTest {

	@Test
	public void testCases() {
		String input1 = "((1 < 3) < 5 > ((6 < 8) < 10 > 15))";
		String input2 = "((1 < 5) < 3 > 2)";
		String input3 = "(((3 < 4) < 5 > (6 < 7 > (8 > 9))) < 10 > (11 < 12 > 13))";
		String input4 = "((((1 < 2) < 3) < 4) < 5 > 4)";
		test(input1, true);
		test(input2, false);
		test(input3, true);
		test(input4, false);
	}

	private void test(String input, boolean sorted) {
		CharStream chars = CharStreams.fromString(input);
		Lexer lexer = new TreeLexer(chars);
		TokenStream tokens = new CommonTokenStream(lexer);
		TreeParser parser = new TreeParser(tokens);
		ParseTreeWalker walker = new ParseTreeWalker();
		SortedTreeCalculator calc = new SortedTreeCalculator();
		ParseTree tree = parser.top();
		walker.walk(calc, tree);
		assertEquals(sorted, calc.val(tree).isSorted);
	}
	
	
}
