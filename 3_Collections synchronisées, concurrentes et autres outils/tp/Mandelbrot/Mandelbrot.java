// -*- coding: utf-8 -*-
package exo1;

import java.awt.Color;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Mandelbrot {
    final static int nbThreads = 4 ;   // nombre de pixels par ligne et par colonne
    final static int taille = 500 ;   // nombre de pixels par ligne et par colonne
    final static Picture image = new Picture(taille, taille) ;
    // Il y a donc taille*taille pixels blancs ou gris à déterminer
    final static int max = 100_000 ; 
    // C'est le nombre maximum d'itérations pour déterminer la couleur d'un pixel
    
    public static void main(String[] args)  {
        final long début = System.nanoTime() ;

        // Création du réservoir formé de nbThreads esclaves
        ExecutorService executeur = Executors.newFixedThreadPool(nbThreads) ;
      
        // Remplissage de la liste des tâches
        TraceLigne[] mesTaches = new TraceLigne[taille] ;
        for (int i = 0; i < taille ; i++){
            mesTaches[i] = new TraceLigne(i);
            executeur.execute(mesTaches[i]);
        }
        executeur.shutdown(); // Il n'y a plus aucune tâche à soumettre
        // Il faut maintenant attendre la fin des calculs
        try{
            while (! executeur.awaitTermination(1, TimeUnit.SECONDS)) {
                System.out.print("#") ;
            }
        } catch (InterruptedException e) {e.printStackTrace();}
        System.out.println() ;
        
        final long fin = System.nanoTime() ;
        final long durée = (fin - début) / 1_000_000 ;
        System.out.println("Durée = " + (double) durée / 1000 + " s.") ;
        image.show() ;
    }    

    // La fonction colorierPixel(i,j) colorie le pixel (i,j) de l'image en gris ou blanc
    public static void colorierPixel(int i, int j) {
        final Color gris = new Color(90, 90, 90) ;
        final Color blanc = new Color(255, 255, 255) ;
        final double xc = -.5 ;
        final double yc = 0 ; // Le point (xc,yc) est le centre de l'image
        final double region = 2 ;
        /*
          La région du plan considérée est un carré de côté égal à 2.
          Elle s'étend donc du point (xc - 1, yc - 1) au point (xc + 1, yc + 1)
          c'est-à-dire du point (-1.5, -1) en bas à gauche au point (0.5, 1) en haut
          à droite
        */
        double a = xc - region/2 + region*i/taille ;
        double b = yc - region/2 + region*j/taille ;
        // Le pixel (i,j) correspond au point (a,b)
        if (mandelbrot(a, b, max)) image.set(i, j, gris) ;
        else image.set(i, j, blanc) ; 
    }

    // La fonction mandelbrot(a, b, max) détermine si le point (a,b) est gris
    public static boolean mandelbrot(double a, double b, int max) {
        double x = 0 ;
        double y = 0 ;
        for (int t = 0; t < max; t++) {
            if (x*x + y*y > 4.0) return false ; // Le point (a,b) est blanc
            double nx = x*x - y*y + a ;
            double ny = 2*x*y + b ;
            x = nx ;
            y = ny ;
        }
        return true ; // Le point (a,b) est gris
    }

    private static class TraceLigne implements Runnable {
    	private int indexLigne;
        public TraceLigne(int index) {
        	this.indexLigne = index;
        }
        
        public void run() {
            for (int i = 0; i < taille; i++) {
                colorierPixel(this.indexLigne, i) ;
            }
            image.show(); // Affiche l'image au fur et à mesure
//            System.out.println("Ligne terminée : " + this.indexLigne);
        }
    	
    }
}



/* 
   $ make
   javac *.java 
   jar cvmf MANIFEST.MF Mandelbrot.jar *.class 
   manifeste ajouté
   ajout : Mandelbrot.class(entrée = 1697) (sortie = 1066)(compression : 37 %)
   ajout : Picture.class(entrée = 5689) (sortie = 3039)(compression : 46 %)
   rm *.class 
   $ java -jar Mandelbrot.jar
   Durée = 35.851 s.
   ^C
*/
