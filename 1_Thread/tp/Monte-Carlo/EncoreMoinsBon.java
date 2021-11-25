// -*- coding: utf-8 -*-

import java.lang.Math;
import java.util.Random;
import java.lang.Thread;
import java.lang.Runtime;

public class EncoreMoinsBon extends Thread {
    static long nbTirages = 5_000_000;          // Précision du calcul, fixée à 5 000 000
    volatile long tiragesDansLeDisque = 0;      // Nb de tirages dans le disque pour chaque thread
    static int nbThreads = 4;                   // Nb de threads utilisés
    long part;

    static Random alea = new Random();

    public static void main (String args[]) {
        if (args.length>0) {
            try { nbTirages = 1_000_000 * Integer.parseInt(args[0]); }
            catch(NumberFormatException nfe) {
                System.err.println
                    ("Usage : java EncoreMoinsBon <nb de tirages>");
                System.err.println(nfe.getMessage());
                System.exit(1);
            }
        }

        System.out.println("Nombre de tirages: " + nbTirages/1_000_000 + " million(s).") ;
		final long startTime = System.nanoTime();
		final long endTime;

        EncoreMoinsBon[] T = new EncoreMoinsBon[nbThreads];
        for(int i=0; i<nbThreads; i++){
            T[i] = new EncoreMoinsBon(nbTirages / nbThreads);
            T[i].start();
        }
        for(int i=0; i<nbThreads; i++){
            try{ T[i].join(); } catch(InterruptedException e){e.printStackTrace();}
        }
        int somme = 0;
        for(int i=0; i<nbThreads; i++) {
            somme += T[i].tiragesDansLeDisque;
        }
        double resultat = (double) somme / nbTirages ;
        System.out.format("Estimation de Pi/4: %.9f %n", resultat) ;
        double erreur = 100 * Math.abs(resultat-Math.PI/4)/(Math.PI/4) ;
        System.out.format("Pourcentage d'erreur: %.9f %% %n", erreur);
		endTime = System.nanoTime();
		final long duree = (endTime - startTime) / 1_000_000 ;
		System.out.format("Durée du calcul: %.3f s.%n", (double) duree/1000);
		System.out.println("Nb de processeurs: " + Runtime.getRuntime().availableProcessors());
    }

    public EncoreMoinsBon (long part){
        this.part = part;
    }

    public void run(){
        double x, y;
        for(long i = 0; i <= part; i++){
            x = alea.nextDouble() ;
            y = alea.nextDouble() ;
            if (x * x + y * y <= 1) tiragesDansLeDisque++ ;
        }
    }
}


/*
   $ make
   javac -encoding UTF-8 *.java
   $ java EncoreMoinsBon 500
   Nombre de tirages: 5 million(s).
   Estimation de Pi/4: 0,785473000
   Pourcentage d'erreur: 0,009528492 %
   Durée du calcul: 2,356 s.
   Nb de processeurs: 8
   $ java EncoreMoinsBon 500
   Nombre de tirages: 500 million(s).
   Estimation de Pi/4: 0,785426048
   Pourcentage d'erreur: 0,003550378 %
   Durée du calcul: 223,733 s.
   Nb de processeurs: 8

*/
