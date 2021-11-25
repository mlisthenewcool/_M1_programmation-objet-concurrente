// -*- coding: utf-8 -*-

public class WaitNotify {
	public static void main(String[] args) {
		Moniteur moniteur = new Moniteur();
		Afficheur afficheur = new Afficheur(moniteur);
		Travailleur travailleur = new Travailleur(moniteur);
		afficheur.start();
		travailleur.start();
	}
}

class Moniteur {
	int data = 0;

	synchronized void initialiser() throws InterruptedException {
		Thread.sleep(1000);
		data = 1;
		notify();
	}

	synchronized int renvoyer() throws InterruptedException {
		wait();
		return data;
	}
}

class Afficheur extends Thread {
	Moniteur moniteur;
	public Afficheur(Moniteur moniteur){
		this.moniteur = moniteur;
	}

	public void run() {
		try {
			System.out.println("Le résultat est " + moniteur.renvoyer());
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
	}
}

class Travailleur extends Thread {
	Moniteur moniteur;

	public Travailleur(Moniteur moniteur) {
		this.moniteur = moniteur;
	}

	public void run() {
		try {
			moniteur.initialiser();
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
	}
}