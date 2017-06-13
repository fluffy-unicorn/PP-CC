package pp.block1.cc.dfa;

public class MyChecker implements Checker {

	@Override
	public boolean accepts(State start, String word) {
		State state = start;
		for (int i = 0; i < word.length(); i++) {
			state = state.getNext(word.charAt(i));
			if (state == null) {
				return false;
			}
		}
		return state.isAccepting();
	}

}
