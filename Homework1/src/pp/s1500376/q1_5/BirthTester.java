package pp.s1500376.q1_5;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

public class BirthTester {

	@Test
	public void tests() {
		test("bible.birth", null);
		test("wrong.birth",
				   list("[2:0] Betty has not been introduced", 
						"[3:0] The first person of the beget rule is male",
						"[5:2] Andrea has already been introduced", 
						"[8:0] Betty has already been introduced"));
		test("biblewrong.birth",
				   list("[1:16] Adam has already been introduced", 
						"[1:36] The first person of the beget rule is male"));
		test("greek.birth", null);
	}

	private void test(String file, List<String> expected) {
		CharStream chars = null;
		try {
			chars = CharStreams.fromFileName("src/pp/s1500376/q1_5/" + file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Lexer lexer = new BirthLexer(chars);
		TokenStream tokens = new CommonTokenStream(lexer);
		BirthParser parser = new BirthParser(tokens);
		ParseTreeWalker walker = new ParseTreeWalker();
		BirthChecker calc = new BirthChecker();
		ParseTree tree = parser.branch();
		walker.walk(calc, tree);
		List<String> errors = calc.getErrors();
		assertTrue(areListsEqual(expected, errors));
	}

	public static boolean areListsEqual(List<String> a, List<String> b) {
		if (a == null)
			return b == null || b.size() == 0;
		if (a.size() != b.size())
			return false;
		for (int i = 0; i < a.size(); i++) {
			if (!a.get(i).equals(b.get(i)))
				return false;
		}
		return true;
	}

	public static List<String> list(String... strings) {
		List<String> result = new ArrayList<>();
		for (String s : strings)
			result.add(s);
		return result;
	}
}
