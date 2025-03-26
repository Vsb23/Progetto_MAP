package clustering;

import data.Data;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 * <h2>La classe Cluster rappresenta un insieme di indici di transazioni per l'agglomerative clustering.</h2>
 * <p>
 * Questa classe gestisce un cluster di indici di esempi, permettendo operazioni
 * come l'aggiunta di dati, la fusione di cluster e la clonazione.
 * </p>
 */
public class Cluster implements Iterable<Integer>, Cloneable, Serializable {
	/** <h4>Insieme di indici di transizioni che appartengono al cluster.</h4>*/
	private Set<Integer> clusteredData = new TreeSet<>();

	/**
	 * <h4>Restituisce un iteratore sui dati raggruppati nel cluster.</h4>
	 *
	 * @return un iteratore per gli indici degli esempi nel cluster
	 */
	public Iterator<Integer> iterator() {
		return clusteredData.iterator();
	}

	/**
	 * <h4>Aggiunge l'indice di un esempio al cluster.</h4>
	 *
	 * @param id l'indice dell'esempio da aggiungere al cluster
	 */
	void addData(int id){
		clusteredData.add(id);
	}

	/**
	 * <h4>Restituisce la dimensione del cluster, ovvero il numero di transazioni in esso.</h4>
	 *
	 * @return la dimensione del cluster
	 */
	public int getSize() {
		return clusteredData.size();
	}

	/**
	 * <h4>Clona il cluster attuale creando una copia profonda.</h4>
	 *
	 * @return un nuovo oggetto Cluster, copia del cluster attuale
	 */
	public Cluster clone() {
		Cluster o = null;
		try {
			o = (Cluster)super.clone();
			o.clusteredData = new TreeSet<>(this.clusteredData);
		}catch (CloneNotSupportedException e) {
			System.err.println("Cloning failed");
		}
		return o;
	}

	/**
	 * <h4>Crea un nuovo cluster che è la fusione di due cluster esistenti.</h4>
	 *
	 * @param c il cluster da fondere con il cluster attuale
	 * @return un nuovo cluster che contiene gli indici di entrambi i cluster
	 */
	Cluster mergeCluster (Cluster c) {
		Cluster newC = clone();
		newC.clusteredData.addAll(c.clusteredData);

		return newC;
	}

	/**
	 * <h4>Restituisce una rappresentazione in forma di stringa degli indici nel cluster.</h4>
	 *
	 * @return una stringa contenente gli indici delle transazioni nel cluster, separati da virgole
	 */
	public String toString() {		
		StringBuilder str = new StringBuilder();
		for(Integer i: clusteredData)
			str.append(i).append(",");
		str = new StringBuilder(str.substring(0, str.length() - 1));

		return str.toString();
	}

	/**
	 * <h4>Restituisce una rappresentazione in forma di stringa degli esempi nel cluster,
	 * utilizzando l'oggetto Data per recuperare i dettagli degli esempi.</h4>
	 *
	 * @param data contiene il dataset, utilizzato per ottenere la rappresentazione degli esempi
	 * @return una stringa contenente la rappresentazione degli esempi del cluster
	 * @see Data
	 */
	String toString(Data data){
		StringBuilder str = new StringBuilder();
		for(Integer i: clusteredData)
			str.append("<").append(data.getExample(i)).append(">");
		return str.toString();
	}
}

