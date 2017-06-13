package pp.block1.cc.test;

import org.junit.Test;

import pp.block1.cc.antlr.PL1;

public class PL1Test {
	private static LexerTester tester = new LexerTester(PL1.class);

	@Test
	public void simpelTests() {
		tester.correct("\"a12345\"");
		tester.correct("\"\"");
		tester.correct("\"Hoi\"\"\"");
		tester.yields("\"\"\"Hoi\"", PL1.ID);
		tester.correct("\"Hoi\"\"Hoi\"");
		tester.yields("\"\"\"\"", PL1.ID);
		tester.wrong("Hoi");
		tester.wrong("\"Hoi\"\"");
		tester.wrong("\"Hoi\"Hoi\"");
	}
}
