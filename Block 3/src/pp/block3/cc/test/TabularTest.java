package pp.block3.cc.test;

import java.io.IOException;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.Test;

import pp.block3.cc.tabular.TabularLexer;
import pp.block3.cc.tabular.TabularParser;

public class TabularTest {
	private final String prefix = "src/pp/block3/cc/tabular/";

	@Test
	public void test() {
		test("tabular-1.tex");
		test("tabular-2.tex");
		test("tabular-3.tex");
		test("tabular-4.tex");
		test("tabular-5.tex");
	}

	private void test(String file) {
		parse(prefix + file);
	}

	private ParseTree parse(String file) {
		CharStream chars = null;
		try {
			chars = CharStreams.fromFileName(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Lexer lexer = new TabularLexer(chars);
		TokenStream tokens = new CommonTokenStream(lexer);
		TabularParser parser = new TabularParser(tokens);
		return parser.table();
	}
}
