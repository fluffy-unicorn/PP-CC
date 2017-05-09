package pp.block1.cc.dfa;

import java.util.ArrayList;
import java.util.List;

public class MyScanner implements Scanner {

	@Override
	public List<String> scan(State dfa, String text) {
		State state = dfa;
		int lastEndIdx = 0;
		int lastStartIdx = 0;
		List<String> result = new ArrayList<String>();
		StringBuilder sb = new StringBuilder(text);
		for (int i = 0; i < text.length(); i++) {
			state = state.getNext(text.charAt(i));
			if (state == null) {
				if (lastEndIdx + 1 - lastStartIdx == 0)
					break;
				char[] buffer = new char[lastEndIdx + 1 - lastStartIdx];
				sb.getChars(lastStartIdx, lastEndIdx + 1, buffer, 0);
				result.add(String.valueOf(buffer));

				state = dfa;
				lastStartIdx = lastEndIdx + 1;
				i = lastEndIdx;
			} else if (state.isAccepting()) {
				lastEndIdx = i;
			}
		}
		if(state != null && state.isAccepting()) {
			char[] buffer = new char[text.length() - lastStartIdx];
			sb.getChars(lastStartIdx, text.length(), buffer, 0);
			result.add(String.valueOf(buffer));
		}
		return result;
	}

}
