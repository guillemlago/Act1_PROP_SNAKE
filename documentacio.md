# Multi-Snake

## Exercici 1

Haviem de fer una breu implementació en pddl del mapa A, dels diferents mapes que hi ha com a jocs de prova.

### Domain.pddl

Per a definir el domini del mapa A, ens han de quedar ven definides les regles a aplicar:

- Cada agent pot fer un moviment per torn.
- Els moviments són horitzontals o vérticals.
- Només es pot moure a caselles lliures, respectant les següents normes:
    * La casella no pot ser una paret.
    * La casella no pot ser part del seu rastre.
    * La casella no pot ser part d'un altre agent.
    * Es pot trepitjar la sortida d'un altre agent; en abandonar-la, es converteix en paret i aquell agent ja no podrà sortir.
- Quan un agent abandona la seva casella, l'anterior es torna paret.
- Quan un agent arriba a la seva sortida:
    * L'agent desapareix.
    * La sortida es torna paret.
- El joc s'acaba quan tots els agents surten per les seves respectives sortides.
- El cost de la solució és igual al nombre de moviments empleats pels agents. 

### Problem.ppdl

Per a definir el problema, cal primer definir l'estat inicial dels agents i l'estat del mapa. Definim les caselles lliures, les no definides, no són verdaderes, segons la teoría de predicats.

El mapa en qüestió:

    ########
    a·····A#
    #··#···#
    #B·····b
    ########

- Definim els objectes que interactuen amb el problema.
- Definim els estats inicials dels objectes.
- Definim les adjacencies de les caselles, només horitzonal i verticalment, ja que no es permeten moviments en diagonal.
