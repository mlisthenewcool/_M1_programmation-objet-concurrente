// -*- coding: utf-8 -*-

public class MonRunnable implements Runnable {
	public static void main(String[] args) {
		new Thread(new MonRunnable()).start();
		new Thread(new MonRunnable()).start();
	}

	public void run()
	{
		for (int i = 1; i<= 1000; i++)
			System.out.println(i);
	}
}