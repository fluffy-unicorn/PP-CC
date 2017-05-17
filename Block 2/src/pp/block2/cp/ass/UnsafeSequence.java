package pp.block2.cp.ass;

public class UnsafeSequence {
	private int value;
	
	public int getNext() {
		return value++;
	}
	
	public synchronized int getNextSafe() {
		return value++;
	}
}
