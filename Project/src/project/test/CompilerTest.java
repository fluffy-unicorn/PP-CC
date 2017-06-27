package project.test;

import java.io.IOException;
import java.text.ParseException;
import project.compiler.Compiler;
import org.junit.Test;


public class CompilerTest {
	private final static String BASE_IN_DIR = "src/project/goosespeak/";
	private final static String BASE_OUT_DIR = "/media/UData/development/workspaces/PP/Project/sprockell/src/";
	private final static String EXT_GOOSE = ".goose";
	private final static String EXT_HASKELL = ".hs";
	private final static String TEST = "compilerTest_";
	private final Compiler compiler = new Compiler();
	@Test //(timeout=1000)
	public void testCalcs() throws IOException, ParseException {
//		compile("math");
//		compile("fib");
//		compile("arrays");
//		compile("pointer");
		compile("concurrent");
	}
	
	private void compile(String filename) {
		try {
			compiler.compileAndWrite(BASE_IN_DIR + filename + EXT_GOOSE , BASE_OUT_DIR + TEST + filename + EXT_HASKELL);
		} catch (ParseException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	
}
