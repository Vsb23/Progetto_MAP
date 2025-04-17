package fsm.states;

/**
 * <h2>La classe {@code InvalidDepthException} rappresenta un'eccezione che viene sollevata quando l'utente
 * inserisce una profondità non valida durante il processo di selezione della profondità nel flusso di clustering.</h2>
 * <p>La profondità deve essere un numero intero compreso tra 1 e il valore massimo specificato dal dataset.</p>
 *
 * @see Exception
 */
public class InvalidDepthException extends Exception {
    /**
     * <h4>Costruttore che crea un'eccezione con un messaggio di errore specifico per la profondità non valida.</h4>
     * <p>Il messaggio include il valore massimo della profondità consentita.</p>
     *
     * @param maxDepth Il valore massimo di profondità consentito, che viene incluso nel messaggio di errore.
     */
    InvalidDepthException(int maxDepth) {
        super("Profondità non valida, inserisci un numero intero compreso tra 1 e " + maxDepth + ".");
    }
}
