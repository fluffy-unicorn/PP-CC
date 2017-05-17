package pp.block3.cc.symbol;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

public class MySymbolTable implements SymbolTable {

	private Deque<List<String>> scopeStack = new ArrayDeque<>();
	@Override
	public void openScope() {
		scopeStack.push(new ArrayList<String>());
	}

	@Override
	public void closeScope() {
		scopeStack.pop();
	}

	@Override
	public boolean add(String id) {
		List<String> head = scopeStack.peek();
		if (head != null) {
			if(head.contains(id)) {
				return false;
			}
			else {
				return head.add(id);
			}
		} else {
			openScope();
			return add(id);
		}
	}

	@Override
	public boolean contains(String id) {
		Iterator<List<String>> it = scopeStack.iterator();
		while(it.hasNext()) {
			if(it.next().contains(id)) {
				return true;
			}
		}
		return false;
	}

}
