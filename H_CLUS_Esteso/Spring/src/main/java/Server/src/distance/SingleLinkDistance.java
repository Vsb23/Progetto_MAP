package Server.src.distance;

import Server.src.clustering.Cluster;
import Server.src.data.Data;
import Server.src.data.Example;
import Server.src.data.InvalidSizeException;

/**
 * <h2>Implementazione della metrica di distanza "Single Link" tra due cluster.</h2>
 * <p>
 * La distanza "Single Link" (o distanza minima) tra due cluster è definita come la distanza minima tra
 * qualsiasi coppia di esempi appartenenti ai due cluster distinti. Questa metrica è comunemente utilizzata
 * nell'algoritmo di clustering gerarchico.
 * </p>
 * <p>Questa classe implementa l'interfaccia {@link ClusterDistance}, che definisce il metodo per calcolare
 * la distanza tra due cluster.</p>
 */
public class SingleLinkDistance implements ClusterDistance {

	/**
	 * <h4>Calcola la distanza "Single Link" tra due cluster.</h4>
	 *
	 * @param c1 Il primo cluster.
	 * @param c2 Il secondo cluster.
	 * @param d  I dati che contengono gli esempi appartenenti ai cluster.
	 * @return La distanza minima tra i due cluster.
	 * @throws InvalidSizeException Se le dimensioni degli esempi nei cluster non sono compatibili per il calcolo della distanza.
	 */
	@Override
	public double distance (Cluster c1, Cluster c2, Data d)  throws InvalidSizeException {
		double min = Double.MAX_VALUE;
		for (Integer i: c1) {
			Example e1 = d.getExample(i);
			for(Integer j: c2) {
				double distance = e1.distance(d.getExample(j));
				if(distance < min)
					min = distance;
			}
		}
		return min;
	}
}
