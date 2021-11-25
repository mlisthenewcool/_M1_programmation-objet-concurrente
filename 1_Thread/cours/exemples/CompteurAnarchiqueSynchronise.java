// -*- coding: utf-8 -*-

public class CompteurAnarchiqueSynchronise extends Thread {
	static volatile int valeur = 0;

	public static void main(String[] args) {
		CompteurAnarchiqueSynchronise c1 = new CompteurAnarchiqueSynchronise();
		CompteurAnarchiqueSynchronise c2 = new CompteurAnarchiqueSynchronise();
		c1.start(); c2.start();
		try {
			c1.join(); c2.join();
		} catch(InterruptedException e) {
			e.printStackTrace();
		}

		System.out.println("La valeur finale est " + valeur);
	}

	public void run(){
		for (int i = 1; i <= 10000; i++)
			synchronized (CompteurAnarchiqueSynchronise.class){ valeur++; }
	}
}