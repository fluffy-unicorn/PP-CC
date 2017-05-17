package pp.block3.cc.test;

import static org.junit.Assert.assertEquals;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.Test;

import pp.block3.cc.antlr.CC3;
import pp.block3.cc.antlr.CC3AttrLexer;
import pp.block3.cc.antlr.CC3AttrParser;
import pp.block3.cc.antlr.CC3AttrParser.TContext;
import pp.block3.cc.antlr.CC3Lexer;
import pp.block3.cc.antlr.CC3Parser;
import pp.block3.cc.antlr.Type;

public class CC3Test {
	private final ParseTreeWalker walker = new ParseTreeWalker();
	private final CC3 calc = new CC3();

	@Test
	public void test() {
		test(Type.NUM, "10^2");
		test(Type.STR, "abc^2");
		test(Type.NUM, "10+2");
		test(Type.STR, "abc+def");
		test(Type.BOOL, "false+true");
		test(Type.BOOL, "10=10");
		test(Type.BOOL, "abc=cba");
		test(Type.BOOL, "true=false");
		test(Type.ERR,  "abc^abc");
		test(Type.ERR,  "false^2");
		test(Type.ERR,  "abc+false");
		test(Type.ERR,  "abc+2");
		test(Type.ERR,  "valse=false");
	}

	private void test(Type expected, String expr) {
		assertEquals(expected, parseCC3Attr(expr).type);
		ParseTree tree = parseCC3(expr);
		this.calc.init();
		this.walker.walk(this.calc, tree);
		assertEquals(expected, this.calc.val(tree));
	}

	private ParseTree parseCC3(String text) {
		CharStream chars = CharStreams.fromString(text);
		Lexer lexer = new CC3Lexer(chars);
		TokenStream tokens = new CommonTokenStream(lexer);
		CC3Parser parser = new CC3Parser(tokens);
		return parser.t();
	}

	private TContext parseCC3Attr(String text) {
		CharStream chars = CharStreams.fromString(text);
		Lexer lexer = new CC3AttrLexer(chars);
		TokenStream tokens = new CommonTokenStream(lexer);
		CC3AttrParser parser = new CC3AttrParser(tokens);
		return parser.t();
	}
}
