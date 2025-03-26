package Server.src.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.springframework.stereotype.Component;

/**
 * <h2>La classe MultiServer implementa il server multithread.</h2>
 * <p>
 * Il server multithread è un server che può gestire più client contemporaneamente.
 * Viene creato l'oggetto <code>ServerSocket</code> che si mette in ascolto su una specifica porta.
 * Quando viene stabilita una connessione con un client, viene creato un oggetto ServerOneClient che gestisce la comunicazione con quel client.
 * </p>
 * @see ServerOneClient
 * @see ServerSocket
 */
@Component
public class MultiServer {
    /**
     * <h4>Porta su cui il server è in ascolto.</h4>
     */
    private final int PORT;

    /**
     * <h4>Costruttore del server multithread.</h4>
     * <p>
     * Viene inizializzato il valore della porta e viene chiamato il metodo run().
     * </p>
     *
     * @param port porta su cui il server è in ascolto
     */
    private MultiServer(int port) {
        PORT = port;
        run();
    }

    /**
     * <h4>Esegue il server e gestisce le connessioni dei client. Crea il <code>ServerSocket</code> e si mette in ascolto su una specifica porta.</h4>
     * <p>
     * Quando viene stabilita una connessione con un client (viene accettato il Socket del client), viene creato
     * un oggetto {@link ServerOneClient} che gestisce la comunicazione con quel client.
     * In caso di errore nella creazione del <code>ServerSocket</code>, viene stampato un messaggio di errore e il programma termina.
     * In caso di errore nell'accept del <code>ServerSocket</code>, viene stampato un messaggio di errore, liberata la porta e il programma termina.
     * </p>
     * @see ServerOneClient
     * @see ServerSocket
     * @see Socket
     * @see IOException
     */
    private void run() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server avviato sulla porta: " + PORT);
            while(true) {
                try {
                    Socket socket = serverSocket.accept();
                    System.out.println("Connessione accettata da: " + socket);
                    new ServerOneClient(socket);
                } catch (IOException e) {
                    System.err.println("Errore durante l'accettazione della connessione: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Impossibile avviare il server sulla porta " + PORT + ": " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * <h4>Metodo principale che avvia il server sulla porta 8080.</h4>
     * <p>
     * Questo metodo crea un'istanza della classe {@link MultiServer} e avvia il server.
     * </p>
     *
     * @param args Argomenti della linea di comando (non utilizzati).
     */
    public static void main(String[] args) {
        new MultiServer(8080);
    }
}
