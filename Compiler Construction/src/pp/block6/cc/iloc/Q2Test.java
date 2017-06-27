package pp.block6.cc.iloc;

import java.io.File;
import java.io.IOException;

import pp.iloc.Assembler;
import pp.iloc.Simulator;
import pp.iloc.eval.Machine;
import pp.iloc.model.Program;
import pp.iloc.parse.FormatException;

public class Q2Test {
	private static final String BASENAME = "src/pp/block6/cc/iloc/";

	public static void main(String[] args) {
		run("q2_1.iloc");
		run("q2_2.iloc");
	}

	public static void run(String file) {
		Program prog = parse(file);
		Machine mach = new Machine();
		int p = mach.init("p",0);
		mach.init("a", 100);
		mach.init("af", 5);
		mach.init("b",-500);
		mach.init("bf", 4);
		Simulator sim = new Simulator(prog, mach);
		sim.run();
		System.out.println("P = " + mach.load(p));
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
			e.printStackTrace();
			return null;
		}
	}

}
