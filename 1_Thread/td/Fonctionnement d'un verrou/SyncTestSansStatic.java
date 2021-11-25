// -*- coding: utf-8 -*-

class SyncTestSansStatic extends Thread {
	String msg;
	private Object monVerrou = new Object();

	public SyncTestSansStatic(String s) {
		msg = s;
	}

	public void run() {
		synchronized (monVerrou) {
			System.out.print("[" + msg);
			try { Thread.sleep(1000); }
			catch (InterruptedException e) {e.printStackTrace();}
			System.out.println("]");
		}
	}

	public static void main(String [] args) {
		new SyncTestSansStatic("Hello").start();
		new SyncTestSansStatic("Synchronized").start();
		new SyncTestSansStatic("World").start();
	}
}
