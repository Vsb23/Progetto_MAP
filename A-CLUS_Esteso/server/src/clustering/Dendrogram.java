package clustering;

import data.Data;
import java.io.Serializable;

/**
 * <h2>La classe Dendrogram rappresenta un dendrogramma utilizzato nell'agglomerative clustering.</h2>
 * <p>
 * Questa classe gestisce una struttura ad albero che rappresenta i cluster
 * e le fusioni di cluster a vari livelli di profondità, facilitando la visualizzazione
 * e l'analisi dei risultati del clustering gerarchico. Ogni livello del dendrogramma
 * rappresenta uno stato diverso della fusione dei cluster.
 * </p>
 */
class Dendrogram implements Serializable {
    /** <h4>Array di ClusterSet che rappresentano i livelli del dendrogramma.</h4> */
    private final ClusterSet[] tree;

    /**
     * <h4>Costruttore per inizializzare un Dendrogram con una profondità specificata.</h4>
     *
     * @param depth la profondità del dendrogramma
     * @throws NegativeDepthException se la profondità è negativa
     */
    Dendrogram(int depth) throws NegativeDepthException {
        if(depth < 0) {
            throw new NegativeDepthException("Valore negativo per la profondita' del Dendrogramma.");
        }
        tree = new ClusterSet[depth];
    }

    /**
     * <h4>Imposta il ClusterSet per un dato livello nel dendrogramma.</h4>
     *
     * @param c il ClusterSet da impostare
     * @param level il livello del dendrogramma in cui impostare il ClusterSet
     */
    void setClusterSet(ClusterSet c, int level) {
        tree[level] = c;
    }

    /**
     * <h4>Restituisce il ClusterSet presente a un livello specificato nel dendrogramma.</h4>
     *
     * @param level il livello del dendrogramma da cui recuperare il ClusterSet
     * @return il ClusterSet al livello specificato
     */
    ClusterSet getClusterSet(int level) {
        return tree[level];
    }

    /**
     * <h4>Restituisce la profondità del dendrogramma.</h4>
     *
     * @return la profondità del dendrogramma
     */
    int getDepth() {
        return tree.length;
    }

    /**
     * <h4>Restituisce una rappresentazione in forma di stringa dei livelli del dendrogramma.</h4>
     *
     * @return una stringa contenente i livelli e i loro ClusterSet
     */
    public String toString() {
        StringBuilder v = new StringBuilder();
        for (int i = 0; i < tree.length; i++)
            v.append("level").append(i).append(":\n").append(tree[i]).append("\n");
        return v.toString();
    }

    /**
     * <h4>Restituisce una rappresentazione in forma di stringa dei livelli del dendrogramma,
     * utilizzando l'oggetto Data per ottenere i dettagli degli esempi.</h4>
     *
     * @param data l'oggetto Data utilizzato per ottenere la rappresentazione degli esempi
     * @return una stringa contenente i livelli e i loro esempi completi, non solo gli indici
     */
    String toString(Data data){
        StringBuilder v = new StringBuilder();
        for (int i = 0; i < tree.length; i++)
            v.append("level").append(i).append(":\n").append(tree[i].toString(data)).append("\n");
        return v.toString();
    }
}
