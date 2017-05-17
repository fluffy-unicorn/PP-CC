package pp.block3.cc.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
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

import pp.block3.cc.symbol.CheckDeclUse;
import pp.block3.cc.symbol.DeclUseLexer;
import pp.block3.cc.symbol.DeclUseParser;

public class CheckDeclUseTest {
	private final ParseTreeWalker walker = new ParseTreeWalker();
	private CheckDeclUse cdu = new CheckDeclUse();
	private final String prefix = "src/pp/block3/cc/test/";

	@Test
	public void test() {
		test("example1.du",  true);
		test("example2.du", false);
		test("example3.du", false);
	}

	private void test(String file, boolean succeed) {
		ParseTree tree = parseCDU(prefix + file);
		this.cdu.init(file);
		this.walker.walk(this.cdu, tree);
		assertEquals(this.cdu.getErrors().isEmpty(), succeed);
		printErrors(cdu.getErrors());
	}

	private void printErrors(List<String> errors) {
		for(String s : errors) {
			System.err.println(s);
		}
	}
	private ParseTree parseCDU(String file) {
		CharStream chars = null;
		try {
			chars = CharStreams.fromFileName(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Lexer lexer = new DeclUseLexer(chars);
		TokenStream tokens = new CommonTokenStream(lexer);
		DeclUseParser parser = new DeclUseParser(tokens);
		return parser.program();
	}
}
