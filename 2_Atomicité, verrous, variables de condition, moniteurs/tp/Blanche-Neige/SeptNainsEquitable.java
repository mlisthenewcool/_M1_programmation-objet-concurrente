//-*- coding: utf-8 -*-

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SeptNainsEquitable {
	public static void main(String[] args) {
		int nbNains = 7;
		String nom [] = {"Simplet", "Dormeur",  "Atchoum", "Joyeux", "Grincheux", "Prof", "Timide"};
		Nain nain [] = new Nain [nbNains];
		for(int i = 0; i < nbNains; i++) nain[i] = new Nain(nom[i]);
		for(int i = 0; i < nbNains; i++) nain[i].start();
		for(int i = 0; i < nbNains; i++) {
			try { nain[i].join(); } catch (InterruptedException e) {e.printStackTrace();}
		}
		// System.out.println("C'est fini.");
	}

	static class Nain extends Thread {
		private static final BlancheNeige bn = new BlancheNeige();
		private int nbAcces;
		public Nain(String nom) {
			this.setName(nom);
			this.nbAcces = 0;
		}
		public void run() {
			while(true) {
				bn.requerir();
				bn.acceder();
				this.nbAcces++;
				System.out.println("\t\t" + getName() + " a un accès exclusif à Blanche-Neige (" + this.nbAcces + ").");
				try {sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
				bn.relacher();
			}
			// System.out.println(getName() + " a terminé!");
		}
	}

	static class BlancheNeige {
		private volatile boolean libre = true;        // Initialement, Blanche-Neige est libre.
		private volatile List<String> fileAttente = Collections.synchronizedList(new ArrayList<String>());
		public synchronized void requerir() {
			System.out.println("\t" + Thread.currentThread().getName()
					+ " veut un accès exclusif à la ressource");

			fileAttente.add(Thread.currentThread().getName());

			System.out.println("\t" + Thread.currentThread().getName()
					+ " s'ajoute à la liste d'attente, en position " + fileAttente.indexOf(Thread.currentThread().getName()));
		}

		public synchronized void acceder() {
			while( ! libre || fileAttente.indexOf(Thread.currentThread().getName()) != 0 ) { // Le nain s'endort sur l'objet bn
				try { wait(); } catch (InterruptedException e) {e.printStackTrace();}
			}
			libre = false;
			System.out.println("\t\t" + Thread.currentThread().getName()
							+ " accède à la ressource.");
		}

		public synchronized void relacher() {
			System.out.println("\t\t\t" + Thread.currentThread().getName()
							+ " relâche la ressource.");

			fileAttente.remove(0);
			System.out.println("\t\t\t" + Thread.currentThread().getName()
					+ " libère sa place dans la file d'attente.");

			System.out.println("\t\t\t" + "Les prochains nains dans la file d'attente sont :");

			for(int i = 0; i < fileAttente.size(); i++)
				System.out.println("\t\t\t- " + fileAttente.get(i));

			libre = true;
			notifyAll();
		}
	}
}