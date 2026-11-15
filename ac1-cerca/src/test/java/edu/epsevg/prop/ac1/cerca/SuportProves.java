package edu.epsevg.prop.ac1.cerca;

import edu.epsevg.prop.ac1.cerca.model.Mapa;
import edu.epsevg.prop.ac1.model.*;
import edu.epsevg.prop.ac1.resultat.ResultatCerca;
import java.nio.file.*;
import java.io.IOException;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

final class SuportProves {
    static Mapa mapa(Path dir, String text) throws IOException {
        Path p = Files.createTempFile(dir, "mapa-", ".map");
        Files.writeString(p, text);
        return new Mapa(p);
    }
    static Mapa mapaA(Path dir) throws IOException {
        return mapa(dir, "########\na·····A#\n#··#···#\n#B·····b\n########\n");
    }
    static Mapa corredor(Path dir, int passos) throws IOException {
        String fila = "a" + "·".repeat(passos - 1) + "A#";
        String vora = "#".repeat(fila.length());
        return mapa(dir, vora + "\n" + fila + "\n" + vora + "\n");
    }
    static Mapa mou(Mapa m, int id, Direccio d) { return (Mapa) m.mou(new Moviment(id, d)); }
    static ResultatCerca cerca(Cerca cerca, Mapa inicial) {
        ResultatCerca r = new ResultatCerca();
        r.startTime(); cerca.ferCerca(inicial, r); r.stopTime();
        return r;
    }
    static void valida(Mapa inicial, ResultatCerca r, int longitud) {
        assertNotNull(r.getSolucio());
        assertEquals(longitud, r.getSolucio().size());
        Mapa copia = new Mapa(inicial), actual = inicial;
        for (Moviment moviment : r.getSolucio()) {
            assertTrue(actual.getAccionsPossibles().stream().anyMatch(m ->
                    m.getAgentId() == moviment.getAgentId() && m.getDireccio() == moviment.getDireccio()));
            actual = (Mapa) actual.mou(moviment);
        }
        assertTrue(actual.esFinalPartida());
        assertEquals(copia, inicial);
    }
}
