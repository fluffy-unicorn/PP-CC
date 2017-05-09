package pp.block2.cp.cp1;

import java.util.LinkedList;

import pp.block2.cp.queue.Queue;
import pp.block2.cp.queue.QueueEmptyException;

public class MyQueue<T> implements Queue<T> {
	private LinkedList<T> list = new LinkedList<T>();

	@Override
	public synchronized void push(T x) {
		list.add(x);
	}

	@Override
	public synchronized T pull() throws QueueEmptyException {
		if (list.isEmpty())
			throw new QueueEmptyException();
		else
			return list.removeFirst();
	}

	@Override
	public int getLength() {
		return list.size();
	}
}