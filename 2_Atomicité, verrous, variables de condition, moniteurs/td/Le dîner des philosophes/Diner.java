//-*- coding: utf-8 -*-

import java.util.Random;
import java.lang.Thread;

public class Diner {
	public static void main(String[] args) {
		final int nombrePhilosophes = 5;

		Fourchette[] fourchettes = new Fourchette[nombrePhilosophes];
		Philosophe[] philosophes = new Philosophe[nombrePhilosophes];

		String[] noms = {"Nietzsche", "Alain", "Sartre", "Aristote", "Cantona"};

		for(int i = 0; i < nombrePhilosophes; i++) {
			fourchettes[i] = new Fourchette(i);
		}

		for(int i = 0; i < nombrePhilosophes; i++) {
			philosophes[i] = new Philosophe(noms[i], fourchettes[i], fourchettes[(i + 1) % nombrePhilosophes]);
		}

		for(int i = 0; i < nombrePhilosophes; i++) {
			philosophes[i].start();
		}
	}
}

class Fourchette {
	private volatile boolean libre = true; // au départ, la fourchette est libre
	private final int identifiant;

	public Fourchette(int identifiant) {
		this.identifiant = identifiant;
	}

	public synchronized void prendre() throws InterruptedException {
		System.out.println("[" + Thread.currentThread().getName() + "] : Je veux la fourchette " + this.identifiant);

		while(!libre) {
			wait();
		}

		libre = false;

		System.out.println("[" + Thread.currentThread().getName() + "] : Je viens de prendre la fourchette " + this.identifiant);
	}

	public synchronized void lacher() {
		System.out.println("[" + Thread.currentThread().getName() + "] : Je lâche la fourchette " + this.identifiant);

		libre = true;
		notifyAll();

		System.out.println("[" + Thread.currentThread().getName() + "] : J'ai lâché la fourchette et notifié tout le monde " + this.identifiant);
	}
}

class Philosophe extends Thread {
	private Fourchette gauche;
	private Fourchette droite;
	//private int nombreDeRepas;

	public Philosophe(String nom, Fourchette gauche, Fourchette droite) {
		this.setName(nom);
		//this.nombreDeRepas = 0;
		this.gauche = gauche;
		this.droite = droite;
	}

	@Override
	public void run() {
		while(true) {
			Random alea = new Random();
			int low = 2000;
			int high = 5000;
			int attente = alea.nextInt(high-low) + low;
			System.out.println("[" + Thread.currentThread().getName() + "] : J'ai une idée, je vais y réfléchir pendant " + attente + " secondes !");
			try {
				sleep(attente);
				this.gauche.prendre();
				this.droite.prendre();
				sleep(3000);
				this.gauche.lacher();
				this.droite.lacher();
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}