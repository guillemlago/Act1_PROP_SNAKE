package edu.epsevg.prop.ac1.cerca.heuristica;

import edu.epsevg.prop.ac1.cerca.model.Mapa;
import edu.epsevg.prop.ac1.model.Posicio;

import java.util.List;

/**
 * Suma, per cada agent que encara no hagi sortit, la distància Manhattan des de la seva
 * posició actual fins a la seva pròpia sortida. És admissible perquè, com que cada
 * moviment mou un únic agent una casella, calen com a mínim tants moviments com la suma
 * de les distàncies pendents de tots els agents.
 */
public class HeuristicaBasica implements Heuristica {



    public HeuristicaBasica() {
    }

    @Override
    public int h(Mapa estat) {
        // Creem una llista de posicions dels agents
        List<Posicio> agents = estat.getAgents();
        int suma = 0;

        // Per cada agent veiem la seva posició dins del mapa 
        // i calculem la seva distancia de la sortida.
        for (int id = 0; id < agents.size(); id++) {
            // Si l'agent ja ha sortit, no aporta més informació, continuem al següent
            if (!estat.esAgentActiu(id)) continue;  

            // Adquirim la posició actual de l'agent i la seva sortida
            Posicio pos = agents.get(id);
            Posicio sortida = estat.getSortida(id);
            // Calculem la distancia de Manhattan de l'agent fins la sortida
            suma += Math.abs(pos.x - sortida.x) + Math.abs(pos.y - sortida.y);
        }
        // Retornem la suma de les distancies de tots els agents pendents
        return suma;
    }

    
    @Override
    public String getNom() {
        return "H.bas";
    }
}
