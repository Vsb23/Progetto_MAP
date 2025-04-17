package communication;

import fsm.Data;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

/**
 * <h2>La classe {@code ClientSocket} gestisce la comunicazione con un server tramite socket.</h2>
 * <p>Essa invia richieste al server per caricare dati, estrarre dendrogrammi e controllare l'esistenza di file.
 * Ogni tipo di richiesta è rappresentato da un codice specifico e il server risponde con i dati richiesti
 * o un messaggio di errore in caso di problemi.</p>
 */
public class ClientSocket {
    /**
     * <h4>Codice di richiesta per caricare i dati di una tabella dal server.</h4>
     * <p>Indica al server che il client desidera caricare i dati relativi a una tabella specificata.</p>
     */
    private static final int LOAD_DATA_REQUEST = 0;

    /**
     * <h4>Codice di richiesta per eseguire l'estrazione di un dendrogramma dal server.</h4>
     * <p>Indica al server che il client desidera generare un dendrogramma utilizzando i dati specificati.</p>
     */
    private static final int MINE_DENDROGRAM_REQUEST = 1;

    /**
     * <h4>Codice di richiesta per caricare un dendrogramma da un file esistente sul server.</h4>
     * <p>Indica al server che il client desidera caricare un dendrogramma precedentemente salvato in un file.</p>
     */
    private static final int LOAD_DENDROGRAM_REQUEST = 2;

    /**
     * <h4>Codice di richiesta per ottenere l'elenco delle tabelle disponibili sul server.</h4>
     * <p>Indica al server che il client desidera ottenere una lista delle tabelle disponibili per l'elaborazione.</p>
     */
    private static final int GET_TABLES_REQUEST = 3;

    /**
     * <h4>Codice di richiesta per verificare se un file esiste sul server.</h4>
     * <p>Indica al server che il client desidera verificare l'esistenza di un file specificato.</p>
     */
    private static final int CHECK_FILE_EXISTS_REQUEST = 4;

    /** <h4>L'indirizzo del server a cui connettersi.</h4> */
    private final String serverAddress;

    /** <h4>La porta del server a cui connettersi.</h4> */
    private final int serverPort;

    /** <h4>Il socket utilizzato per la connessione al server.</h4> */
    private Socket socket;

    /** <h4>Lo stream di output per inviare oggetti al server.</h4> */
    private ObjectOutputStream out;

    /** <h4>Lo stream di input per ricevere oggetti dal server.</h4> */
    private ObjectInputStream in;

    /**
     * <h4>Costruttore che inizializza il {@code ClientSocket}.</h4>
     * <p>Il costruttore stabilisce una connessione con il server, utilizzando un indirizzo e una porta predefiniti,
     * e inizializza gli stream di input e output.</p>
     *
     * @throws IOException Se si verifica un errore durante la creazione della connessione o degli stream.
     */
    public ClientSocket() throws IOException {
        this.serverAddress = "localhost";
        this.serverPort = 8080;
        socket = new Socket(serverAddress, serverPort);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
    }

    /**
     * <h4>Invia una richiesta per caricare i dati di una tabella dal server.</h4>
     * <p>Questo metodo invia al server il nome della tabella e riceve una risposta con il dataset e la profondità massima.</p>
     *
     * @param tableName Il nome della tabella da caricare.
     * @return Un oggetto {@link LoadDataResponse} contenente i dati della tabella e la profondità massima.
     * @throws IOException Se si verifica un errore durante l'invio o la ricezione dei dati.
     * @throws ClassNotFoundException Se si verifica un errore durante la deserializzazione dell'oggetto.
     * @throws SocketResponseException Se la risposta del server non è "OK".
     */
    public LoadDataResponse sendLoadDataRequest(String tableName) throws IOException, ClassNotFoundException, SocketResponseException {
        out.writeObject(LOAD_DATA_REQUEST);
        out.writeObject(tableName);

        String response = (String) in.readObject();

        if (response.equalsIgnoreCase("OK")) {
            String dataset = (String) in.readObject();
            int maxDepth = (int) in.readObject();
            return new LoadDataResponse(dataset, maxDepth);
        }
        throw new SocketResponseException(response);
    }

    /**
     * <h4>Invia una richiesta per ottenere la lista dei dataset disponibili sul server.</h4>
     * <p>Questo metodo invia una richiesta al server e riceve una lista di nomi di tabelle disponibili.</p>
     *
     * @return Una lista di stringhe contenente i nomi delle tabelle disponibili.
     * @throws IOException Se si verifica un errore durante l'invio o la ricezione dei dati.
     * @throws ClassNotFoundException Se si verifica un errore durante la deserializzazione dell'oggetto.
     * @throws SocketResponseException Se la risposta del server non è "OK".
     */
    public String[] sendGetDatasetsRequest() throws IOException, ClassNotFoundException, SocketResponseException {
        out.writeObject(GET_TABLES_REQUEST);
        String response = (String) in.readObject();
        if (response.equalsIgnoreCase("OK")) {
            return (String[]) in.readObject();
        }
        throw new SocketResponseException(response);
    }

    /**
     * <h4>Invia una richiesta per estrarre un dendrogramma dai dati specificati.</h4>
     * <p>Il metodo invia i parametri necessari al server, inclusi il nome della tabella, la distanza,
     * la profondità e il nome del file salvato. Se la risposta è "OK", il metodo restituisce il nome del file creato.</p>
     *
     * @param data I dati da utilizzare per estrarre il dendrogramma.
     * @return Il nome del file creato dal server per il dendrogramma.
     * @throws IOException Se si verifica un errore durante l'invio o la ricezione dei dati.
     * @throws ClassNotFoundException Se si verifica un errore durante la deserializzazione dell'oggetto.
     * @throws SocketResponseException Se la risposta del server non è "OK".
     */
    public String sendMineDendrogramRequest(Data data) throws IOException, ClassNotFoundException, SocketResponseException {
        out.writeObject(MINE_DENDROGRAM_REQUEST);
        out.writeObject(data.getTableName());
        out.writeObject(data.getDistance());
        out.writeObject(data.getDepth());
        out.writeObject(data.getSavedFileName());

        String response = (String) in.readObject();
        if (response.equalsIgnoreCase("OK")) {
            return (String) in.readObject();
        }
        throw new SocketResponseException(response);
    }

    /**
     * <h4>Invia una richiesta per caricare un dendrogramma da un file.</h4>
     * <p>Il metodo invia il nome della tabella e del file al server e riceve una risposta con il risultato.</p>
     *
     * @param tableName Il nome della tabella da cui caricare il dendrogramma.
     * @param fileName Il nome del file da cui caricare il dendrogramma.
     * @return Il risultato della richiesta, generalmente una stringa contenente un messaggio di successo.
     * @throws IOException Se si verifica un errore durante l'invio o la ricezione dei dati.
     * @throws ClassNotFoundException Se si verifica un errore durante la deserializzazione dell'oggetto.
     * @throws SocketResponseException Se la risposta del server non è "OK".
     */
    public String sendLoadDendrogramFromFileRequest(String tableName, String fileName) throws ClassNotFoundException, SocketResponseException, IOException {
        out.writeObject(LOAD_DENDROGRAM_REQUEST);
        out.writeObject(tableName);
        out.writeObject(fileName);

        String response = (String) in.readObject();
        if (response.equalsIgnoreCase("OK")) {
            return (String) in.readObject();
        }
        throw new SocketResponseException(response);
    }

    /**
     * <h4>Invia una richiesta per verificare se un file esiste sul server.</h4>
     * <p>Il metodo invia il nome del file al server e riceve una risposta che indica se il file esiste o meno.</p>
     *
     * @param fileName Il nome del file da verificare.
     * @throws IOException Se si verifica un errore durante l'invio o la ricezione dei dati.
     * @throws ClassNotFoundException Se si verifica un errore durante la deserializzazione dell'oggetto.
     * @throws SocketResponseException Se la risposta del server non è "OK".
     */
    public void sendCheckFileExistsRequest(String fileName) throws IOException, ClassNotFoundException, SocketResponseException {
        out.writeObject(CHECK_FILE_EXISTS_REQUEST);
        out.writeObject(fileName);

        String response = (String) in.readObject();
        if (!response.equalsIgnoreCase("OK")) {
            throw new SocketResponseException(response);
        }
    }

    /**
     * <h4>Chiude la connessione con il server e libera le risorse.</h4>
     * <p>Chiude gli stream di input e output e il socket.</p>
     */
    public void disconnect() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            System.out.println("Errore durante la disconnessione: " + e.getMessage());
        }
    }
}
