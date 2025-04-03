package database;

/**
 * <h2>Eccezione lanciata quando mancano attributi numerici durante l'elaborazione dei dati.</h2>
 */
public class MissingNumberException extends Exception {
    /**
     * <h4>Costruttore di {@code MissingNumberException} che accetta un messaggio di errore.</h4>
     *
     * @param message Il messaggio di errore che descrive la causa dell'eccezione.
     */
    MissingNumberException(String message) {
        super(message);
    }
}
