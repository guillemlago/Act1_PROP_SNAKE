package edu.epsevg.prop.ac1.cerca;

import edu.epsevg.prop.ac1.cerca.model.Mapa;
import edu.epsevg.prop.ac1.cerca.heuristica.*;
import edu.epsevg.prop.ac1.model.Direccio;
import edu.epsevg.prop.ac1.resultat.ResultatCerca;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static edu.epsevg.prop.ac1.cerca.SuportProves.*;

class CerquesTest {
    @TempDir Path dir;
    private List<Cerca> cerques(boolean lnt) {
        return Arrays.asList(new CercaBFS(lnt), new CercaDFS(lnt), new CercaIDS(lnt),
                new CercaAStar(lnt, new HeuristicaBasica()), new CercaAStar(lnt, new HeuristicaAvancada()));
    }
    @Test void totesLesVariantsResolenAEnDotzeMoviments() throws Exception {
        Mapa m = mapaA(dir);
        for (boolean lnt : new boolean[]{false, true}) for (Cerca c : cerques(lnt)) {
            ResultatCerca r = cerca(c, m); valida(m, r, 12);
            assertTrue(r.getNodesExplorats() > 0); assertTrue(r.getMemoriaPicOberts() > 0);
            assertTrue(r.getProfunditatMaxima() >= 12);
            if (!lnt) assertEquals(0, r.getMemoriaPicTancats());
            if (c instanceof CercaAStar) { assertTrue(r.teHeuristicaDisponible()); assertEquals(0, r.getHeuristicaMinima()); }
        }
    }
    @Test void metaInicialISenseSolucio() throws Exception {
        Mapa meta = mou(corredor(dir, 1), 0, Direccio.ESQUERRA);
        Mapa impossible = mapa(dir, "#####a#\n#A·####\n#######\n");
        for (boolean lnt : new boolean[]{false, true}) for (Cerca c : cerques(lnt)) {
            ResultatCerca r = cerca(c, meta); valida(meta, r, 0); assertEquals(1, r.getNodesExplorats());
            assertNull(cerca(c, impossible).getSolucio());
        }
    }
    @Test void limitDfsIIdsSenseLimitFix() throws Exception {
        Mapa cinquanta = corredor(dir, 50), cinquantaUn = corredor(dir, 51);
        for (boolean lnt : new boolean[]{false, true}) {
            valida(cinquanta, cerca(new CercaDFS(lnt), cinquanta), 50);
            ResultatCerca r = cerca(new CercaDFS(lnt), cinquantaUn);
            assertNull(r.getSolucio()); assertEquals(50, r.getProfunditatMaxima());
            valida(cinquantaUn, cerca(new CercaIDS(lnt), cinquantaUn), 51);
        }
    }
    @Test void idsAcumulaComptadorsPeroNoSumaPics() throws Exception {
        Mapa m = corredor(dir, 3);
        for (boolean lnt : new boolean[]{false, true}) {
            ResultatCerca r = cerca(new CercaIDS(lnt), m); valida(m, r, 3);
            assertEquals(10, r.getNodesExplorats()); assertEquals(3, r.getNodesTallats());
            assertEquals(1, r.getMemoriaPicOberts());
            assertEquals(lnt ? 3 : 0, r.getMemoriaPicTancats());
            assertEquals(lnt ? 4 : 1, r.getMemoriaPicTotal());
        }
    }
    @Test void aStarPodaArrelISuccessorsInabastables() throws Exception {
        Mapa m = corredor(dir, 1), impossible = mapa(dir, "###a#\n#A###\n#####\n");
        for (boolean lnt : new boolean[]{false, true}) {
            Cerca c = new CercaAStar(lnt, new HeuristicaAvancada());
            ResultatCerca r = cerca(c, impossible);
            assertNull(r.getSolucio()); assertEquals(1, r.getNodesExplorats()); assertEquals(1, r.getNodesTallats());
            Mapa branca = mapa(dir, "####\naA·#\n####\n");
            r = cerca(c, branca); valida(branca, r, 1); assertTrue(r.getNodesTallats() > 0);
            valida(m, cerca(c, m), 1);
        }
    }
    @Test void aStarTrobaElCamiCurtEntreAlternatives() throws Exception {
        Mapa m = mapa(dir, "########\na·····A#\n#······#\n########\n");
        for (boolean lnt : new boolean[]{false, true}) {
            valida(m, cerca(new CercaBFS(lnt), m), 6);
            valida(m, cerca(new CercaAStar(lnt, new HeuristicaBasica()), m), 6);
            valida(m, cerca(new CercaAStar(lnt, new HeuristicaAvancada()), m), 6);
        }
    }
}
