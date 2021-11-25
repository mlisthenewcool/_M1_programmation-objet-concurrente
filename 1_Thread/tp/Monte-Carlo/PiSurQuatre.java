// -*- coding: utf-8 -*-

import java.lang.Math;
import java.util.Random;

public class PiSurQuatre {
	public static void main(String[] args) {
		long nbTirages = 100_000_000; // Précision du calcul
		long tiragesDansLeDisque = 0 ;

		if (args.length > 0) {
			try { nbTirages = 1_000_000 * Integer.parseInt(args[0]); }
			catch(NumberFormatException nfe) {
				System.err.println
					("Usage : java PiSurQuatre <nb de tirages (en millions)>");
				System.err.println(nfe.getMessage());
				System.exit(1);
			}
		}

		System.out.println("Nombre de tirages: " + nbTirages / 1_000_000 + " million(s).") ;
		final long startTime = System.nanoTime();
		final long endTime;

		Random alea = new Random();
		alea.setSeed(System.currentTimeMillis());
		double x, y;
		for (long i = 0; i < nbTirages; i++) {
			x = alea.nextDouble() ;
			y = alea.nextDouble() ;
			if (x * x + y * y <= 1) tiragesDansLeDisque++ ;
		}
		double resultat = (double) tiragesDansLeDisque / nbTirages ;
		System.out.format("Estimation de Pi/4: %.9f %n", resultat) ;
		double erreur = 100 * Math.abs(resultat-Math.PI/4)/(Math.PI/4) ;
		System.out.format("Pourcentage d'erreur: %.9f %% %n", erreur);
		endTime = System.nanoTime();
		final long duree = (endTime - startTime) / 1_000_000 ;
		System.out.format("Durée du calcul: %.3f s.%n", (double) duree/1000);
	}
}