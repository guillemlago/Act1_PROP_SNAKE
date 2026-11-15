package edu.epsevg.prop.ac1.cerca;

import edu.epsevg.prop.ac1.cerca.model.Mapa;
import edu.epsevg.prop.ac1.model.Direccio;
import edu.epsevg.prop.ac1.model.Moviment;
import edu.epsevg.prop.ac1.model.Posicio;
import edu.epsevg.prop.ac1.resultat.ResultatCerca;

import java.util.*;

/**
 * Repte: backtracking de camins disjunts, guiats per distàncies BFS.
 * Completem els agents en ordre d'identificador per evitar intercalacions equivalents.
 * Explora camins alternatius, però retorna la primera solució: no garanteix l'òptim.
 */
public class CercaCamins extends Cerca {

    public CercaCamins() {
        super(false);
    }

    @Override
    public void ferCerca(Mapa inicial, ResultatCerca rc) {
        Deque<Node> LNO = new ArrayDeque<>();
        LNO.push(new Node(inicial, null, null, 0, 0));
        rc.updateMemoria(LNO.size(), 0);

        while (!LNO.isEmpty()) {
            Node actual = LNO.pop();
            rc.incNodesExplorats();
            rc.actualitzaProfunditat(actual.depth);

            if (actual.estat.esFinalPartida()) {
                rc.setSolucio(reconstrueixSolucio(actual));
                rc.updateMemoria(LNO.size(), 0);
                return;
            }

            // Primer agent pendent. No canviem d'agent fins que arribi a la sortida.
            int agent = 0;
            while (!actual.estat.esAgentActiu(agent)) agent++;

            int[][] distancies = null;
            boolean bloquejat = false;
            for (int id = agent; id < actual.estat.getAgents().size(); id++) {
                if (!actual.estat.esAgentActiu(id)) continue;
                int[][] distanciesAgent = distanciesFinsSortida(actual.estat, id);
                Posicio origen = actual.estat.getAgents().get(id);
                if (distanciesAgent[origen.x][origen.y] == Mapa.INF) {
                    bloquejat = true;
                    break;
                }
                if (id == agent) distancies = distanciesAgent;
            }

            // Les parets només augmenten: una sortida inabastable no es recuperarà.
            if (bloquejat) {
                rc.incNodesTallats();
                rc.updateMemoria(LNO.size(), 0);
                continue;
            }

            Posicio origen = actual.estat.getAgents().get(agent);
            List<Moviment> moviments = new ArrayList<>();
            for (Moviment moviment : actual.estat.getAccionsPossibles()) {
                if (moviment.getAgentId() != agent) continue;
                Posicio desti = origen.translate(moviment.getDireccio());
                if (distancies[desti.x][desti.y] == Mapa.INF) {
                    rc.incNodesTallats();
                } else {
                    moviments.add(moviment);
                }
            }

            // Apilem en ordre invers perquè el pas més proper a la sortida surti primer.
            final int[][] distanciesActuals = distancies;
            moviments.sort(Comparator.comparingInt(moviment -> {
                Posicio desti = origen.translate(moviment.getDireccio());
                return distanciesActuals[desti.x][desti.y];
            }));
            for (int i = moviments.size() - 1; i >= 0; i--) {
                Moviment moviment = moviments.get(i);
                Mapa seguent = (Mapa) actual.estat.mou(moviment);
                LNO.push(new Node(seguent, actual, moviment, actual.depth + 1, actual.g + 1));
            }
            rc.updateMemoria(LNO.size(), 0);
        }
    }

    /** BFS inversa que reserva les posicions i les sortides dels altres agents. */
    private int[][] distanciesFinsSortida(Mapa mapa, int agent) {
        int[][] distancies = new int[mapa.getN()][mapa.getM()];
        for (int[] fila : distancies) Arrays.fill(fila, Mapa.INF);

        Posicio sortida = mapa.getSortida(agent);
        if (!esTransitable(mapa, sortida, agent)) return distancies;

        Queue<Posicio> LNO = new ArrayDeque<>();
        LNO.add(sortida);
        distancies[sortida.x][sortida.y] = 0;

        while (!LNO.isEmpty()) {
            Posicio actual = LNO.remove();
            for (Direccio direccio : Direccio.values()) {
                Posicio veina = actual.translate(direccio);
                if (!esTransitable(mapa, veina, agent)) continue;
                if (distancies[veina.x][veina.y] != Mapa.INF) continue;
                distancies[veina.x][veina.y] = distancies[actual.x][actual.y] + 1;
                LNO.add(veina);
            }
        }
        return distancies;
    }

    private boolean esTransitable(Mapa mapa, Posicio posicio, int agent) {
        if (mapa.getCella(posicio) == Mapa.PARET) return false;
        for (int id = 0; id < mapa.getAgents().size(); id++) {
            if (id == agent) continue;
            if (posicio.equals(mapa.getAgents().get(id))) return false;
            if (posicio.equals(mapa.getSortida(id))) return false;
        }
        return true;
    }

    private List<Moviment> reconstrueixSolucio(Node node) {
        List<Moviment> solucio = new ArrayList<>();
        for (Node actual = node; actual.pare != null; actual = actual.pare) {
            solucio.add(actual.accio);
        }
        Collections.reverse(solucio);
        return solucio;
    }

    @Override
    public String getNomCerca() {
        return "Camins disjunts (BFS + backtracking)";
    }
}
