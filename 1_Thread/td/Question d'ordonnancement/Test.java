// -*- coding: utf-8 -*-

class Test extends Thread {
	String msg;

	public Test(String s) {
		msg = s;
	}

	public void run() {
	try {
		sleep(1000);
	} catch (InterruptedException e) {e.printStackTrace();};
		System.out.print(msg + " ");
	}

	public static void oldmain(String [] args) {
		new Test("Hello").start();
		new Test("Synchronized").start();
		new Test("World").start();
	}

	public static void main(String [] args) {
		Test t1 = new Test("Hello");
		Test t2 = new Test("Synchronized");
		Test t3 = new Test("World");

		t1.start();
		t2.start();
		t3.start();

	try {
		t1.join();
		t2.join();
		t3.join();
	} catch (InterruptedException e) {
		e.printStackTrace();
	}

	System.out.println();
	}
}