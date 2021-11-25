// -*- coding: utf-8 -*-

public class CompteurAnarchique extends Thread {
	static volatile int valeur = 0;

	public static void main(String[] args) throws Exception {
		CompteurAnarchique Premier = new CompteurAnarchique();
		CompteurAnarchique Second = new CompteurAnarchique();
		Premier.start();
		Second.start();
		Premier.join();
		Second.join();
		System.out.println("La valeur finale est " + valeur);
	}

	public void run(){
		for (int i = 1; i <= 10_000; i++) valeur++;
	}
}