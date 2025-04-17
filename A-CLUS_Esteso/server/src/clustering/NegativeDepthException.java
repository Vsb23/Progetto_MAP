package clustering;

/**
 * <h2>La classe NegativeDepthException gestisce l'eccezione lanciata nel caso in cui il valore inserito
 * della profondita' del dendrogramma e' negativo. </h2>
 * @see Exception
 */
public class NegativeDepthException extends Exception {
    /**
     * <h4>Costruisce un oggetto <code>InvalidDepthException</code> con i dettagli specificati nel messaggio.</h4>
     * @param message messaggio di errore
     */
    NegativeDepthException(String message) {
        super(message);
    }
}
