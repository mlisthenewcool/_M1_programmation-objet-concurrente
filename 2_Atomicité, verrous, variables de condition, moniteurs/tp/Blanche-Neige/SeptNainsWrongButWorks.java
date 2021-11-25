// -*- coding: utf-8 -*-

public class SeptNainsWrongButWorks {
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

	static class BlancheNeige {
		private volatile int libre = 1;        // Initialement, Blanche-Neige est libre.
		private volatile int fileAttente = 1;
		public synchronized void requerir() {
			System.out.println("\t" + Thread.currentThread().getName()
							+ " veut un accés exclusif à la ressource");
			((Nain) Thread.currentThread()).position = fileAttente;
			fileAttente++;
		}

		public synchronized void acceder() {
			while(((Nain) Thread.currentThread()).position != libre) { // Le nain s'endort sur l'objet bn
				try { wait(); } catch (InterruptedException e) {e.printStackTrace();}
			}
			System.out.println("\t\t" + Thread.currentThread().getName()
							+ " accède à la ressource.");
		}

		public synchronized void relacher() {
			System.out.println("\t\t\t" + Thread.currentThread().getName()
							+ " relâche la ressource.");
			libre++;
			notifyAll();
		}
	}

	static class Nain extends Thread {
		private static final BlancheNeige bn = new BlancheNeige();
		public int position;
		public Nain(String nom) {
			this.setName(nom);
			this.position = 0;
		}
		public void run() {
			while(true) {
				bn.requerir();
				bn.acceder();
				System.out.println("\t\t" + getName() + " a un accés exclusif à Blanche-Neige.");
				try {sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
				bn.relacher();
			}
			// System.out.println(getName() + " a terminé!");
		}
	}
}
