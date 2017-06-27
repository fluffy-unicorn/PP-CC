package pp.block6.cc.iloc;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import pp.iloc.Assembler;
import pp.iloc.Simulator;
import pp.iloc.eval.Machine;
import pp.iloc.model.Program;
import pp.iloc.parse.FormatException;

public class Q4Test {
	private static final String BASENAME = "src/pp/block6/cc/iloc/";

	public static void main(String[] args) {
		int[] a = { 5, -1, 3, 5, 10, 15 };
		run(a, 3);
		int[] b = { 7, -5, -3, 0, 2, 10, 12, 17 };
		run(b, 12);
	}

	public static void run(int[] a, int val) {
		Program prog = parse("Question 4.iloc");
		Machine mach = new Machine();
		final int STATIC = 80;
		mach.setNum("a", STATIC);
		int i = 0;
		for (int ai : a) {
			mach.store(ai, STATIC + i);
			i += 4;
		}
		mach.store(val, 0);
		Simulator sim = new Simulator(prog, mach);
		sim.setOut(System.out);
		//int pc;
		while (prog.size() != mach.getPC()) {
			//String in = prog.getInstr().get(pc).toString();
			// System.out.println(prog.getInstr().get(mach.getPC()));
			sim.step();
			// System.out.println(mach);
			// sleep(100);
		}
	}

	public static void sleep(int i) {
		try {
			Thread.sleep(i);
		} catch (InterruptedException e) {
			e.getMessage();
		}

	}

	public static Program parse(String filename) {
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
