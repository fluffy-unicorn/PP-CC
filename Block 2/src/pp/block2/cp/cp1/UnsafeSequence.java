package pp.block2.cp.cp1;

public class UnsafeSequence {
	private int value;
	
	public int getNext() {
		return value++;
	}
	
	public synchronized int getNextSafe() {
		return value++;
	}
}
