package edu.epsevg.prop.ac1.cerca;

import edu.epsevg.prop.ac1.cerca.model.Mapa;
import edu.epsevg.prop.ac1.cerca.heuristica.*;
import edu.epsevg.prop.ac1.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static edu.epsevg.prop.ac1.cerca.SuportProves.*;

class HeuristiquesTest {
    @TempDir Path dir;
    @Test void valorInicialIAgentsSortits() throws Exception {
        Mapa a = mapaA(dir);
        for (Heuristica h : new Heuristica[]{new HeuristicaBasica(), new HeuristicaAvancada()}) {
            assertEquals(12, h.h(a));
            Mapa m = mapa(dir, "#######\naA·B··b\n#######\n");
            assertEquals(4, h.h(m));
            m = mou(m, 0, Direccio.ESQUERRA); assertEquals(3, h.h(m));
            for (int i = 0; i < 3; i++) m = mou(m, 1, Direccio.DRETA);
            assertEquals(0, h.h(m)); assertEquals(12, h.h(a));
        }
    }
    @Test void avancadaTeEnCompteObstaclesIRetornaInf() throws Exception {
        Heuristica h = new HeuristicaAvancada();
        Mapa m = mapa(dir, "#######\na·#··A#\n#···###\n#######\n"), copia = new Mapa(m);
        assertEquals(5, new HeuristicaBasica().h(m)); assertEquals(7, h.h(m)); assertEquals(copia, m);
        assertEquals(Mapa.INF, h.h(mapa(dir, "###a#\n#A###\n#####\n")));
        assertEquals(Mapa.INF, h.h(mapa(dir, "########\na·B·A··b\n########\n")));
    }
    @Test void admissibilitatIConsistenciaEnUnArbrePetit() throws Exception {
        Mapa m = mapa(dir, "##a##\n#A··#\n#···#\n#####\n");
        Deque<Mapa> pendents = new ArrayDeque<>(); Set<Mapa> vistos = new HashSet<>();
        pendents.add(m);
        while (!pendents.isEmpty()) {
            Mapa actual = pendents.remove(); if (!vistos.add(actual)) continue;
            for (Heuristica h : new Heuristica[]{new HeuristicaBasica(), new HeuristicaAvancada()}) {
                int valor = h.h(actual);
                var r = cerca(new CercaBFS(true), actual);
                if (r.getSolucio() != null) assertTrue(valor <= r.getSolucio().size());
                for (Moviment moviment : actual.getAccionsPossibles()) {
                    Mapa fill = (Mapa) actual.mou(moviment);
                    int seguent = h.h(fill);
                    if (valor == Mapa.INF) assertEquals(Mapa.INF, seguent);
                    else assertTrue((long) valor <= 1L + seguent);
                }
            }
            for (Moviment moviment : actual.getAccionsPossibles()) pendents.add((Mapa) actual.mou(moviment));
        }
    }
}
