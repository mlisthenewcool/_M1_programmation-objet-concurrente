# Les variables atomiques

- utiliser un verrou privé (donc inutilisable par le reste du programme) limite fortement les risques d'interblocages
	- private final static Object monVerrou = new Object();
- utiliser un entier atomique
	- static AtomicInteger valeur = new AtomicInteger(0);
	- valeur.incrementAndGet();
- AtomicBoolean, AtomicInteger, AtomicLong, AtomicReference
	- get(), set(), getAndSet()
	- assure la visibilité immédiate des opérations réalisées sur l'objet
	- compareAndSet()
		- il faut deviner la valeur courante pour la modifier

# Emploi des méthodes d'interruption d'un thread

- interrupt()
- isInterrupted() renvoie le statut d'interruption sans le modifier
- interrupted() renvoie le statut d'interruption du thread en le mettant à faux s'il été levé
- lorsqu'un thread exécute une instruction bloquante telle que sleep(), join(), wait(), une InterruptionException est levée

- il faut donc veiller à capturer ces exceptions et à réinterrompre le thread, en pensant à bien relâcher les verrous qu'on a pris
