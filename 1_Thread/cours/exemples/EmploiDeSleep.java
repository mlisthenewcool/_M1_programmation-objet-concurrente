// -*- coding: utf-8 -*-

public class EmploiDeSleep extends Thread {
	public static void main(String[] args) {
		new EmploiDeSleep().start();
		new EmploiDeSleep().start();
	}

	public void run() {
		for (int i = 1; i <= 1000; i++) {
			System.out.println(i + " " + currentThread().getName());
			try {
				sleep(100);
			} catch(InterruptedException e) {
				e.printStackTrace();
			} 
		}
	}
}
