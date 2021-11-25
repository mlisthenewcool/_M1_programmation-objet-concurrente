// -*- coding: utf-8 -*-

public class MonThreadPrioriteRelative extends Thread {
	public static void main(String[] args) {
		MonThreadPrioriteRelative p1 = new MonThreadPrioriteRelative();
		p1.setName("p1");
		MonThreadPrioriteRelative p2 = new MonThreadPrioriteRelative();
		p2.setName("p2");
		p1.setPriority(MAX_PRIORITY);
		p2.setPriority(MIN_PRIORITY);
		p1.start();
		p2.start();
	}

	public void run() {
		for (int i = 1; i <= 100; i++)
			System.out.println(i + " " + currentThread().getName());
	}
}