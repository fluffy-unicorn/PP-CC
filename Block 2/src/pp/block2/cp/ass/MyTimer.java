package pp.block2.cp.ass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

public class MyTimer {
	private Timer myTimer = new Timer();
	private List<TimerTask> tasks = new ArrayList<>();
	public Integer[] myInt = { 0 };
	public CopyOnWriteArrayList<Integer> output = new CopyOnWriteArrayList<>();

	public MyTimer(int number_of_tasks) {
		for (int i = 0; i < number_of_tasks; i++) {
			tasks.add(new MyTimerTask(myInt, output));
		}
		for (TimerTask tt : tasks) {
			myTimer.schedule(tt, 0);
		}
	}

	@SuppressWarnings("rawtypes")
	public static boolean isCollectionSorted(List list) {
		List copy = new ArrayList(list);
		Collections.sort(copy);
		return copy.equals(list);
	}

	public static void main(String[] args) throws InterruptedException {
		for (int i = 0; i < 100; i++) {
			MyTimer myTimer = new MyTimer(1000);
			while (true) {
				Thread.sleep(10);
				if (myTimer.output.size() == 1000) {
					System.out.println(isCollectionSorted(myTimer.output));
					break;
				}
			}
		}
	}
}
