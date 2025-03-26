package Server.src.distance;

import Server.src.clustering.Cluster;
import Server.src.data.Data;
import Server.src.data.Example;
import Server.src.data.InvalidSizeException;

/**
 * <h2>Implementazione della metrica di distanza "Average Link" tra due cluster.</h2>
 * <p>
 * La distanza "Average Link" è calcolata come la media delle distanze tra tutti i possibili accoppiamenti di esempi
 * appartenenti a due cluster distinti. Questa metrica è comunemente utilizzata negli algoritmi di clustering gerarchico.
 * </p>
 * <p>Questa classe implementa l'interfaccia {@link ClusterDistance}, che definisce il metodo per calcolare la distanza tra due cluster.</p>
 */
public class AverageLinkDistance implements ClusterDistance {


    /**
     * <h4>Calcola la distanza "Average Link" tra due cluster.</h4>
     *
     * @param c1 Il primo cluster.
     * @param c2 Il secondo cluster.
     * @param d  I dati che contengono gli esempi appartenenti ai cluster.
     * @return La distanza media tra i due cluster.
     * @throws InvalidSizeException Se le dimensioni degli esempi nei cluster non sono compatibili per il calcolo della distanza.
     */
    @Override
    public double distance(Cluster c1, Cluster c2, Data d)  throws InvalidSizeException {
        double avg;
        double sum = 0;
        for (Integer i: c1) {
            Example e1 = d.getExample(i);
            for(Integer j: c2) {
                    sum += e1.distance(d.getExample(j));
            }
        }
        avg = sum / (c1.getSize() * c2.getSize());
        return avg;
    }
}
