package Server.src.database;

/**
 * <h2>La classe {@code DatabaseConnectionException} rappresenta un'eccezione personalizzata
 * che viene lanciata quando si verificano errori durante la connessione al database.</h2>
 * <p>
 * Fornisce un costruttore che consente di specificare un messaggio di errore dettagliato.
 * </p>
**/
public class DatabaseConnectionException extends Exception {
    /**
     * <h4>Crea una nuova eccezione {@code DatabaseConnectionException} con un messaggio di errore specificato.</h4>
     *
     * @param message Il messaggio che descrive l'errore che ha causato l'eccezione.
     */
    DatabaseConnectionException(String message) {
        super(message);
    }
}
