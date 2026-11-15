package edu.epsevg.prop.ac1.cerca;

import edu.epsevg.prop.ac1.cerca.model.Mapa;
import edu.epsevg.prop.ac1.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static edu.epsevg.prop.ac1.cerca.SuportProves.*;

class MapaTest {
    @TempDir Path dir;

    @Test void igualtatIHashDeCopies() throws Exception {
        Mapa a = mapaA(dir), b = new Mapa(a), c = new Mapa(b);
        assertEquals(a, a); assertEquals(a, b); assertEquals(b, a); assertEquals(b, c); assertEquals(a, c);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(null, a); assertNotEquals("mapa", a);
        assertEquals(1, new HashSet<>(Arrays.asList(a, b, c)).size());
    }
    @Test void mateixosAgentsAmbRastresDiferentsNoSonIguals() throws Exception {
        Mapa m = mapa(dir, "##a##\n#A··#\n#···#\n#···#\n#####\n");
        Mapa a = mou(mou(m, 0, Direccio.AVALL), 0, Direccio.DRETA);
        Mapa b = mou(mou(m, 0, Direccio.DRETA), 0, Direccio.AVALL);
        assertEquals(a.getAgents(), b.getAgents());
        assertNotEquals(a, b);
        assertEquals(2, new HashSet<>(Arrays.asList(a, b)).size());
        assertNotEquals(m, a);
    }
    @Test void igualtatAmbAgentsQueJaHanSortit() throws Exception {
        Mapa m = corredor(dir, 1);
        Mapa finalitzat = mou(m, 0, Direccio.ESQUERRA);
        assertNull(finalitzat.getAgents().get(0));
        assertEquals(finalitzat, new Mapa(finalitzat));
        assertEquals(finalitzat.hashCode(), new Mapa(finalitzat).hashCode());
    }
    @Test void distanciesDelMapaAIObstacles() throws Exception {
        Mapa m = mapaA(dir);
        for (int id = 0; id < m.getAgents().size(); id++) {
            Posicio src = m.getAgents().get(id), sortida = m.getSortida(id);
            int[][] d = m.distancesFrom(src);
            assertEquals(0, d[src.x][src.y]); assertEquals(6, d[sortida.x][sortida.y]);
            for (int x = 0; x < m.getN(); x++) for (int y = 0; y < m.getM(); y++) {
                Posicio p = new Posicio(x, y);
                if (m.getCella(p) == Mapa.PARET || (!p.equals(src) && m.getAgents().contains(p)))
                    assertEquals(Mapa.INF, d[x][y]);
            }
        }
    }
    @Test void bfsRodejaParets() throws Exception {
        Mapa m = mapa(dir, "#######\na·#··A#\n#···###\n#######\n");
        assertEquals(7, m.distancesFrom(m.getAgents().get(0))[1][0]);
    }
    @Test void agentsIRastresBloquegenElPas() throws Exception {
        Mapa m = mapa(dir, "#######\naA·B··b\n#######\n");
        int[][] d = m.distancesFrom(m.getAgents().get(0));
        assertEquals(1, d[1][2]); assertEquals(Mapa.INF, d[1][3]); assertEquals(Mapa.INF, d[1][4]);
        Mapa bloquejat = mou(m, 0, Direccio.DRETA);
        assertEquals(Mapa.INF, bloquejat.distancesFrom(bloquejat.getAgents().get(0))[1][0]);
        Mapa parcial = mou(m, 0, Direccio.ESQUERRA);
        assertEquals(3, parcial.distancesFrom(parcial.getAgents().get(1))[1][6]);
    }
    @Test void origenInvalidIDadesIndependents() throws Exception {
        Mapa m = mapaA(dir), copia = new Mapa(m);
        for (Posicio p : Arrays.asList(null, new Posicio(-1, 0), new Posicio(m.getN(), m.getM()), new Posicio(0, 0)))
            for (int[] fila : m.distancesFrom(p)) for (int d : fila) assertEquals(Mapa.INF, d);
        int[][] d = m.distancesFrom(m.getAgents().get(0));
        d[1][0] = -1;
        assertEquals(6, m.distancesFrom(m.getAgents().get(0))[1][0]);
        assertEquals(copia, m); assertEquals(copia.hashCode(), m.hashCode());
    }
}
