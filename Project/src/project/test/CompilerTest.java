package project.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.Test;

import project.antlr.GooseSpeakLexer;
import project.antlr.GooseSpeakParser;
import project.compiler.Checker;
import project.compiler.Compiler;
import project.compiler.ErrorListener;
import project.compiler.FunctionChecker;
import project.compiler.FunctionTable;

public class CompilerTest {
	private final static boolean SkipAutoTest = false;
	private final static String PS = File.separator;
	private final static String BASE_IN_DIR = "src" + PS + "project" + PS + "tests" + PS;
	private final static String BASE_OUT_DIR = "src" + PS + "sprockell" + PS + "src" + PS;
	private final static String EXT_GOOSE = ".goose";
	private final static String EXT_HASKELL = ".hs";
	private final Compiler compiler = new Compiler();
	private final FunctionChecker functionChecker = new FunctionChecker();
	private final Checker checker = new Checker();
	private final ErrorListener errorListener = new ErrorListener();

	@Test
	public void functionCheckerTest() {
		functionCheckerTest("Test1", true);
		functionCheckerTest("Test2", false);
		functionCheckerTest("Test3", false);
		functionCheckerTest("Test4", true);
		functionCheckerTest("Test5", false);
		functionCheckerTest("Test6", false);
	}

	@Test
	public void checkerTest() {
		checkerTest("Test1", true);
		checkerTest("Test2", true);
		checkerTest("Test3", false);
		checkerTest("Test4", false);
		checkerTest("Test5", true);
		checkerTest("Test6", false);
		checkerTest("Test7", true);
		checkerTest("Test8", false);
		checkerTest("Test9", false);
		checkerTest("Test10", true);
		checkerTest("Test11", false);
		checkerTest("Test12", true);
		checkerTest("Test13", false);
		checkerTest("Test14", false);
		checkerTest("Test15", true);
		checkerTest("Test16", true);
		checkerTest("Test17", false);
		checkerTest("Test18", false);
	}

	@Test
	public void codeGeneratorTest() {
		codeGeneratorTest(Directory.FunctionChecker, "Test1", false);
		codeGeneratorTest(Directory.FunctionChecker, "Test4", false);
		codeGeneratorTest(Directory.Checker, "Test1", false);
		codeGeneratorTest(Directory.Checker, "Test2", false);
		codeGeneratorTest(Directory.Checker, "Test5", false);
		codeGeneratorTest(Directory.Checker, "Test7", false);
		codeGeneratorTest(Directory.Checker, "Test10", false);
		codeGeneratorTest(Directory.Checker, "Test12", false);
		codeGeneratorTest(Directory.Checker, "Test15", false);
		codeGeneratorTest(Directory.Checker, "Test16", false);
		codeGeneratorTest(Directory.CodeGenerator, "arrays", false);
		codeGeneratorTest(Directory.CodeGenerator, "boolean", false);
		codeGeneratorTest(Directory.CodeGenerator, "concurrent", false);
		codeGeneratorTest(Directory.CodeGenerator, "fib", true);
		codeGeneratorTest(Directory.CodeGenerator, "maths", false);
		codeGeneratorTest(Directory.CodeGenerator, "pointer", false);
	}

	/** A test helper method for function checker */
	private void functionCheckerTest(String filename, boolean pass) {
		File file = new File(BASE_IN_DIR + Directory.FunctionChecker + PS + filename + EXT_GOOSE);
		ParseTree tree = null;
		try {
			tree = parse(file);
		} catch (IOException e) {
			// Should not occur
			fail(e.getMessage());
		}
		List<String> errors = new ArrayList<>();
		this.functionChecker.check(errors, tree);
		assertEquals(pass, errors.size() == 0);
	}

	/** A test helper method for checker */
	private void checkerTest(String filename, boolean pass) {
		File file = new File(BASE_IN_DIR + Directory.Checker + PS + filename + EXT_GOOSE);
		ParseTree tree = null;
		try {
			tree = parse(file);
		} catch (IOException e) {
			// Should not occur
			fail(e.getMessage());
		}
		List<String> errors = new ArrayList<>();
		FunctionTable functionTable = this.functionChecker.check(errors, tree);
		this.checker.check(errors, functionTable, tree);
		assertEquals(filename, pass, errors.size() == 0);
	}

	private void codeGeneratorTest(Directory dir, String filename, boolean hasIn) {
		String generatedFile = compile(dir, filename);
		if(SkipAutoTest)
			return;
		try {
			System.out.println("Automatically testing " + generatedFile);
			String outFile = BASE_IN_DIR + dir + PS + filename + ".out";
			String inFile = BASE_IN_DIR + dir + PS + filename + ".in";
			ProcessBuilder pb = new ProcessBuilder("ghc", "-e", "main", generatedFile);
			pb.directory(new File(BASE_OUT_DIR));
			if(hasIn)
				pb.redirectInput(Paths.get(inFile).toFile());
			Process p = pb.start();
			List<String> stdOut = getInputAsString(p.getInputStream());
			List<String> stdErr = getInputAsString(p.getErrorStream());
			assertTrue(stdErr.size() == 0);
			assertEquals(toStringList(outFile), stdOut);
		} catch (IOException e) {
			// do nothing
		}
	}

	/** Compile and write to a file */
	private String compile(Directory d, String filename) {
		String input = BASE_IN_DIR + d + PS + filename + EXT_GOOSE;
		String output = d + "_" + filename + EXT_HASKELL;
		try {
			compiler.compileAndWrite(input, BASE_OUT_DIR + output);
		} catch (IOException e) {
			// Should not occur
			fail(e.getMessage());
		}
		return output;
	}

	/**
	 * Parses a file
	 * 
	 * @throws IOException
	 */
	private ParseTree parse(File file) throws IOException {
		return parse(CharStreams.fromPath(file.toPath()));
	}

	/**
	 * Parses an character stream. It will fail JUnit tests when either the
	 * lexer or the parser has any errors.
	 * 
	 * @param chars
	 *            The character stream
	 * @return A parse tree to be used in the compiler
	 * @throws ParseException
	 */
	private ParseTree parse(CharStream chars) {
		Lexer lexer = new GooseSpeakLexer(chars);
		lexer.removeErrorListeners();
		lexer.addErrorListener(errorListener);
		assertFalse(errorListener.hasErrors());
		TokenStream tokens = new CommonTokenStream(lexer);
		GooseSpeakParser parser = new GooseSpeakParser(tokens);
		parser.removeErrorListeners();
		parser.addErrorListener(errorListener);
		assertFalse(errorListener.hasErrors());
		ParseTree result = parser.program();
		return result;
	}

	private enum Directory {
		CodeGenerator, Checker, FunctionChecker
	}

	private List<String> getInputAsString(InputStream is) {
		List<String> result = new ArrayList<>();
		try (Scanner s = new Scanner(is)) {
			while (s.hasNextLine())
				result.add(s.nextLine());
		}
		return result;
	}

	private List<String> toStringList(String filename) {
		try {
			return Files.readAllLines(Paths.get(filename));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}
