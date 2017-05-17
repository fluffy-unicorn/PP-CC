package pp.block2.cp.ass;

import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

public class MyTimerTask extends TimerTask {

	private Integer[] i;
	private CopyOnWriteArrayList<Integer> out;
	
	public MyTimerTask(Integer[] i, CopyOnWriteArrayList<Integer> out) {
		this.i = i;
		this.out = out;
	}
	@Override
	public void run() {
		i[0] += 1;
		out.add(i[0]);
	}
}
