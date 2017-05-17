package pp.block2.cp.ass;

import java.util.Collections;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import nl.utwente.pp.cp.junit.ConcurrentRunner;
import nl.utwente.pp.cp.junit.ThreadNumber;
import nl.utwente.pp.cp.junit.Threaded;
import pp.block2.cp.queue.QueueEmptyException;

@RunWith(ConcurrentRunner.class)
public class MyQueueTest {
	/**
	 * The amount of threads used in each test.
	 */
	private CopyOnWriteArrayList<Integer> producerList = new CopyOnWriteArrayList<Integer>();
	private CopyOnWriteArrayList<Integer> consumerList = new CopyOnWriteArrayList<Integer>();
	private static final int AMOUNT_OF_THREADS = 5;
	private MyQueue<Integer> queue = new MyQueue<Integer>();
	private final Random random = new Random();
	
	@Test
	@Threaded(count = AMOUNT_OF_THREADS * 2)
	public void advancedMultiThreadedTest(@ThreadNumber int theadNumber) throws InterruptedException {
		if (theadNumber < AMOUNT_OF_THREADS) {
			this.producer(theadNumber);
		} else {
			this.consumer(theadNumber - AMOUNT_OF_THREADS);
		}
	}
	

	
	/**
	 * Function which reads integers from the queue and therefore performs the task of a consumer.
	 * @param num The number of this consumer.
	 * @throws InterruptedException If the thread got interrupted.
	 */
	private void consumer(int num) throws InterruptedException {
		int i = 100;
		while (i > 0) {
			Integer read;
			try {
				read = this.queue.pull();
				consumerList.add(read);
				i--;
			} catch (QueueEmptyException e) {
				Thread.sleep(500);
			}
		}
	}

	/**
	 * Function which writes integers to the queue and therefore performs the task of a producer.
	 * @param num The number of this producer.
	 */
	private void producer(int num) {
		for (int i = 0; i < 100; i++) {
			int write = this.random.nextInt();
			producerList.add(write);
			this.queue.push(write);
		}
	}
	
	@After
	public void after() {
		if(producerList.size() != consumerList.size())
			Assert.fail("Lists are not equal sized");
		Collections.sort(producerList);
		Collections.sort(consumerList);
		for(int i = 0; i < producerList.size(); i++) {
			Assert.assertEquals(producerList.get(i), consumerList.get(i));
		}
		
	}
}
