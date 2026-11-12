package edu.epsevg.prop.ac1.cerca.model;

import edu.epsevg.prop.ac1.model.Direccio;
import edu.epsevg.prop.ac1.model.MapaBase;
import edu.epsevg.prop.ac1.model.Posicio;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Objects;
import java.util.Queue;

/**
 * Implementació concreta de {@link MapaBase}. Aquí és on cal decidir quan dos estats
 * són "el mateix" a efectes de detecció de cicles/repeticions (LNT): com que cada
 * moviment converteix la casella d'origen en paret de manera permanent, dos estats amb
 * els mateixos agents però un rastre de parets diferent <b>no</b> són el mateix estat
 * (podrien tenir moviments futurs possibles diferents). Per tant {@link #equals(Object)}
 * i {@link #hashCode()} han de tenir en compte tant la posició de cada agent com el
 * contingut complet de la graella.
 */
public class Mapa extends MapaBase {


    public Mapa(Path fitxer) throws IOException { super(fitxer); }

    public Mapa(Mapa other) { super(other); }

    @Override
    protected MapaBase clona() { return new Mapa(this); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Mapa)) return false;

        Mapa mapa = (Mapa) o;
        return n == mapa.n
                && m == mapa.m
                && agents.equals(mapa.agents)
                && getSortides().equals(mapa.getSortides())
                && Arrays.deepEquals(grid, mapa.grid);
    }

    @Override
    public int hashCode() {
        int resultat = Objects.hash(n, m, agents, getSortides());
        resultat = 31 * resultat + Arrays.deepHashCode(grid);
        return resultat;
    }

    /**
     * Distàncies mínimes (en nombre de passos,  tenint en compte de no passar per les parets
     * actuals ni trepitjar els agents) des de l'agent a la posició {@code src} a cada casella de la graella,
     * calculades amb BFS.
     * Útil per construir heurístiques informades..
     * @return matriu de distàncies indexat per {@code x, y}; Retorn {@code Integer.MAX_VALUE/4}
     *         si la casella és inabastable
     */
    @Override
    public int[][] distancesFrom(Posicio src) {
        int[][] distancies = new int[n][m];
        for (int[] fila : distancies) {
            Arrays.fill(fila, INF);
        }

        if (src == null || getCella(src) == PARET) {
            return distancies;
        }

        Queue<Posicio> LNO = new ArrayDeque<>();
        distancies[src.x][src.y] = 0;
        LNO.add(src);

        // La matriu de distàncies també indica les caselles ja descobertes.
        while (!LNO.isEmpty()) {
            Posicio actual = LNO.remove();

            for (Direccio direccio : Direccio.values()) {
                Posicio veina = actual.translate(direccio);
                // getCella() també considera paret qualsevol posició fora del mapa.
                if (getCella(veina) == PARET) continue;
                if (distancies[veina.x][veina.y] != INF) continue;
                if (agents.contains(veina)) continue;

                distancies[veina.x][veina.y] = distancies[actual.x][actual.y] + 1;
                LNO.add(veina);
            }
        }

        return distancies;
    }
}
