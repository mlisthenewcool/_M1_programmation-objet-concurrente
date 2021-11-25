// -*- coding: utf-8 -*-

import java.util.concurrent.TimeUnit;

public class SeptNainsInterruption {
	public static void main(String[] args) {
		int nbNains = 7;
		String nom [] = {"Simplet", "Dormeur",  "Atchoum", "Joyeux", "Grincheux", "Prof", "Timide"};
		Nain nain [] = new Nain [nbNains];
		for(int i = 0; i < nbNains; i++) nain[i] = new Nain(nom[i]);
		for(int i = 0; i < nbNains; i++) nain[i].start();

		// attendre 5 secondes
		try {
			TimeUnit.SECONDS.sleep(5);
		} catch (InterruptedException e) {
			System.err.println(e.getMessage());
		}

		// interrompre tous les nains
		System.out.println("\n[MAIN] Interruption des nains");
		for(int i = 0; i < nbNains; i++) {
			System.out.println("[MAIN] " + nom[i] + " est interrompu");
			nain[i].interrupt();
		}

		// on attend que tous les nains soient interrompus
		for(int i = 0; i < nbNains; i++) {
			try {
				nain[i].join();
			} catch(InterruptedException e) {
				System.out.println("[" + nom[i] + "] Erreur dans le join : " + e.getMessage());
			}
		}

		System.out.println("[MAIN] C'est la fin...");
	}

	static class BlancheNeige {
		private volatile boolean libre = true; // Initialement, Blanche-Neige est libre.
		public synchronized void requerir() {
			System.out.println("[" + Thread.currentThread().getName()
							+ "] veut un accès exclusif à la ressource");
		}

		public synchronized void acceder() throws InterruptedException {
			while( ! libre ) { // Le nain s'endort sur l'objet bn
				wait();
			}
			libre = false;
			System.out.println("[" + Thread.currentThread().getName()
							+ "] accède à la ressource.");
		}

		public synchronized void relacher() {
			System.out.println("[" + Thread.currentThread().getName()
							+ "] relâche la ressource.");
			libre = true;
			notifyAll();
		}
	}

	static class Nain extends Thread {
		private static final BlancheNeige bn = new BlancheNeige();

		public Nain(String nom) {
			this.setName(nom);
		}

		public void run() {
			// le nain essaye d'accéder à Blanche-Neige tant qu'il n'est pas interrompu
			while(! isInterrupted()) {
				try {
					bn.requerir();

					// accéder peut donner une InterruptedException si un
					// signal d'interruption est reçu pendant l'instruction wait
					bn.acceder();
					System.out.println("[" + getName() + "] a un accès exclusif à Blanche-Neige.");

					try {
						// le nain s'endort
						sleep(1000);
					} catch (InterruptedException e) {
						// s'il est interrompu pendant son sommeil
						// on affiche un message et on le ré-interrompt
						System.out.println("[" + getName() + "] \"Je suis interrompu pendant le SLEEP ! Au revoir !\"");
						interrupt();
					} finally {
						// dans tous les cas
						// le nain doit relacher l'accès à blanche neige
						bn.relacher();
					}
				} catch(InterruptedException e) {
					// on arrive dans cette portion du code uniquement si une
					// Interruption Exception est levée dans accéder()
					System.out.println("[" + getName() + "] \"Je suis interrompu pendant WAIT ! Au revoir !\"");
					interrupt();
				}
			}
			System.out.println("[" + getName() + "] a terminé!");
		}
	}
}
