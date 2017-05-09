package pp.block1.cc.test;
import org.junit.Test;

import pp.block1.cc.antlr.ID6;

public class ID6Test {
	private static LexerTester tester = new LexerTester(ID6.class);
	
	@Test
	public void simpelTests() {
		tester.correct("a12345");
		tester.correct("AbCd54");
		tester.yields("ABDEF1", ID6.ID);
		tester.wrong("1abcde");
		tester.wrong("abcdefg");
	}
	
	@Test
	public void sequenceTests() {
		tester.correct("a12345a12345");
		tester.correct("ABCDEFh99399");
		tester.yields("a12454a12345", ID6.ID, ID6.ID);
		tester.wrong("123456a12345");
		tester.wrong("a12345123456");
	}
}
