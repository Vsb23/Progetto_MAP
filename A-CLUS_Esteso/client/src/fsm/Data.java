package fsm;

/**
 * <h2>La classe {@code Data} rappresenta un contenitore per i dati necessari per l'elaborazione di un dendrogramma.</h2>
 * <p>Contiene informazioni sul dataset, la tabella, il file salvato, e parametri come la profondità massima, la profondità
 * corrente e il tipo di distanza utilizzata.</p>
 */
public class Data {
    /** <h4>Il nome del file salvato che contiene i risultati dell'elaborazione.</h4> */
    private String savedFileName;

    /** <h4>Il nome della tabella utilizzata per caricare i dati.</h4> */
    private String tableName;

    /** <h4>Il dataset associato alla tabella, che può essere caricato o elaborato.</h4> */
    private String dataset;

    /** <h4>La profondità massima del dendrogramma.</h4> */
    private int maxDepth;

    /** <h4>La profondità corrente del dendrogramma.</h4> */
    private int depth;

    /** <h4>Il tipo di distanza utilizzato per l'elaborazione dei dati (ad esempio, distanza euclidea).</h4> */
    private String distance;

    /**
     * <h4>Costruttore vuoto della classe {@code Data}.</h4>
     * <p>Questo costruttore non inizializza alcun attributo. È utilizzato per creare un'istanza di {@code Data}
     * che verrà successivamente popolata tramite i metodi setter.</p>
     */
    public Data(){ }

    /**
     * <h4>Imposta il dataset da utilizzare.</h4>
     *
     * @param dataset Il dataset che sarà utilizzato per l'elaborazione.
     */
    public void setDataset(String dataset) {
        this.dataset = dataset;
    }

    /**
     * <h4>Restituisce il dataset associato.</h4>
     *
     * @return Il dataset.
     */
    public String getDataset() {
        return dataset;
    }

    /**
     * <h4>Restituisce il nome della tabella associata a questo oggetto {@code Data}.</h4>
     *
     * @return Il nome della tabella.
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * <h4>Imposta il nome della tabella.</h4>
     *
     * @param tableName Il nome della tabella.
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    /**
     * <h4>Restituisce il nome del file salvato.</h4>
     *
     * @return Il nome del file salvato.
     */
    public String getSavedFileName() {
        return savedFileName;
    }

    /**
     * <h4>Imposta il nome del file salvato.</h4>
     *
     * @param savedFileName Il nome del file che contiene i risultati dell'elaborazione.
     */
    public void setSavedFileName(String savedFileName) {
        this.savedFileName = savedFileName;
    }

    /**
     * <h4>Restituisce la profondità massima del dendrogramma.</h4>
     *
     * @return La profondità massima.
     */
    public int getMaxDepth() { return maxDepth; }

    /**
     * <h4>Imposta la profondità massima del dendrogramma.</h4>
     *
     * @param maxDepth La profondità massima da impostare.
     */
    public void setMaxDepth(int maxDepth) { this.maxDepth = maxDepth; }

    /**
     * <h4>Restituisce la profondità corrente del dendrogramma.</h4>
     *
     * @return La profondità corrente.
     */
    public int getDepth() {return depth; }

    /**
     * <h4>Imposta la profondità corrente del dendrogramma.</h4>
     *
     * @param depth La profondità corrente da impostare.
     */
    public void setDepth(int depth) {
        this.depth = depth;
    }

    /**
     * <h4>Restituisce il tipo di distanza utilizzato per l'elaborazione dei dati.</h4>
     *
     * @return Il tipo di distanza (ad esempio, distanza euclidea).
     */
    public String getDistance() {
        return distance;
    }

    /**
     * <h4>Imposta il tipo di distanza utilizzato per l'elaborazione dei dati.</h4>
     *
     * @param distance Il tipo di distanza da utilizzare.
     */
    public void setDistance(String distance) {
        this.distance = distance;
    }
}
