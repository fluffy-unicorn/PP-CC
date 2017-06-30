package project.compiler;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
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

	public static final int MAX_THREADS = 4;
	private static final boolean DEBUG = true;
	private static final String EXT_HASKELL = ".hs";
	private static final String EXT_GOOSESPEAK = ".goose";
	private FunctionChecker functionChecker;
	private Checker checker;
	private Generator generator;

	private Program compile(String filename) throws ParseException, IOException {
		functionChecker = new FunctionChecker();
		checker = new Checker();
		generator = new Generator();
		File file = new File(filename); // TODO file not exists error
		ParseTree tree = parse(file);
		List<String> errors = new ArrayList<>();
		print("Starting function checker");
		FunctionTable funcTable = this.functionChecker.check(errors, tree);
		if (handleErrors(errors))
			return null;
		print("Starting checker");
		Result result = this.checker.check(errors, funcTable, tree);
		if (handleErrors(errors))
			return null;
		print("Starting generator");
		Program program = this.generator.generate(tree, result);
		return program;
	}

	private boolean handleErrors(List<String> errors) {
		if (errors.size() > 0) {
			// TODO more beautiful
			print("There were errors in the checker phase");
			for (String error : errors)
				System.err.println(error);
			return true;
		}
		return false;
	}

	public void compileAndWrite(String filename) throws ParseException, IOException {
		compileAndWrite(filename, stripFilename(filename) + EXT_HASKELL);
	}

	public void compileAndWrite(String input, String output) throws ParseException, IOException {
		print("Starting compiler on " + input);
		Program program = compile(input);
		if (program != null)
			try {
				program.writeToFile(output);
				print("Succesfully written to " + output);
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
	}

	private ParseTree parse(File file) throws ParseException, IOException {
		return parse(CharStreams.fromPath(file.toPath()));
	}

	private String stripFilename(String filename) {
		int extIndex = filename.lastIndexOf(EXT_GOOSESPEAK);
		if (extIndex != -1)
			return filename.substring(0, extIndex);
		return filename;
	}

	private ParseTree parse(CharStream chars) throws ParseException {
		Lexer lexer = new GooseSpeakLexer(chars);
		TokenStream tokens = new CommonTokenStream(lexer);
		GooseSpeakParser parser = new GooseSpeakParser(tokens);
		ParseTree result = parser.program();
		return result;
	}

	private void print(String s) {
		if (DEBUG)
			System.out.println(s);
	}
}
