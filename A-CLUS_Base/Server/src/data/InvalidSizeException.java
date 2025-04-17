package data;

/**
 * <h2>La classe InvalidSizeException gestisce l'eccezione lanciata nel caso in cui i due
 * esempi tra cui si vuole calcolare la distanza euclidea abbiano dimensione diversa. </h2>
 * @see Exception
 */
public class InvalidSizeException extends Exception {
    /**
     * <h4>Costruisce un oggetto <code>InvalidSizeException</code> con i dettagli specificati nel messaggio.</h4>
     *
     * @param message messaggio di errore
     */
    InvalidSizeException(String message) {
        super(message);
    }
}
