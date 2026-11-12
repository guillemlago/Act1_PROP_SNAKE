package edu.epsevg.prop.ac1.cerca;

import edu.epsevg.prop.ac1.cerca.heuristica.Heuristica;
import edu.epsevg.prop.ac1.cerca.model.Mapa;
import edu.epsevg.prop.ac1.model.Moviment;
import edu.epsevg.prop.ac1.resultat.ResultatCerca;

import java.util.*;

public class CercaAStar extends Cerca {

    private Heuristica heur;

    public CercaAStar(boolean usarLNT, Heuristica heur) {
        super(usarLNT);
        this.heur = heur;
    }


    @Override
    public void ferCerca(Mapa inicial, ResultatCerca rc) {
        // Prioritzem f = g + h. En cas d'empat, preferim la menor h.
        PriorityQueue<NodeAStar> LNO = new PriorityQueue<>(
                Comparator.comparingLong((NodeAStar node) -> (long) node.g + node.h)
                        .thenComparingInt(node -> node.h));
        Map<Mapa, Integer> LNT = new HashMap<>();

        NodeAStar arrel = new NodeAStar(inicial, null, null, 0, 0, heur.h(inicial));
        LNO.add(arrel);
        rc.updateMemoria(LNO.size(), 0);

        while (!LNO.isEmpty()) {
            NodeAStar actual = LNO.remove();

            Integer costTancat = LNT.get(actual.estat);
            if (usarLNT && costTancat != null && costTancat <= actual.g) {
                rc.incNodesTallats();
                rc.updateMemoria(LNO.size(), LNT.size());
                continue;
            }

            rc.incNodesExplorats();
            rc.actualitzaProfunditat(actual.depth);
            rc.actualitzaHeuristica(actual.h);

            // També comprovem l'arrel, que pot ser inabastable des del principi.
            if (actual.h >= Mapa.INF) {
                rc.incNodesTallats();
                rc.updateMemoria(LNO.size(), LNT.size());
                continue;
            }

            if (actual.estat.esFinalPartida()) {
                rc.setSolucio(reconstrueixSolucio(actual));
                rc.updateMemoria(LNO.size(), LNT.size());
                return;
            }

            // Un cost menor permet reobrir un estat explorat anteriorment.
            if (usarLNT) {
                LNT.put(actual.estat, actual.g);
            }

            for (Moviment moviment : actual.estat.getAccionsPossibles()) {
                Mapa estatSeguent = (Mapa) actual.estat.mou(moviment);
                int costSeguent = actual.g + 1;
                costTancat = LNT.get(estatSeguent);

                if (usarLNT && costTancat != null && costTancat <= costSeguent) {
                    rc.incNodesTallats();
                    continue;
                }
                if (!usarLNT && esAAncestre(estatSeguent, actual)) {
                    rc.incNodesTallats();
                    continue;
                }

                int h = heur.h(estatSeguent);
                if (h >= Mapa.INF) {
                    rc.incNodesTallats();
                    continue;
                }

                NodeAStar fill = new NodeAStar(estatSeguent, actual, moviment,
                        actual.depth + 1, costSeguent, h);
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

    /** Conserva h per evitar recalcular-la cada vegada que s'ordena la LNO. */
    private static class NodeAStar extends Node {
        public final int h;

        public NodeAStar(Mapa estat, Node pare, Moviment accio, int depth, int g, int h) {
            super(estat, pare, accio, depth, g);
            this.h = h;
        }
    }

    /**
     * Torna el nom de la cerca.
     * @return el nom de la cerca.
     */
    public String getNomCerca(){
        return "A*("+heur.getNom()+")";
    }
}
