package clustering;

/**
 * <h2>La classe InvalidDepthException gestisce l'eccezione lanciata nel caso in cui la
 * profondita' del dendrogramma e' maggiore del numero di esempi presenti nella tabella scelta. </h2>
 * @see Exception
 */
public class InvalidDepthException extends Exception {
    /**
     * <h4>Costruisce un oggetto <code>InvalidDepthException</code> con i dettagli specificati nel messaggio.</h4>
     *
     * @param message messaggio di errore
     */
    InvalidDepthException(String message) {
        super(message);
    }
}
