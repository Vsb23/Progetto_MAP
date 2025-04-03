package communication;

/**
 * <h2>La classe {@code SocketResponseException} è un'eccezione personalizzata utilizzata per gestire gli errori
 * relativi alle risposte del server durante la comunicazione tramite socket.</h2>
 * <p>Viene sollevata quando il server restituisce una risposta che non è quella attesa, come un errore o un
 * messaggio di stato inaspettato.</p>
 */
public class SocketResponseException extends Exception {
    /**
     * <h4>Costruttore che crea un'istanza dell'eccezione {@code SocketResponseException} con un messaggio di errore.</h4>
     *
     * @param message Il messaggio di errore che descrive il problema verificatosi nella comunicazione del socket.
     */
    SocketResponseException(String message) {
        super(message);
    }
}
