package pp.block6.cc.iloc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import org.junit.Test;

import pp.iloc.Assembler;
import pp.iloc.Simulator;
import pp.iloc.model.Program;
import pp.iloc.parse.FormatException;

public class FibIlocTest {
	private static final String BASENAME = "src/pp/block6/cc/iloc/";

	@Test
	public void test() {
		test(89, "10\n");
		test(987, "15\n");
		test(1346269, "30\n");
	}

	private void test(int outcome, String input) {
		assertEquals(outcome, run(input));
	}

	private int run(String input) {
		Program prog = parse("fib.iloc");
		Simulator sim = new Simulator(prog);
		sim.setIn(new ByteArrayInputStream(input.getBytes()));
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		sim.setOut(out);
		sim.run();
		try (Scanner scan = new Scanner(out.toString())) {
			return Integer.parseInt(scan.findInLine("[\\d-]+"));
		}
	}

	private Program parse(String filename) {
		File file = new File(filename);
		if (!file.exists()) {
			file = new File(BASENAME + filename);
		}
		try {
			return Assembler.instance().assemble(file);
		} catch (FormatException | IOException e) {
			fail(e.getMessage());
			return null;
		}
	}
}
