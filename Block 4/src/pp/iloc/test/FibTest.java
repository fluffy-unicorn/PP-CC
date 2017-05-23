package pp.iloc.test;

import static org.junit.Assert.fail;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import pp.iloc.Assembler;
import pp.iloc.Simulator;
import pp.iloc.eval.Machine;
import pp.iloc.model.Program;
import pp.iloc.parse.FormatException;

public class FibTest {
	private final static String BASE_DIR = "src/pp/iloc/test/";

	@Test(timeout = 1000)
	public void RegToRegTest() {
		testRR(55, 10);
		testRR(1836311903,46);
		testRR(-1323752223,47);
	}

	@Test(timeout = 1000)
	public void MemToMemTest() {
		testMM(55, 10);
		testMM(1836311903,46);
		testMM(-1323752223,47);
	}

	private Program assembleProg(String filename) {
		File file = new File(BASE_DIR + filename + ".iloc");
		Program p = null;
		try {
			p = Assembler.instance().assemble(file);
		} catch (FormatException | IOException e) {
			fail(e.getMessage());
		}
		return p;
	}

	private void testRR(int expected, int n) {
		Machine m = new Machine();
		m.init("n", n);
		new Simulator(assembleProg("fibrr"), m).run();
		assertEquals(expected, m.getReg("r_z"));

	}

	private void testMM(int expected, int n) {
		Machine m = new Machine();
		m.init("n", n);
		m.init("x", 0);
		m.init("y", 1);
		int _z = m.init("z", 1);
		new Simulator(assembleProg("fibmm"), m).run();
		assertEquals(expected, m.load(_z));
	}

}
