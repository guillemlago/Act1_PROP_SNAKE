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
        Queue<Node> LNO = new ArrayDeque<>();
        Set<Mapa> LNT = new HashSet<>();

        Node arrel = new Node(inicial, null, null, 0, 0);
        LNO.add(arrel);
        rc.updateMemoria(LNO.size(), 0);

        while (!LNO.isEmpty()) {
            Node actual = LNO.remove();

            if (usarLNT && LNT.contains(actual.estat)) {
                rc.incNodesTallats();
                rc.updateMemoria(LNO.size(), LNT.size());
                continue;
            }

            rc.incNodesExplorats();
            rc.actualitzaProfunditat(actual.depth);

            if (actual.estat.esFinalPartida()) {
                rc.setSolucio(reconstrueixSolucio(actual));
                rc.updateMemoria(LNO.size(), LNT.size());
                return;
            }

            if (usarLNT) {
                LNT.add(actual.estat);
            }

            for (Moviment moviment : actual.estat.getAccionsPossibles()) {
                Mapa estatSeguent = (Mapa) actual.estat.mou(moviment);

                if (usarLNT && LNT.contains(estatSeguent)) {
                    rc.incNodesTallats();
                    continue;
                }
                if (!usarLNT && esAAncestre(estatSeguent, actual)) {
                    rc.incNodesTallats();
                    continue;
                }

                Node fill = new Node(estatSeguent, actual, moviment,
                        actual.depth + 1, actual.g + 1);
                LNO.add(fill);
            }

            rc.updateMemoria(LNO.size(), LNT.size());
        }
    }

    /** Comprova si l'estat ja apareix a la branca actual. */
    private boolean esAAncestre(Mapa estat, Node node) {
        for (Node actual = node; actual != null; actual = actual.pare) {
            if (actual.estat.equals(estat)) {
                return true;
            }
        }
        return false;
    }

    /** Reconstrueix la seqüència d'accions seguint els nodes pare. */
    private List<Moviment> reconstrueixSolucio(Node node) {
        List<Moviment> solucio = new ArrayList<>();
        for (Node actual = node; actual.pare != null; actual = actual.pare) {
            solucio.add(actual.accio);
        }
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
