package edu.epsevg.prop.ac1.cerca;

import edu.epsevg.prop.ac1.cerca.model.Mapa;
import edu.epsevg.prop.ac1.model.Moviment;
import edu.epsevg.prop.ac1.resultat.ResultatCerca;

import java.util.*;

public class CercaDFS extends Cerca {
    private static final int LIMIT_PER_DEFECTE = 50;

    public CercaDFS(boolean usarLNT) { super(usarLNT); }

    @Override
    public void ferCerca(Mapa inicial, ResultatCerca rc) {
        ferCercaLimitada(inicial, rc, LIMIT_PER_DEFECTE);
    }

    /** Retorna si el límit ha deixat successors pendents d'explorar. */
    boolean ferCercaLimitada(Mapa inicial, ResultatCerca rc, int limit) {
        // La LNO és una pila. La LNT guarda la menor profunditat explorada.
        Deque<Node> LNO = new ArrayDeque<>();
        Map<Mapa, Integer> LNT = new HashMap<>();
        boolean limitAssolit = false;

        LNO.push(new Node(inicial, null, null, 0, 0));
        rc.updateMemoria(LNO.size(), 0);

        while (!LNO.isEmpty()) {
            Node actual = LNO.pop();

            Integer profunditatTancada = LNT.get(actual.estat);
            if (usarLNT && profunditatTancada != null
                    && profunditatTancada <= actual.depth) {
                rc.incNodesTallats();
                rc.updateMemoria(LNO.size(), LNT.size());
                continue;
            }

            rc.incNodesExplorats();
            rc.actualitzaProfunditat(actual.depth);

            if (actual.estat.esFinalPartida()) {
                rc.setSolucio(reconstrueixSolucio(actual));
                rc.updateMemoria(LNO.size(), LNT.size());
                return false;
            }

            if (usarLNT) {
                LNT.put(actual.estat, actual.depth);
            }

            // L'últim successor afegit serà el primer que explorarem.
            for (Moviment moviment : actual.estat.getAccionsPossibles()) {
                Mapa estatSeguent = (Mapa) actual.estat.mou(moviment);
                profunditatTancada = LNT.get(estatSeguent);

                if (usarLNT && profunditatTancada != null
                        && profunditatTancada <= actual.depth + 1) {
                    rc.incNodesTallats();
                    continue;
                }
                if (!usarLNT && esAAncestre(estatSeguent, actual)) {
                    rc.incNodesTallats();
                    continue;
                }

                if (actual.depth == limit) {
                    // No afegim nodes de profunditat superior al límit.
                    limitAssolit = true;
                    rc.incNodesTallats();
                    continue;
                }

                Node fill = new Node(estatSeguent, actual, moviment,
                        actual.depth + 1, actual.g + 1);
                LNO.push(fill);
            }

            rc.updateMemoria(LNO.size(), LNT.size());
        }

        return limitAssolit;
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
        return "DFS";
    }    
}
