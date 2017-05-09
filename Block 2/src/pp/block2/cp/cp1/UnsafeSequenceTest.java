package pp.block2.cp.cp1;

import java.util.Collections;
import java.util.concurrent.CopyOnWriteArrayList;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import nl.utwente.pp.cp.junit.ConcurrentRunner;
import nl.utwente.pp.cp.junit.Threaded;

@RunWith(ConcurrentRunner.class)
public class UnsafeSequenceTest {
	/**
	 * The amount of threads used in each test.
	 */
	private static final boolean SAFE = true;
	private static final int AMOUNT_OF_THREADS = 5;
	private UnsafeSequence seq = new UnsafeSequence();
	private CopyOnWriteArrayList<Integer> hashMap = new CopyOnWriteArrayList<Integer>();

	@Test
	@Threaded(count = AMOUNT_OF_THREADS)
	public void simpelTest() throws InterruptedException {
		if (SAFE)
			hashMap.add(seq.getNextSafe());
		else
			hashMap.add(seq.getNext());
	}

	@After
	public void after() {
		Collections.sort(hashMap);
		for (int i = 0; i < AMOUNT_OF_THREADS; i++) {
			if (hashMap.get(i) != i) {
				System.err.println(hashMap.toString());
				Assert.fail("List is not in increasing order");
			}
		}
		System.out.println(hashMap.toString());
	}
}
