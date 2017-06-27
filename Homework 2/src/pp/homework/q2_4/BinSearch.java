package pp.homework.q2_4;

public class BinSearch {
	private static int[] a;

	public static void main(String[] args) {
		int length = args.length - 1;
		a = new int[length];
		for (int i = 0; i < a.length; i++) {
			a[i] = Integer.parseInt(args[i]);
			if (i > 0 && a[i] < a[i - 1]) {
				System.err.println("Input array is not sorted");
			}
		}
		depth(Integer.parseInt(args[length]));
	}

	public static void depth(int val) {
		System.out.println("Depth: " + depth3(val, 0, a.length));
	}

	public static int depth3(int val, int low, int high) {
		int result;
		if (high < low) {
			result = -1;
		} else {
			int mid = (low + high) / 2;
			if (a[mid] == val) {
				result = 1;
			} else if (a[mid] < val) {
				result = depth3(val, mid + 1, high) + 1;
			} else {
				result = depth3(val, low, mid - 1) + 1;
			}
		}
		return result;
	}
}
