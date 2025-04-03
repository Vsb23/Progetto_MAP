package clustering;

import data.Data;
import data.InvalidSizeException;
import distance.ClusterDistance;
import java.io.Serializable;

/**
 * <h2>La classe ClusterSet rappresenta un insieme di cluster utilizzati nell'agglomerative clustering.</h2>
 * <p>
 * Questa classe gestisce un insieme di oggetti Cluster, consentendo operazioni
 * come l'aggiunta di cluster, la fusione dei cluster più vicini e la rappresentazione
 * in forma di stringa degli cluster. È progettata per facilitare la gestione e
 * l'organizzazione dei cluster durante il processo di clustering gerarchico.
 * @see Cluster
 */
class ClusterSet implements Serializable {
	/** <h4>Array di cluster contenuti nel ClusterSet.</h4> */
	private final Cluster[] C;

	/** <h4>Indice dell'ultimo cluster aggiunto.</h4> */
	private int lastClusterIndex = 0;

	/**
	 * <h4>Costruttore per inizializzare un ClusterSet con un numero specificato di cluster.</h4>
	 *
	 * @param k il numero massimo di cluster che il ClusterSet può contenere
	 */
	ClusterSet(int k) {
		C = new Cluster[k];
	}

	/**
	 * <h4>Aggiunge un cluster all'insieme, evitando duplicati.</h4>
	 *
	 * @param c il cluster da aggiungere
	 */
	void add(Cluster c) {
		for(int j = 0; j < lastClusterIndex; j++)
			if(c == C[j]) // to avoid duplicates
				return;
		C[lastClusterIndex] = c;
		lastClusterIndex++;
	}

	/**
	 * <h4>Restituisce la lunghezza dell'array `C`.</h4>
	 *
	 * @return la lunghezza dell'array `C`
	 */
	int getLength() {
		return C.length;
	}

	/**
	 * <h4>Restituisce il cluster all'indice specificato.</h4>
	 *
	 * @param i l'indice del cluster da restituire
	 * @return il cluster all'indice specificato
	 */
	Cluster get(int i) {
		return C[i];
	}

	/**
	 * <h4>Fusione dei due cluster più vicini nel ClusterSet, basata su una misura di distanza.</h4>
	 *
	 * @param distance oggetto che calcola la distanza tra i cluster
	 * @param data oggetto Data utilizzato per ottenere i dati necessari al calcolo della distanza
	 * @return un nuovo ClusterSet contenente i cluster fusi
	 * @throws InvalidSizeException se la dimensione del ClusterSet è non valida
	 */
	ClusterSet mergeClosestClusters(ClusterDistance distance, Data data) throws InvalidSizeException {
		double s = Double.MAX_VALUE;
		double dist;
		int [] pos = new int[2];
		for(int i = 0; i < lastClusterIndex - 1; i++) {
			for(int j = i + 1; j < lastClusterIndex; j++) {
				dist = distance.distance(get(i), get(j), data);
				if (dist < s) {
					s = dist;
					pos[0] = i;
					pos[1] = j;
				}
			}
		}
		ClusterSet n = new ClusterSet(lastClusterIndex - 1);
		for (int k = 0; k < lastClusterIndex; k++) {
			if(k != pos[0] && k != pos[1]) {
				n.add(get(k).clone());
			} else if(k == pos[0]) {
				n.add((get(pos[0]).clone()).mergeCluster(get(pos[1]).clone()));
			}
		}
		return n;
	}

	/**
	 * <h4>Restituisce una rappresentazione in forma di stringa dei cluster nell'insieme.</h4>
	 *
	 * @return una stringa contenente i cluster e i loro indici
	 */
	public String toString() {
		StringBuilder str = new StringBuilder();
		for(int i = 0; i < C.length; i++) {
			if (C[i]!= null) {
				str.append("cluster").append(i).append(":").append(C[i]).append("\n");
			}
		}
		return str.toString();
	}

	/**
	 * <h4>Restituisce una rappresentazione in forma di stringa dei cluster
	 * nell'insieme, utilizzando l'oggetto Data per ottenere i dettagli degli esempi.</h4>
	 *
	 * @param data l'oggetto Data utilizzato per ottenere la rappresentazione degli esempi
	 * @return una stringa contenente i cluster e i loro esempi
	 */
	String toString(Data data) {
		StringBuilder str = new StringBuilder();
		for(int i = 0; i < C.length; i++){
			if (C[i]!= null){
				str.append("cluster").append(i).append(":").append(C[i].toString(data)).append("\n");
			}
		}
		return str.toString();
	}
}
