package exo2;
// -*- coding: utf-8 -*-

import java.util.Random ;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


public class TriRapide {
    private final static int NB_THREADS = Runtime.getRuntime().availableProcessors();
    private static ExecutorService executeur = Executors.newFixedThreadPool(NB_THREADS) ;
    private static CompletionService<int[]> ecs = new ExecutorCompletionService<int[]>(executeur);
    
    private static final int TAILLE = 1_000_000;
    private final static int TAILLE_MIN_PARALLELE = 1000; // Valeur limite de l'utilisation de la méthode parallèle
    private static final int[] TAB_SEQUENTIEL = new int[TAILLE] ;         // Le tableau d'entiers à trier 
    private static final int[] TAB_PARALLELE = new int[TAILLE] ;         // Le tableau d'entiers à trier 
    private static final int BORNE = 10 * TAILLE; // Valeur maximale dans le tableau
    
    //=======================================================================
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
    // =======================================================================
    private static void echangerElements(int[] t, int m, int n) {
        int temp = t[m] ;
        t[m] = t[n] ;
        t[n] = temp ;
    }
    
    // =======================================================================
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
        if (début < fin) { // S'il y a un seul élément, il n'y a rien à faire!
            int p = partitionner(t, début, fin) ;      
            trierRapidement(t, début, p-1) ;
            trierRapidement(t, p+1, fin) ;
        }
    }

    static class TriParallele implements Callable<int[]> {
    	private int []tableau;
    	private int début, fin, taille_min_partition;
    	
        public TriParallele(int[] tab, int début, int fin, int taille_min_partition) {
        	this.tableau = tab;
        	this.début = début;
        	this.fin = fin;
        	this.taille_min_partition = taille_min_partition;
        }
        
        public int[] call() throws Exception {
        	System.out.println("PARALLELE /// From\t" + this.début + " to\t" + this.fin + " /// Size = " + (this.fin - this.début + 1));
        	trierRapidement(this.tableau, this.début, this.fin) ;      

        	int [] ret = {this.début, p , this.fin};
            return ret;
        }
    	
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        Random alea = new Random() ;
        for (int i=0 ; i<TAILLE ; i++) {                          // Remplissage aléatoire du tableau
        	TAB_SEQUENTIEL[i] = alea.nextInt(2*BORNE) - BORNE ;            
        	TAB_PARALLELE[i] = TAB_SEQUENTIEL[i];					   	  // On fait une copie du tableau pour comparer les deux méthodes
        }
        System.out.print("Tableau initial : ") ;
        afficher(TAB_SEQUENTIEL, 0, TAILLE -1) ;                         // Affiche le tableau à trier

        //////////////////////////////////////
        /////////// TRI SEQUENTIEL ///////////
        //////////////////////////////////////
        System.out.println("/////////// TRI SEQUENTIEL ///////////");
        System.out.println("Démarrage du tri rapide.") ;
        long débutDuTriSeq = System.nanoTime();

        trierRapidement(TAB_SEQUENTIEL, 0, TAILLE-1) ;                   // Tri du tableau

        long finDuTriSeq = System.nanoTime();
        long duréeDuTriSeq = (finDuTriSeq - débutDuTriSeq) / 1_000_000 ;
        System.out.print("Tableau trié de manière séquentielle : ") ; 
        afficher(TAB_SEQUENTIEL, 0, TAILLE -1) ;                         // Affiche le tableau obtenu
        System.out.println("obtenu en " + duréeDuTriSeq + " millisecondes.") ;
        System.out.println();
        ///////////////////////////////////// FIN TRI SEQUENTIEL

        /////////////////////////////////////
        /////////// TRI PARALLELE ///////////
        /////////////////////////////////////
        System.out.println("/////////// TRI PARALLELE ///////////");
        System.out.println("Longueur max d'un tableau a traiter en parallele : " + TAILLE_MIN_PARALLELE);
        System.out.println("Nombre de threads utilisé : " + NB_THREADS);

        System.out.println("Démarrage du tri rapide.") ;
        long débutDuTriPar = System.nanoTime();
        

        // Création du réservoir formé de nbThreads esclaves
        ExecutorService executeur = Executors.newFixedThreadPool(NB_THREADS) ;
        CompletionService<int []> ecs = new ExecutorCompletionService<int []>(executeur);
        TriParallele tache = new TriParallele(0, TAILLE-1);
        ecs.submit(tache);
        
        do {
            Future<int[]> promesse = ecs.take();
            int[] result = promesse.get();

            TriParallele part1 = new TriParallele(result[0], result[1] - 1);
            TriParallele part2 = new TriParallele(result[1] + 1, result[2]);
            
            ecs.submit(part1);
            ecs.submit(part2);
            
/*            
            tache = new TriParallele(result[0], result[1] - 1);
            ecs.submit(tache);

            tache = new TriParallele(result[1] + 1, result[2]);
            ecs.submit(tache);
*/
        } while(ecs.poll() == null);
        
        
//        Future<int []> promesse = ecs.take();
//        int [] result = promesse.get();

        executeur.shutdown(); // Il n'y a plus aucune tâche à soumettre

        // Il faut maintenant attendre la fin des calculs
        try{
            while (! executeur.awaitTermination(1, TimeUnit.SECONDS)) {
                System.out.print("#") ;
            }
        } catch (InterruptedException e) {e.printStackTrace();}
        System.out.println() ;

        long finDuTriPar = System.nanoTime();
        long duréeDuTriPar = (finDuTriPar - débutDuTriPar) / 1_000_000 ;
        System.out.print("Tableau trié de manière parallèle : ") ; 
        afficher(TAB_PARALLELE, 0, TAILLE -1) ;                         // Affiche le tableau obtenu
        System.out.println("obtenu en " + duréeDuTriPar + " millisecondes.") ;
        ///////////////////////////////////// FIN TRI PARALLELE
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
