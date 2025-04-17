package fsm.states;

/**
 * <h2>La classe {@code NoTablesException} rappresenta un'eccezione che viene sollevata quando non ci sono
 * tabelle disponibili nel database, e quindi non è possibile eseguire un'operazione che richiede l'accesso a
 * una tabella.</h2>
 *
 * @see Exception
 */
public class NoTablesException extends Exception {
    /**
     * <h4>Costruttore che crea un'eccezione con un messaggio di errore che indica che non ci sono tabelle
     * disponibili nel database.</h4>
     */
    NoTablesException() {
        super("Non ci sono tabelle disponibili nel database");
    }
}
