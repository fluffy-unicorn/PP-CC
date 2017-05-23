package pp.iloc.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import pp.iloc.Assembler;
import pp.iloc.Simulator;
import pp.iloc.eval.Machine;
import pp.iloc.model.Program;
import pp.iloc.parse.FormatException;

public class CC6Test {
	private final static String BASE_DIR = "src/pp/iloc/test/";

	@Test
	public void assemblerTest() {
		File file = new File(BASE_DIR + "max.iloc");
		try {
			Program p1 = Assembler.instance().assemble(file);
			String print = p1.prettyPrint();
			Program p2 = Assembler.instance().assemble(print);
			assertEquals(p1, p2);

		} catch (FormatException | IOException e) {
			fail(e.getMessage());
		}

	}

	@Test 
	public void simulatorTest() {
		File file = new File(BASE_DIR + "max.iloc");
		Program p = null;
		try {
			p = Assembler.instance().assemble(file);
		} catch (FormatException | IOException e) {
			fail(e.getMessage());
			
		}
		Machine m = new Machine();
		m.init("a", 2,5,7,1,5,3);
		m.setNum("alength", 6);
		new Simulator(p, m).run();
		assertEquals(7, m.getReg("r_max"));
	}

}
