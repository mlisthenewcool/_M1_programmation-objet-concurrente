// -*- coding: utf-8 -*-

public class StartVsRun extends Thread {
	public static void main(String[] args) {
		StartVsRun p1 = new StartVsRun();
		p1.setName("p1");
		StartVsRun p2 = new StartVsRun();
		p2.setName("p2");
		p1.start();
		p2.run(); // Le thread main exécute la méthode run() du thread p2.
	}

	public void run() {
		for (int i = 1; i <= 10; i++)
			System.out.println(i + " " + Thread.currentThread().getName());
	}
}
