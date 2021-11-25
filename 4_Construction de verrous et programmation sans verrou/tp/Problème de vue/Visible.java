public class Visible {
    public static void main(String[] args) throws Exception {
        A a = new A() ;        // Création d'un objet a de la classe A
        a.start() ;            // Lancement du thread a
        Thread.sleep(1000) ;
        a.valeur = 1 ;         // Modification de l'attribut valeur
        a.fin = true ;         // Modification de l'attribut fin
        System.out.println("Le main a terminé.") ;      
    }  
} 

class A extends Thread {
    public int valeur = 0 ;
    public volatile boolean fin = false ;    
    public void run() {
        while(! fin) ;    // Attente active
        System.out.println(valeur) ;      
    }      
}  

/*
  $ make
  javac *.java
  $ java Visible
  Le main a terminé.
  ^C$ 
*/
