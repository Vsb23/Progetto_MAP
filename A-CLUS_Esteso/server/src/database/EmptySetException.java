package database;

/**
 * <h2>Eccezione lanciata quando si tenta di eseguire un'operazione su un insieme vuoto.</h2>
 */
public class EmptySetException extends Exception {
    /**
     * <h4>Costruttore di {@code EmptySetException} che accetta un messaggio di errore.</h4>
     * @param message Il messaggio di errore che descrive la causa dell'eccezione.
     */
    EmptySetException(String message) {
        super(message);
    }
}
