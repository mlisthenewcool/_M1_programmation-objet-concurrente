// -*- coding: utf-8 -*-

import java.lang.Math;
import java.lang.Thread;
import java.lang.Runnable;
import java.util.Random;

public class PiSurQuatreParallele extends Thread {
	volatile long tiragesDansLeDisque = 0;
	private long localNbTirages;

	public PiSurQuatreParallele(long localNbTirages) {
		this.localNbTirages = localNbTirages;
	}

	public void run() {
		Random alea = new Random();
		//alea.setSeed(System.currentTimeMillis());
		double x, y;
		for (long i = 0; i < this.localNbTirages; i++) {
			x = alea.nextDouble() ;
			y = alea.nextDouble() ;
			if (x * x + y * y <= 1) tiragesDansLeDisque++;
		}
	}

	public static void main(String[] args) {
		long nbTirages = 100_000_000; // Précision du calcul
		int nbThreads = Runtime.getRuntime().availableProcessors();

		if (args.length > 0) {
			try { nbTirages = 1_000_000 * Integer.parseInt(args[0]); }
			catch(NumberFormatException nfe) {
				System.err.println
					("Usage : java PiSurQuatreParallele <nb de tirages (en millions)>");
				System.err.println(nfe.getMessage());
				System.exit(1);
			}
		}

		System.out.println("Nombre de processeurs disponibles : " + nbThreads);
		System.out.println("Nombre de tirages : " + nbTirages / 1_000_000 + " million(s).") ;
		final long startTime = System.nanoTime();
		final long endTime;

		PiSurQuatreParallele[] threads = new PiSurQuatreParallele[nbThreads];
		// création et lancement des threads
		for(int i = 0; i < nbThreads; i++) {
			threads[i] = new PiSurQuatreParallele(nbTirages/nbThreads);
			threads[i].start();
		}

		// attente de tous les threads
		for(int i = 0; i < nbThreads; i++) {
			try {
				threads[i].join();
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
		}

		// calcul de pi/4 avec le calcul de chacun des threads
		long somme = 0;
		for(int i = 0; i < nbThreads; i++) {
			somme += threads[i].tiragesDansLeDisque;
		}

		double resultat = (double) somme / nbTirages ;
		System.out.format("Estimation de Pi/4: %.9f %n", resultat) ;
		double erreur = 100 * Math.abs(resultat-Math.PI/4)/(Math.PI/4) ;
		System.out.format("Pourcentage d'erreur: %.9f %% %n", erreur);
		endTime = System.nanoTime();
		final long duree = (endTime - startTime) / 1_000_000 ;
		System.out.format("Durée du calcul: %.3f s.%n", (double) duree/1000);
	}
}