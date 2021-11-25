// -*- coding: utf-8 -*-

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Diner {
    public static void main(String args[]) {
        int nbSauvages = 10;               // La tribu comporte 10 sauvages affamés
        int nbPortions = 5;                // Le pôt contient 5 parts, lorsqu'il est rempli
        System.out.println("Il y a " + nbSauvages + " sauvages.");
        System.out.println("Le pôt peut contenir "+ nbPortions + " portions.");
        Pot pot = new Pot(nbPortions);
        new Cuisinier(pot).start();
        for (int i = 0; i < nbSauvages; i++) {
            new Sauvage(pot, i).start();
        }
    } // CE PROGRAMME N'EST PAS SENSÉ TERMINER !
}  

class Sauvage extends Thread {
    public Pot pot;
    public Sauvage(Pot pot, int numéro) {
        this.pot = pot ; 
        this.setName("S" + numéro) ;
    }
    public void run() {
        while (true) {
            try {
                System.out.println(getName() + ": J'ai faim!");
                pot.seServir();
                System.out.println(getName() + ": Je me suis servi et je vais manger!");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                break;
            }; 
        }
    }
}

class Cuisinier extends Thread {
    public Pot pot;
    public Cuisinier(Pot pot) {
        this.pot = pot ;
        this.setName("Cuisinier") ;
    }
    public void run() {
        while(true){
            System.out.println("\t\t\t\t" + getName() + ": Je m’endors.");
            try {
                pot.remplir();
            } catch (InterruptedException e) {
                break;
            }; 
        }
    }
}

class Pot {
    private volatile int nbPortions;
    private final int volume;
    //private ReentrantLock louche = new ReentrantLock(true);
    private boolean louche = true;
    // La louche est libre au départ

    public Pot(int nbPortions) {
		this.volume = nbPortions;
		this.nbPortions = nbPortions;
	}

	synchronized public boolean estVide() {
		return (nbPortions == 0); 
	}

	synchronized public void seServir() throws InterruptedException {
        //louche.lock();
        while(!louche) {wait();}
        louche = false;
        System.out.println("\t" + Thread.currentThread().getName()
                           + ": Je prends la louche.");
		if ( ! estVide() ) {
            System.out.println("\t" + Thread.currentThread().getName() + ": Il y a une part disponible ! ");
        } else { // Le pot est vide: on réveille le cuisinier
			System.out.println("\t" + Thread.currentThread().getName() + ": Le pôt est vide!");
			System.out.println("\t\t" + Thread.currentThread().getName() + ": Je réveille le cuisinier.");
			notifyAll();
			System.out.println("\t\t" + Thread.currentThread().getName() + ": J'attends que le pôt soit plein!");
            while ( estVide() ) { // Tant que le pôt est vide, je ne me sers pas.
                wait();
            }
			System.out.println("\t\t" + Thread.currentThread().getName() + ": Je me réveille! Je me sers.");
        }
		nbPortions--;
        System.out.println("\t" + Thread.currentThread().getName()
                            + ": Je pose la louche.");
        //louche.unlock();
        louche = true;
        notifyAll();
	}
    
	synchronized public void remplir() throws InterruptedException {		
		while( ! estVide() || louche) {                 // Tant que le pôt n'est pas vide, je ne le remplis pas.
			wait();
		}
		System.out.println("\t\t\t\t" + Thread.currentThread().getName()
                           + ": Je suis réveillé et je cuisine...");
        Thread.sleep(2000);
		nbPortions = volume;
		System.out.println("\t\t\t\t" + Thread.currentThread().getName()
                           + ": Le pôt est plein!");
		notifyAll();
	}   
}


/*
  $ java Diner
  Il y a 10 sauvages.
  Le pôt peut contenir 5 portions.
  ................................ Cuisinier: Je m’endors.
  S0: J'ai faim!
  ........S0: Il y a une part disponible ! 
  S0: Je me suis servi et je vais manger!
  S1: J'ai faim!
  ........S1: Il y a une part disponible ! 
  S1: Je me suis servi et je vais manger!
  S2: J'ai faim!
  ........S2: Il y a une part disponible ! 
  S2: Je me suis servi et je vais manger!
  S3: J'ai faim!
  ........S3: Il y a une part disponible ! 
  S3: Je me suis servi et je vais manger!
  S4: J'ai faim!
  S5: J'ai faim!
  S6: J'ai faim!
  ........S4: Il y a une part disponible ! 
  S7: J'ai faim!
  ........S6: Le pôt est vide!
  S8: J'ai faim!
  S4: Je me suis servi et je vais manger!
  ................S6: Je réveille le cuisinier.
  S9: J'ai faim!
  ................S6: J'attends que le pôt soit plein!
  ........S5: Le pôt est vide!
  ................S5: Je réveille le cuisinier.
  ................S5: J'attends que le pôt soit plein!
  ........S9: Le pôt est vide!
  ................S9: Je réveille le cuisinier.
  ................S9: J'attends que le pôt soit plein!
  ................................ Cuisinier: Je suis réveillé et je cuisine...
  S0: J'ai faim!
  S3: J'ai faim!
  S4: J'ai faim!
  S1: J'ai faim!
  S2: J'ai faim!
  ................................ Cuisinier: Le pôt est plein!
  ................................ Cuisinier: Je m’endors.
  ........S8: Il y a une part disponible ! 
  S8: Je me suis servi et je vais manger!
  ........S7: Il y a une part disponible ! 
  S7: Je me suis servi et je vais manger!
  ................S9: Je me réveille! Je me sers.
  S9: Je me suis servi et je vais manger!
  ........S2: Il y a une part disponible ! 
  S2: Je me suis servi et je vais manger!
  ........S1: Il y a une part disponible ! 
  S1: Je me suis servi et je vais manger!
  ........S4: Le pôt est vide!
  ................S4: Je réveille le cuisinier.
  ................S4: J'attends que le pôt soit plein!
  ........S3: Le pôt est vide!
  ................S3: Je réveille le cuisinier.
  ................S3: J'attends que le pôt soit plein!
  ........S0: Le pôt est vide!
  ................S0: Je réveille le cuisinier.
  ................S0: J'attends que le pôt soit plein!
  ................................ Cuisinier: Je suis réveillé et je cuisine...
  S1: J'ai faim!
  S2: J'ai faim!
  S8: J'ai faim!
  S7: J'ai faim!
  S9: J'ai faim!
  ................................ Cuisinier: Le pôt est plein!
  ................................ Cuisinier: Je m’endors.
  ................S4: Je me réveille! Je me sers.
  S4: Je me suis servi et je vais manger!
  ................S3: Je me réveille! Je me sers.
  S3: Je me suis servi et je vais manger!
  ................S5: Je me réveille! Je me sers.
  S5: Je me suis servi et je vais manger!
  ................S6: Je me réveille! Je me sers.
  ^C$
*/
