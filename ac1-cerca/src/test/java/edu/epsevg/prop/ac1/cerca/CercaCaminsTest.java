package edu.epsevg.prop.ac1.cerca;

import edu.epsevg.prop.ac1.cerca.model.Mapa;
import edu.epsevg.prop.ac1.model.Direccio;
import edu.epsevg.prop.ac1.model.Moviment;
import edu.epsevg.prop.ac1.resultat.ResultatCerca;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static edu.epsevg.prop.ac1.cerca.SuportProves.*;
import static org.junit.jupiter.api.Assertions.*;

class CercaCaminsTest {
    @TempDir Path dir;

    @Test void resolAIPotContinuarAmbUnAgentJaFora() throws Exception {
        Mapa inicial = mapaA(dir);
        CercaCamins cerca = new CercaCamins();
        ResultatCerca r = cerca(cerca, inicial);
        valida(inicial, r, 12);
        assertFalse(cerca.usesLNT());
        assertEquals(0, r.getMemoriaPicTancats());
        assertTrue(r.getMemoriaPicOberts() > 0);

        Mapa parcial = inicial;
        int passos = 0;
        for (Moviment moviment : r.getSolucio()) {
            if (!parcial.esAgentActiu(0)) break;
            parcial = (Mapa) parcial.mou(moviment);
            passos++;
        }
        assertFalse(parcial.esAgentActiu(0));
        valida(parcial, cerca(cerca, parcial), 12 - passos);
        valida(inicial, cerca(cerca, inicial), 12);
    }

    @Test void retrocedeixIProvaUnCamiMesLlargPerNoBloquejarB() throws Exception {
        Mapa m = mapa(dir, "###b###\n#A···a#\n#·#B#·#\n#·····#\n#######\n");
        // El camí directe d'A talla l'únic accés de B a b. Cal voltar per sota.
        ResultatCerca r = cerca(new CercaCamins(), m);
        valida(m, r, 10);
        assertTrue(r.getNodesTallats() > 0);
        assertEquals(Direccio.AVALL, r.getSolucio().get(0).getDireccio());
    }

    @Test void esgotaLesAlternativesQuanNoHiHaCaminsDisjunts() throws Exception {
        Mapa m = mapa(dir, "###b###\n#A···a#\n###B###\n#######\n");
        Mapa copia = new Mapa(m);
        ResultatCerca r = cerca(new CercaCamins(), m);
        assertNull(r.getSolucio());
        assertTrue(r.getNodesExplorats() > 1);
        assertTrue(r.getNodesTallats() > 0);
        assertEquals(copia, m);
    }

    @Test void tractaMetaInicialISortidesInabastables() throws Exception {
        Mapa meta = mou(corredor(dir, 1), 0, Direccio.ESQUERRA);
        ResultatCerca r = cerca(new CercaCamins(), meta);
        valida(meta, r, 0);
        assertEquals(1, r.getNodesExplorats());

        Mapa impossible = mapa(dir, "###a#\n#A###\n#####\n");
        r = cerca(new CercaCamins(), impossible);
        assertNull(r.getSolucio());
        assertEquals(1, r.getNodesExplorats());
        assertEquals(1, r.getNodesTallats());
    }

    @Test void noAplicaElLimitDeCinquantaDeLaDfs() throws Exception {
        Mapa m = corredor(dir, 51);
        ResultatCerca r = cerca(new CercaCamins(), m);
        valida(m, r, 51);
        assertEquals(51, r.getProfunditatMaxima());
    }
}
