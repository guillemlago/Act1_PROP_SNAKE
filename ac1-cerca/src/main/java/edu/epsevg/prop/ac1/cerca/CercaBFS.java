package edu.epsevg.prop.ac1.cerca;

import edu.epsevg.prop.ac1.cerca.model.Mapa;
import edu.epsevg.prop.ac1.model.Moviment;
import edu.epsevg.prop.ac1.resultat.ResultatCerca;

import java.util.*;

public class CercaBFS extends Cerca {

    public CercaBFS(boolean usarLNT) {
        super(usarLNT);
    }

    @Override
    public void ferCerca(Mapa inicial, ResultatCerca rc) {
        // Creem noves llistes per a nodes oberts i tancats. 
        // La llista de nodes oberts és una cua FIFO, i la llista de nodes tancats és un conjunt.
        Queue<Node> LNO = new ArrayDeque<>();
        Set<Mapa> LNT = new HashSet<>();

        // Afegim l'estat inicial a la llista de nodes oberts.
        Node arrel = new Node(inicial, null, null, 0, 0);
        LNO.add(arrel);
        rc.updateMemoria(LNO.size(), 0);

        // Mentre la llista de nodes oberts no estigui buida, anem explorant els nodes.
        while (!LNO.isEmpty()) {
            Node actual = LNO.remove();

            // Si l'estat ja ha estat explorat, el tallem. No volem fer un cicle infinit.
            if (usarLNT && LNT.contains(actual.estat)) {
                rc.incNodesTallats();
                rc.updateMemoria(LNO.size(), LNT.size());
                continue;
            }

            // Actualitzem el nombre de nodes explorats i la profunditat actual.
            rc.incNodesExplorats();
            rc.actualitzaProfunditat(actual.depth);

            // Comprovem si l'estat actual és un estat final. Si ho és, reconstruïm la solució i retornem.
            if (actual.estat.esFinalPartida()) {
                rc.setSolucio(reconstrueixSolucio(actual));
                rc.updateMemoria(LNO.size(), LNT.size());
                return;
            }

            // Afegim l'estat actual a la llista de nodes tancats si estem utilitzant LNT.
            if (usarLNT) {
                LNT.add(actual.estat);
            }

            // Per cada moviment possible, provem possibles estats següents
            for (Moviment moviment : actual.estat.getAccionsPossibles()) {
                Mapa estatSeguent = (Mapa) actual.estat.mou(moviment);
                
                // Si estem usant LNT i l'estat següent probat es troba també a la LNT,
                // no volem recorrer'l 
                if (usarLNT && LNT.contains(estatSeguent)) {
                    rc.incNodesTallats();
                    continue;
                }

                // Si no estem usant la LNT i l'estat següent ja apareix a la branca actual,
                // no volem recorrer-lo tampoc
                if (!usarLNT && esAAncestre(estatSeguent, actual)) {
                    rc.incNodesTallats();
                    continue;
                }

                // Si l'estat següent és vàlid, el creem i l'afegim a la llista de nodes oberts.
                Node fill = new Node(estatSeguent, actual, moviment,
                        actual.depth + 1, actual.g + 1);
                LNO.add(fill);
            }

            // Actualitzem la memòria utilitzada després d'afegir els fills.
            rc.updateMemoria(LNO.size(), LNT.size());
        }
    }

    /** Comprova si l'estat ja apareix a la branca actual. */
    private boolean esAAncestre(Mapa estat, Node node) {

        // Recorrem la branca de nodes pare fins a l'arrel, comprovant si l'estat ja ha aparegut.
        for (Node actual = node; actual != null; actual = actual.pare) {
            if (actual.estat.equals(estat)) {
                return true;
            }
        }
        return false;
    }

    /** Reconstrueix la seqüència d'accions seguint els nodes pare. */
    private List<Moviment> reconstrueixSolucio(Node node) {
        // Creem una llista per a la solució i anem afegint les accions des del node final fins a l'arrel.
        List<Moviment> solucio = new ArrayList<>();
        // Mentre el node actual no sigui l'arrel, anem afegint les accions a la llista de solució.
        for (Node actual = node; actual.pare != null; actual = actual.pare) {
            solucio.add(actual.accio);
        }
        // Com que hem afegit les accions des del node final fins a l'arrel, 
        // cal invertir la llista per obtenir l'ordre correcte.
        Collections.reverse(solucio);
        return solucio;
    }
    
    /**
     * Torna el nom de la cerca.
     * @return el nom de la cerca.
     */
    public String getNomCerca(){
        return "BFS";
    }    
}
