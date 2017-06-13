package pp.block1.cc.test;

import org.junit.Test;

import pp.block1.cc.antlr.LALALI;

public class LALALITest {
	private static LexerTester tester = new LexerTester(LALALI.class);

	@Test
	public void simpelTests() {
		tester.correct("La");
		tester.correct("LaLa");
		tester.correct("LaLaLaLi");
		tester.correct("Laaaaaaaaaa      ");
		tester.correct("Laaaa   Laaa");
		tester.yields("LaaaLaLaa Laaa   LaLiLaa", 
				LALALI.LALA, LALALI.LALALALI, LALALI.LA);
		tester.wrong("LaLaLi");
		tester.wrong("LALALALI");
		tester.wrong("Laa a");
	}
}
