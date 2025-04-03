package communication;

/**
 * <h2>La classe {@code LoadDataResponse} rappresenta la risposta ricevuta dal server
 * quando viene richiesto il caricamento dei dati di una tabella.</h2>
 * <p>Contiene il dataset caricato e la profondità massima associata a quei dati.</p>
 */
public class LoadDataResponse {
    /** <h4>Il dataset caricato dal server.</h4> */
    private final String dataset;

    /** <h4>La profondità massima associata ai dati.</h4> */
    private final int maxDepth;

    /**
     * <h4>Costruttore della classe {@code LoadDataResponse}.</h4>
     * <p>Inizializza un'istanza con il dataset caricato e la profondità massima.</p>
     *
     * @param dataset Il dataset caricato dal server.
     * @param maxDepth La profondità massima associata al dataset.
     */
    LoadDataResponse(String dataset, int maxDepth) {
        this.dataset = dataset;
        this.maxDepth = maxDepth;
    }

    /**
     * <h4>Restituisce il dataset caricato dal server.</h4>
     *
     * @return Il dataset caricato.
     */
    public String getDataset() {
        return dataset;
    }

    /**
     * <h4>Restituisce la profondità massima associata al dataset.</h4>
     *
     * @return La profondità massima.
     */
    public int getMaxDepth() {
        return maxDepth;
    }

    /**
     * <h4>Restituisce una rappresentazione testuale dell'oggetto {@code LoadDataResponse}.</h4>
     *
     * @return Una stringa che rappresenta l'oggetto {@code LoadDataResponse}.
     */
    @Override
    public String toString() {
        return "LoadDataResponse{" +
                "dataset=" + dataset +
                ", maxDepth=" + maxDepth +
                '}';
    }
}
