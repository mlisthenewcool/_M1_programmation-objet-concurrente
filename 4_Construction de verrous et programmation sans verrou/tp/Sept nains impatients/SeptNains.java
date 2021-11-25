// -*- coding: utf-8 -*-

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SeptNains {
	public static void main(String[] args) {
		final String noms [] = {
			"[Simplet]",
			"[Dormeur]",
			"[Atchoum]",
			"[Joyeux]",
			"[Grincheux]",
			"[Prof]",
			"[Timide]"
		};
		final int nbNains = 7;
		Nain nains[] = new Nain[nbNains];

		// création et lancement des nains
		for(int i = 0; i < nbNains; i++) {
			nains[i] = new Nain(noms[i]);
			nains[i].start();
		}

		// création d'un créancier qui réclame son dû
		// on passe au créancier le moniteur commun aux nains
		Creancier creancier = new Creancier(nains[0].bn);
		creancier.start();

		// attente de 5s. avant d'interrompre chaque nain
		try {
			Thread.sleep(5_000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// interruption des nains, un à un
		for(int i = 0; i < nbNains; i++) {
			nains[i].interrupt();
		}

		// attente de la terminaison de chaque nain, l'un après l'autre
		for(int i = 0; i < nbNains; i++) {
			try {
				nains[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// interruption et attente de la teminaison du créancier
		creancier.interrupt();
		try {
			creancier.join();
		} catch(InterruptedException e) {
			e.printStackTrace();
		}

		// affichage du message final
		System.out.println("[MAIN] Tous les nains ont terminé.");
	}
}

class Creancier extends Thread {
	public BlancheNeige bn;

	public Creancier(BlancheNeige bn) {
		this.bn = bn;
	}

	@Override
	public void run() {
		while(true) {
			try {
				sleep(1400);
				synchronized(this.bn) {
					System.out.println("\n[Créancier] Je suis un créancier très patient, quand l'échance est v'nue, je m'fais payer quoiqu'il arrive\n");
					this.bn.notifyAll();
				}
			} catch(InterruptedException e) {
				break;
			}
		}
	}
}

class Nain extends Thread {
	public static BlancheNeige bn = new BlancheNeige();

	public Nain(String nom) {
		this.setName(nom);
	}

	@Override
	public void run() {
		while(true) {
			try {
				bn.requerir();
				bn.acceder();
				System.out.println(getName() + " accède à Blanche-Neige.");
				sleep(2000);
				System.out.println(getName() + " quitte Blanche-Neige.");
				bn.relacher();
			} catch (InterruptedException e) {
				break ;
			}
		}
		System.out.println(getName() + " s'en va!");
	}

	@Override
	public String toString(){
		// Permet un affichage simple de la liste d'attente lors de l'appel à liste.toString()
		return getName();
	}
}

class BlancheNeige {
	// pour afficher éventuellement le contenu de la liste
	private final boolean verbeux = false;
	public ArrayList<Thread> liste = new ArrayList<Thread>();

	public synchronized void requerir() {
		// c'est simplement s'inscrire à la fin de la liste
		liste.add(Thread.currentThread());
		System.out.println(Thread.currentThread().getName() + " requiert un accès exclusif à la ressource");
		if(verbeux) System.out.println("[REQUERIR]" + liste.toString());
	}

	public synchronized void acceder() throws InterruptedException {
		// Le droit d'accéder à la ressource correspond  au fait d'être le premier dans la liste
		while(liste.get(0) != Thread.currentThread()) {
			long avant = System.currentTimeMillis();
			long temps_attendu = 0;
			// le nain s'endort pour 1s. - le temps qu'il a déjà attendu
			while(temps_attendu < 1000) {
				wait(1000 - temps_attendu);
				// on se retrouve ici après 1s. ou lorsqu'on reçoit un signal depuis notifyAll
				temps_attendu = System.currentTimeMillis() - avant;
				if(temps_attendu < 1000) {
					System.out.println(Thread.currentThread().getName() + " a attendu " + temps_attendu);
				}
			}

			String date = new SimpleDateFormat("HH:mm:ss:SSS").format(new Date(System.currentTimeMillis()));
			System.out.println(Thread.currentThread().getName() + " à " + date + " (" + temps_attendu + ") et alors ?");
		}
		System.out.println(Thread.currentThread().getName() + " obtient le privilège d'accès exclusif à la ressource.");
		if (verbeux) System.out.println("[ACCEDER] " + liste.toString());
	}

	public synchronized void relacher() {
		notifyAll();
		liste.remove(0);
		// Le nain s'efface de la liste: il cède ainsi son privilège au suivant.
		System.out.println(Thread.currentThread().getName() + " relâche son privilège.");
		if (verbeux) System.out.println("[RELACHER]" + liste.toString());
	}
}
