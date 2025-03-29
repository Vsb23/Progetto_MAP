package distance;

import clustering.Cluster;
import data.Data;
import data.InvalidSizeException;

/**
 * <h2>Interfaccia che definisce il metodo per calcolare la distanza tra due cluster.</h2>
 * <p>
 * Questa interfaccia viene utilizzata nell'algoritmo di clustering per calcolare la distanza tra due cluster,
 * prendendo in input i cluster e i dati associati. Diverse implementazioni possono definire varie metriche di distanza.
 * </p>
 */
public interface ClusterDistance {
	/**
	 * <h4>Calcola la distanza tra due cluster.</h4>
	 * <p>
	 * Implementa la logica per misurare la distanza tra i cluster specificati utilizzando i dati forniti.
	 * La distanza può essere calcolata con diverse metriche a seconda dell'implementazione.
	 * </p>
	 *
	 * @param c1 Il primo cluster.
	 * @param c2 Il secondo cluster.
	 * @param d  I dati utilizzati per calcolare la distanza tra i cluster.
	 * @return La distanza calcolata tra i due cluster.
	 * @throws InvalidSizeException Se gli esempi tra cui si vuole calcolare la distanza euclidea
	 * hanno diversa dimensione.
	 */
	double distance(Cluster c1, Cluster c2, Data d) throws InvalidSizeException;
}
