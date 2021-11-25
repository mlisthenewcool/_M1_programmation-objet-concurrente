// -*- coding: utf-8 -*-

public class MonThread extends Thread {
	public static void main(String[] args) {
		new MonThread().start();
		new MonThread().start();
	}
	
	public void run()
	{
		for (int i = 1; i<=50; i++)
			System.out.println(i);
	}
}