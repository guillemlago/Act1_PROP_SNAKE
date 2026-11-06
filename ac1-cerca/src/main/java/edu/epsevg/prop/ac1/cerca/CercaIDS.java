package edu.epsevg.prop.ac1.cerca;

import edu.epsevg.prop.ac1.cerca.model.Mapa;
import edu.epsevg.prop.ac1.resultat.ResultatCerca;

public class CercaIDS extends Cerca {


    public CercaIDS(boolean usarLNT) {
        super(usarLNT);
    }

    @Override
    public void ferCerca(Mapa inicial, ResultatCerca rc) {
        CercaDFS cerca = new CercaDFS(usarLNT);
        int limit = 0;
        boolean limitAssolit;

        do {
            // Cada iteració crea una LNO i una LNT noves. El resultat conserva
            // els comptadors acumulats i el màxim dels pics de memòria.
            limitAssolit = cerca.ferCercaLimitada(inicial, rc, limit);
            limit++;
        } while (rc.getSolucio() == null && limitAssolit);
    }
    
    /**
     * Torna el nom de la cerca.
     * @return el nom de la cerca.
     */
    public String getNomCerca(){
        return "IDS";
    }
}
