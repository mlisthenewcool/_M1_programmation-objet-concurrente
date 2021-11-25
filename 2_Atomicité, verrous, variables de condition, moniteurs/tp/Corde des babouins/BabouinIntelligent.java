// -*- coding: utf-8 -*-

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

enum Cote { EST, OUEST }                       // Le canyon possède un côté EST et un côté OUEST

class BabouinIntelligent extends Thread{
	private static int numeroSuivant = 0;      // Compteur partagé par tous les babouins
	private final int numero;                  // Numéro du babouin
	private Corde corde;                       // Corde utilisée par le babouin
	private Cote origine;                      // Côté du canyon où apparaît le babouin: EST ou OUEST

	BabouinIntelligent
(Corde corde, Cote origine){        // Constructeur de la classe BabouinIntelligent

		this.corde = corde;                    // Chaque babouin peut utiliser la corde
		this.origine = origine;                // Chaque babouin apparaît d'un côté précis du canyon
		numero = ++numeroSuivant;              // Chaque babouin possède un numéro distinct

		this.setName("" + this.numero);
	}

	public void run(){
		System.out.println("Le babouin " + numero + " arrive sur le côté " + origine + " du canyon.");
		corde.saisir(origine);                 // Pour traverser, le babouin saisit la corde
		System.out.println("Le babouin " + numero +
						" commence à traverser sur la corde en partant de l'" + origine + ".");
		try { sleep(5000); } catch(InterruptedException e){} // La traversée ne dure que 5 secondes
		corde.lacher(origine);                 // Arrivé de l'autre côté, le babouin lâche la corde
		System.out.println("Le babouin " + numero + " a lâché la corde et s'en va.");
	}

	public static void main(String[] args){
		final int nbBabouins = 20;
		final BabouinIntelligent
	 babouins[] = new BabouinIntelligent
	[nbBabouins];

		Corde corde = new Corde();                        // La corde relie les deux côtés du canyon
		for (int i = 0; i < nbBabouins; i++){
			try { Thread.sleep(500); } catch(InterruptedException e){}
			if (Math.random() >= 0.5){
				babouins[i] = new BabouinIntelligent
			(corde, Cote.EST); // Création d'un babouin à l'est du canyon
				babouins[i].start();
			} else {
				babouins[i]  = new BabouinIntelligent
			(corde, Cote.OUEST); // Création d'un babouin à l'ouest du canyon
				babouins[i].start();
			}
		} // Une vingtaine de babouins sont répartis sur les deux côtés du canyon

		for(int i = 0; i < nbBabouins; i++) {
			try {
				babouins[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("[MAIN] Tous les babouins ont traversé ! C'est la fin...");
	}
}

class Corde {
	private volatile int nbBabouins = 0;
	private volatile Cote sens = Cote.EST;
	private volatile List<String> fileAttente = Collections.synchronizedList(new ArrayList<String>());
	private volatile List<String> surLaCorde = Collections.synchronizedList(new ArrayList<String>());

	public void showState() {
		System.out.println("[File d'attente] " + this.fileAttente);
		System.out.println("[Sur la corde] " + this.surLaCorde);
	}

	public synchronized void saisir(Cote origine) {
		this.showState();
		this.fileAttente.add(Thread.currentThread().getName());

		// tant que le babouin veut aller dans la direction opposée de la corde
		// et qu'il y a déjà 5 babouins, on attend
		while(this.sens == origine || this.nbBabouins >= 5) {
			try {
				wait();
			} catch(InterruptedException e) {
				System.err.println(e.getMessage());
			}
		}

		this.fileAttente.remove(Thread.currentThread().getName());
		this.surLaCorde.add(Thread.currentThread().getName());

		this.nbBabouins++;
		this.sens = (origine == Cote.EST ? Cote.OUEST : Cote.EST);
	}

	public synchronized void lacher(Cote origine){
		this.showState();

		this.nbBabouins--;

		if(this.nbBabouins == 0)
			this.sens = (this.sens == Cote.EST ? Cote.OUEST : Cote.EST);

		this.surLaCorde.remove(Thread.currentThread().getName());

		this.notifyAll();
	}
}
