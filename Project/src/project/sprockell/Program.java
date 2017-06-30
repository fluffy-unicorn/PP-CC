package project.sprockell;

import project.compiler.Compiler;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A program in SPRIL syntax. The string representation using the function
 * {@link Program#toString()} is a correct Haskell program. The function
 * {@link Program#writeToFile(String)} can be used to write the program to a
 * file in order to be run in a Haskell interpreter.
 */
public class Program {
	private static final String IMPORTS = "import Sprockell\nimport Data.Char\n\n";
	private static final String START = "program = [ ";

	private static final String allPrograms() {
		StringBuilder sb = new StringBuilder("[");
		for (int i = 0; i < Compiler.MAX_THREADS - 1; i++) {
			sb.append("program,");
		}
		sb.append("program]");
		return sb.toString();
	};

	private static final String END = "\n          ]\n\nmain = run "+allPrograms()+"\n\n"
			+ "debug = runWithDebugger (debuggerSimplePrint myShow) "+allPrograms()+"\n\n";
	private static final String BIGSPACE = "          ";
	private static final String TAB = "    ";
	private List<Instruction> instructionList = new ArrayList<>();

	public Program() {

	}

	public void addInstruction(Instruction i) {
		instructionList.add(i);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(IMPORTS);
		sb.append(START);
		int i = 0;
		int pc = 0;
		for (; i < instructionList.size() - 1; i++) {

			Instruction ins = instructionList.get(i);
			if (ins.isComment()) {
				sb.append("{--");
				sb.append(ins.getComment());
				sb.append("--}\n");
				sb.append(BIGSPACE);
				sb.append("  ");
			} else {
				sb.append(instructionList.get(i).toString());
				sb.append(TAB);
				sb.append("-- ");
				sb.append(pc);
				sb.append("\n");
				sb.append(BIGSPACE);
				sb.append(", ");
				pc++;
			}
		}
		sb.append(instructionList.get(i).toString() + TAB + "-- " + pc);
		sb.append(END);
		return sb.toString();
	}

	public void writeToFile(String file) throws IOException {
		String program = this.toString();
		BufferedWriter bw = new BufferedWriter(new FileWriter(file));
		bw.write(program);
		bw.close();
	}
}
