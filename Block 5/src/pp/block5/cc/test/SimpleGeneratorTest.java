package pp.block5.cc.test;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import pp.block5.cc.ParseException;
import pp.block5.cc.SimplePascalCompiler;
import pp.iloc.Simulator;
import pp.iloc.model.Program;

@SuppressWarnings("javadoc")
public class SimpleGeneratorTest {
	private final static String BASE_DIR = "src/pp/block5/cc/sample";
	private final static String EXT = ".pascal";
	private final SimplePascalCompiler compiler = SimplePascalCompiler.instance();

	@Test
	public void testGCD() throws IOException, ParseException {
		Program prog = compile("gcd");
		String out = sim(prog, "3\n8");
		assertEquals("Greatest common divisor: 1", out.trim());
		out = sim(prog, "435\n1935");
		assertEquals("Greatest common divisor: 15", out.trim());
	}

	@Test
	public void testPrime() throws IOException, ParseException {
		Program prog = compile("prime");
		String out = sim(prog, "365");
		assertEquals("Divisor: 5", out.trim());
		out = sim(prog, "367");
		assertEquals("Is prime 0", out.trim());
	}

	@Test
	public void testGauss() throws IOException, ParseException {
		Program prog = compile("gauss");
		String out = sim(prog, "10");
		assertEquals("Sum: 55", out.trim());
		out = sim(prog, "100");
		assertEquals("Sum: 5050", out.trim());
	}
	@Test 
	public void testGeneratedILOCCode() throws IOException, ParseException {
		String[] files = { "basic"/*, "fib"*/, "gcd", "prime", "gauss" };
		for(String file : files) {
			Program p = compile(file);
			System.out.println(file + ":");
			System.out.println(p);
			System.out.println();
		}
	}

	private Program compile(String filename) throws IOException, ParseException {
		return this.compiler.compile(new File(BASE_DIR, filename + EXT));
	}

	private String sim(Program prog, String input) {
		Simulator sim = new Simulator(prog);
		sim.setIn(new ByteArrayInputStream(input.getBytes()));
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		sim.setOut(out);
		sim.run();
		return out.toString();
	}

	@SuppressWarnings("unused")
	private String simStep(Program prog, String input) {
		Simulator sim = new Simulator(prog);
		sim.setIn(new ByteArrayInputStream(input.getBytes()));
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		sim.setOut(out);
		while (sim.getVM().getPC() < prog.size()) {
			String line = prog.getInstr().get(sim.getVM().getPC()).toString();
			System.out.println(line);
			sim.step();
			for(String reg : allRegisters(line)) {
				System.out.println(reg + ": " + sim.getVM().getReg(reg));
			}
		}
		return out.toString();
	}

	private List<String> allRegisters(String line) {
		List<String> result = new ArrayList<>();
		Matcher m = Pattern.compile("r_[0-9]+").matcher(line);
		while (m.find()) {
			result.add(m.group());
		}
		return result;
	}
}
