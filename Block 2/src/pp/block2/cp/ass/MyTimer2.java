package pp.block2.cp.ass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

public class MyTimer2 {
	private List<Timer> timers = new ArrayList<>();
	private List<TimerTask> tasks = new ArrayList<>();
	public Integer[] myInt = { 0 };
	public CopyOnWriteArrayList<Integer> output = new CopyOnWriteArrayList<>();

	public MyTimer2(int number_of_timers) {
		for (int i = 0; i < number_of_timers; i++) {
			TimerTask task = new MyTimerTask(myInt, output);
			tasks.add(task);
			Timer timer = new Timer();
			timer.schedule(task, 0);
			timers.add(new Timer());

		}
	}

	@SuppressWarnings("rawtypes")
	public static boolean isCollectionSorted(List list) {
		List copy = new ArrayList(list);
		Collections.sort(copy);
		return copy.equals(list);
	}

	public static void main(String[] args) throws InterruptedException {
		boolean allTrue = true;
		for (int i = 0; i < 100; i++) {
			MyTimer2 myTimer = new MyTimer2(1000);
			while (true) {
				Thread.sleep(10);
				if (myTimer.output.size() == 1000) {
					if(!allTrue)
						break;
					allTrue = isCollectionSorted(myTimer.output) ? true : false;
					break;
				}
			}
		}
		System.out.print(allTrue);
		System.exit(0);
	}
}
