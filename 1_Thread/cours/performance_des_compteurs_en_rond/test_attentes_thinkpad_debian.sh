#!/bin/bash
MACHINE="thinkpad_debian" # Pas d'espaces ici, merci
VALEURCIBLE=2520
DONNEES="donnees_"$MACHINE
DATE=`date "+DATE: %Y-%m-%d%tTIME: %H:%M:%S"`

echo "Tests d'attentes sur "$MACHINE" "$DATE

# Je crée le répertoire dans lequel seront placés les résultats
if [ ! -e $DONNEES ]
then
    echo "Creation du repertoire "$DONNEES
    mkdir $DONNEES
fi
# Je sauvegarde les informations de la machine
if [ -e /proc/cpuinfo ]
then
    cat /proc/cpuinfo > $DONNEES/info.txt
fi
if [ -e /usr/sbin/sysctl ]
then
    sysctl hw.availcpu > $DONNEES/info.txt
fi

# Je compile si c'est pas fait
if [ ! -e Compteur.class ]
then
    javac Compteur.java
fi

for MODE in AAN AAY AP ATP
do
    TEST="donnees_"$MODE".dat"
    echo "# Tests d'attentes sur "$MACHINE" "$DATE > $DONNEES/$TEST
    echo -n "MODE : "$MODE" - "
    for NBCOMPTEUR in 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16
    do
	echo -n " "$NBCOMPTEUR
	java Compteur $NBCOMPTEUR $VALEURCIBLE $MODE | grep "#" >> $DONNEES/$TEST
    done
    echo " (fini)"
done

#touch test_attentes_thinkpad_debian.gp
cp test_attentes_thinkpad_debian.gp $DONNEES
echo "Lancez \"gnuplot test_attentes_thinkpad_debian.gp\" pour fabriquer les graphiques."
