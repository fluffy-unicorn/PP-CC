package project.compiler;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import project.antlr.GooseSpeakLexer;
import project.antlr.GooseSpeakParser;
import project.sprockell.Program;

public class Compiler {

	/** Maximum number of threads */
	public static final int MAX_THREADS = 4;
	/** Show debug messages */
	private static final boolean DEBUG = false;
	/** Default Haskell file extension */
	private static final String EXT_HASKELL = ".hs";
	/** Default GooseSpeak file extension */
	private static final String EXT_GOOSESPEAK = ".goose";
	private FunctionChecker functionChecker;
	private Checker checker;
	private Generator generator;
	private ErrorListener errorListener;

	private Program compile(String filename) throws IOException {
		functionChecker = new FunctionChecker();
		checker = new Checker();
		generator = new Generator();
		errorListener = new ErrorListener();
		File file = new File(filename); 
		debugPrint("Starting lexer and parser");
		ParseTree tree = parse(file);
		List<String> errors = errorListener.getErrors();
		if (handleErrors(errors))
			return null;
		debugPrint("Starting function checker");
		FunctionTable funcTable = this.functionChecker.check(errors, tree);
		if (handleErrors(errors))
			return null;
		debugPrint("Starting checker");
		Result result = this.checker.check(errors, funcTable, tree);
		if (handleErrors(errors))
			return null;
		debugPrint("Starting generator");
		Program program = this.generator.generate(tree, result);
		return program;
	}

	/** 
	 * Print errors to error output.
	 * @param errors The list of errors
	 * @return True when {@code errors.size() > 0}
	 */
	private boolean handleErrors(List<String> errors) {
		if (errors.size() > 0) {
			debugPrint("There were errors during compilation");
			for (String error : errors)
				System.err.println(error);
			return true;
		}
		return false;
	}

	/**
	 * Compile file and write to disk
	 * @param filename Input filename. The output filename will strip the
	 * default GooseSpeak (.goose) extension (if any) and append the default Haskell
	 * (.hs) extension.
	 * @throws IOException
	 */
	public void compileAndWrite(String filename) throws IOException {
		compileAndWrite(filename, stripFilename(filename) + EXT_HASKELL);
	}

	/**
	 * Compile file and write to disk
	 * @param input Input filename
	 * @param output Output filename
	 * @throws IOException
	 */
	public void compileAndWrite(String input, String output) throws IOException {
		debugPrint("Starting compiler on " + input);
		Program program = compile(input);
		if (program != null)
			try {
				program.writeToFile(output);
				debugPrint("Succesfully written to " + output);
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
	}

	/** Parse a file */
	private ParseTree parse(File file) throws IOException {
		return parse(CharStreams.fromPath(file.toPath()));
	}

	/** Parse a character stream */
	private ParseTree parse(CharStream chars) {
		Lexer lexer = new GooseSpeakLexer(chars);
		lexer.removeErrorListeners();
		lexer.addErrorListener(errorListener);
		TokenStream tokens = new CommonTokenStream(lexer);
		GooseSpeakParser parser = new GooseSpeakParser(tokens);
		parser.removeErrorListeners();
		parser.addErrorListener(errorListener);
		ParseTree result = parser.program();
		return result;
	}

	/** Strip the default GooseSpeak (.goose) extension (if any). */
	private String stripFilename(String filename) {
		int extIndex = filename.lastIndexOf(EXT_GOOSESPEAK);
		if (extIndex != -1)
			return filename.substring(0, extIndex);
		return filename;
	}
	
	/** Print only in debug mode */
	private void debugPrint(String s) {
		if (DEBUG)
			System.out.println(s);
	}
}
