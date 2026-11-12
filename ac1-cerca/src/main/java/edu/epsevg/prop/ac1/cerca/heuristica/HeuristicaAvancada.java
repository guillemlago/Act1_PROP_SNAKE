package edu.epsevg.prop.ac1.cerca.heuristica;

import edu.epsevg.prop.ac1.cerca.model.Mapa;
import edu.epsevg.prop.ac1.model.Posicio;

import java.util.List;

/**
 * Com {@link HeuristicaBasica} suma per cada agent actiu  la distància fins a la seva
 * pròpia sortida, però usant la distància real sobre la graella actual calculada amb BFS
 * tenint en compte les parets/rastres ja existents en lloc de la distància Manhattan.
 */
public class HeuristicaAvancada implements Heuristica {
    
  
    public HeuristicaAvancada() {
    }

    
    @Override
    public int h(Mapa estat) {
        List<Posicio> agents = estat.getAgents();
        int suma = 0;

        for (int id = 0; id < agents.size(); id++) {
            // Els agents que ja han sortit no aporten cost pendent.
            if (!estat.esAgentActiu(id)) continue;

            Posicio pos = agents.get(id);
            Posicio sortida = estat.getSortida(id);
            int[][] distancies = estat.distancesFrom(pos);
            int distancia = distancies[sortida.x][sortida.y];

            // Una sortida inabastable impedeix completar la partida.
            if (distancia == Mapa.INF) return Mapa.INF;

            suma += distancia;
        }

        return suma;
    }
    
    @Override
    public String getNom() {
        return "H.av";
    }

        // Podeu implementar aquí d'altres métodes que considereu necessaris
    // ...
}
