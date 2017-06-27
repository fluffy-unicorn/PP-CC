package project.sprockell;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Wrapper for SPRIL instruction
 */
public class Instruction {
	/** The different instruction types */
	public enum InstructionType {
		Compute, Jump, Branch, Load, Store, Push, Pop, ReadInstr, Receive, WriteInstr, TestAndSet, EndProg, Nop, Debug, Comment;
	}
	private List<SPRILType> values = new ArrayList<>();
	private InstructionType type;
	
	public Instruction(InstructionType t, SPRILType... ts) {
		type = t;
		values.addAll(Arrays.asList(ts));
	}

	private static final char SPACE = ' ';

	public boolean isComment() {
		return InstructionType.Comment.equals(type);
	}
	
	public String getComment() {
		return values.get(0).toString();
	}
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(type);
		sb.append(SPACE);
		for (int i = 0; i < values.size(); i++) {
			if(values.get(i) == null) {
				continue;
			}
			sb.append(values.get(i).toString());
			if (i < values.size() - 1)
				sb.append(SPACE);
		}

		return sb.toString();
	}
}
