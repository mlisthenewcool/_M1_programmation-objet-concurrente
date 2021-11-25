// -*- coding: utf-8 -*-

import java.util.Random ;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class TriRapide {
    final static int nbThreads = 4 ;   // nombre de pixels par ligne et par colonne
    static final int taille = 65_000_000;  	                 // Longueur du tableau à trier
    final static int paralleleMinSubmit = Math.max(1000, taille / 100);   // Valeur limite de l'utilisation de la méthode parrallèle
    static final int [] tableauSeq = new int[taille] ;         // Le tableau d'entiers à trier 
    static final int [] tableauPar = new int[taille] ;         // Le tableau d'entiers à trier 
    static final int borne = 10 * taille ;                  // Valeur maximale dans le tableau

    private static void echangerElements(int[] t, int m, int n) {
        int temp = t[m] ;
        t[m] = t[n] ;
        t[n] = temp ;
    }

    private static int partitionner(int[] t, int début, int fin) {
        int v = t[fin] ;                               // Choix (arbitraire) du pivot : t[fin]
        int place = début ;                            // Place du pivot, à droite des éléments déplacés
        for (int i = début ; i<fin ; i++) {            // Parcours du *reste* du tableau
            if (t[i] < v) {                            // Cette valeur t[i] doit être à droite du pivot
                echangerElements(t, i, place) ;        // On le place à sa place
                place++ ;                              // On met à jour la place du pivot
            }
        }
        echangerElements(t, place, fin) ;              // Placement définitif du pivot
        return place ;
    }

    private static void trierRapidement(int[] t, int début, int fin) {
        if (début < fin) {                             // S'il y a un seul élément, il n'y a rien à faire!
            int p = partitionner(t, début, fin) ;      
            trierRapidement(t, début, p-1) ;
            trierRapidement(t, p+1, fin) ;
        }
    }

    private static void afficher(int[] t, int début, int fin) {
        for (int i = début ; i <= début+3 ; i++) {
            System.out.print(" " + t[i]) ;
        }
        System.out.print("...") ;
        for (int i = fin-3 ; i <= fin ; i++) {
            System.out.print(" " + t[i]) ;
        }
        System.out.print("\n") ;
    }

    static class TriParallele implements Callable<int []> {
    	private int [] tableau;
    	private int début, fin;
    	int p;
    	
        public TriParallele(int début, int fin) {
        	this.tableau = tableauPar;
        	this.début = début;
        	this.fin = fin;
        }
        
        public int [] call() throws Exception {
        	p = partitionner(this.tableau, this.début, this.fin) ;      

        	int [] ret = {this.début, p -1, p +1 , this.fin};
            return ret;
        }
    	
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        Random alea = new Random() ;
        for (int i=0 ; i<taille ; i++) {                          // Remplissage aléatoire du tableau
        	tableauSeq[i] = alea.nextInt(2*borne) - borne ;            
        	tableauPar[i] = tableauSeq[i];					   	  // On fait une copie du tableau pour comparer les deux méthodes
        }
        System.out.print("Tableau initial : ") ;
        afficher(tableauSeq, 0, taille -1) ;                         // Affiche le tableau à trier

        /////////// TRI SEQUENTIEL ///////////
        System.out.println("/////////// TRI SEQUENTIEL ///////////");
        System.out.println("Démarrage du tri rapide.") ;
        long débutDuTriSeq = System.nanoTime();

        trierRapidement(tableauSeq, 0, taille-1) ;                   // Tri du tableau

        long finDuTriSeq = System.nanoTime();
        long duréeDuTriSeq = (finDuTriSeq - débutDuTriSeq) / 1_000_000 ;
        System.out.print("Tableau trié de manière séquentielle : ") ; 
        afficher(tableauSeq, 0, taille -1) ;                         // Affiche le tableau obtenu
        System.out.println("obtenu en " + duréeDuTriSeq + " millisecondes.") ;
        System.out.println();
        // FIN TRI SEQUENTIEL

        /////////// TRI PARALLELE ///////////
        System.out.println("/////////// TRI PARALLELE ///////////");
        System.out.println("Longueur minimale d'un tableau a traiter en parallele : " + paralleleMinSubmit);

        System.out.println("Démarrage du tri rapide.") ;
        long débutDuTriPar = System.nanoTime();
        

        // Création du réservoir formé de nbThreads esclaves
        ExecutorService executeur = Executors.newFixedThreadPool(nbThreads) ;
        CompletionService<int []> ecs = new ExecutorCompletionService<int []>(executeur);

        // Création de la première tache
        TriParallele tache = new TriParallele(0, taille-1);
        ecs.submit(tache);
        int nb_tasks = 1; // Une tâche est déjà crée

        while(nb_tasks != 0){
            Future<int []> promesse = ecs.take();		// Une tâche se termine
            nb_tasks--;									// Une tâche vient d'être terminée

            int [] result = promesse.get();
            
            for(int i = 0; i < 4; i+=2) {
                if(result[i + 1] - result[i] > paralleleMinSubmit) {
//                	System.out.println("PARALLELE /// From\t" + result[i] + " to\t" + result[i + 1] + " /// Size = " + (result[i + 1] - result[i]));
    	            tache = new TriParallele(result[i], result[i + 1]);
    	            promesse = ecs.submit(tache);		// Une tâche est créée
    	            nb_tasks++;							// Une tâche vient d'être créée
                }
                else {									// Utilisation du tri séquentiel pour les sous-tableaux trops "petits"
//                	System.out.println("SEQUENTIEL /// From\t" + result[i] + " to\t" + result[i + 1] + " /// Size = " + (result[i + 1] - result[i]));
                	trierRapidement(tableauPar, result[i], result[i + 1]);
                }            	
            }
        }

        executeur.shutdown(); // Il n'y a plus aucune tâche à soumettre

        long finDuTriPar = System.nanoTime();
        long duréeDuTriPar = (finDuTriPar - débutDuTriPar) / 1_000_000 ;
        System.out.println() ;
        System.out.print("Tableau trié de manière parallèle : ") ; 
        afficher(tableauPar, 0, taille -1) ;                         // Affiche le tableau obtenu
        System.out.println("obtenu en " + duréeDuTriPar + " millisecondes.") ;
        System.out.println();
        // FIN TRI PARALLELE
        
        /////////// TEST TABLEAUX ///////////
        int nb_diff = 0;
        for (int i=0 ; i<taille ; i++) {                          // Remplissage aléatoire du tableau
        	if(tableauSeq[i] != tableauPar[i])
        		nb_diff++;					   	  // On fait une copie du tableau pour comparer les deux méthodes
        }

        System.out.println("/////////// TEST TABLEAUX ///////////");
        System.out.println("Nombre de différence(s) relevée(s) entre les deux tableaux : " + nb_diff);
        // FIN TEST TABLEAUX
    }
}


/*
  $ make
  javac *.java
  $ java TriRapide
  Tableau initial :  4967518 -8221265 -951337 4043143... -4807623 -1976577 -2776352 -6800164
  Démarrage du tri rapide.
  Tableau trié :  -9999981 -9999967 -9999957 -9999910... 9999903 9999914 9999947 9999964
  obtenu en 85 millisecondes.
*/
