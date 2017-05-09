package pp.block2.cc.test;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Lexer;
import org.junit.Test;

import pp.block2.cc.antlr.ArithmeticLexer;
import pp.block2.cc.antlr.Calculator;

public class CalculatorTest {
	private Calculator calc = new Calculator();
	
	@Test
	public void testSimple() {
		areEqual("12","3*4");
		areEqual("6","1+2+3");
		areEqual("0","6-3*2");
		areEqual("10","6--4");
		areEqual("2","6+-4");
		areEqual("81","3^4");
		areEqual("81","3^2^2");
		areEqual("-10","-2*5");
	}
	
	@Test 
	public void testWithBrackets() {
		areEqual("49","(10-3)*7");
		areEqual("3602","((25+5)*2)^(17-(3*5))+2");
		areEqual("-67","-31*2+(5-10)");
	}

	public void areEqual(String answer, String expression) {
		assertEquals(new BigInteger(answer), calc.compute(scan(expression)));
	}
	/** Converts a text into a token stream, using the preset lexer type. */
	private Lexer scan(String text) {
		return new ArithmeticLexer(CharStreams.fromString(text));
	}
}
