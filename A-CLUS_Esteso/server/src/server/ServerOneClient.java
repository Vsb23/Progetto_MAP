package server;

import clustering.HierachicalClusterMiner;
import clustering.InvalidDepthException;
import clustering.NegativeDepthException;
import data.Data;
import data.InvalidSizeException;
import data.NoDataException;
import database.Tables;
import database.DbAccess;
import distance.SingleLinkDistance;
import distance.AverageLinkDistance;
import distance.ClusterDistance;
import file.BinaryFileHandler;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.sql.SQLException;

/**
 * <h2>La classe ServerOneClient gestisce la comunicazione con un client.</h2>
 * <p>
 * La classe estende {@link Thread} e effettua overriding del metodo {@link Thread#run()}.
 * La classe riceve una richiesta dal client e la esegue, inviando il risultato al client.
 * Le richieste che il server può ricevere sono caricamento dei dati,
 * esecuzione di algoritmi di clustering, caricamento di dendrogrammi da file e salvataggio su file.</p>
 *
 * @see Thread
 */
public class ServerOneClient extends Thread {
    /**
     * <h4>Codice di richiesta per il caricamento dei dati.</h4>
     */
    private static final int LOAD_DATA_REQUEST = 0;

    /**
     * <<h4>Codice di richiesta per l'esecuzione dell'algoritmo di clustering e generazione del dendrogramma.</h4>
     */
    private static final int MINE_DENDROGRAM_REQUEST = 1;

    /**
     * <h4>Codice di richiesta per il caricamento di un dendrogramma da file.</h4>
     */
    private static final int LOAD_DENDROGRAM_REQUEST = 2;

    /**
     * <h4>Codice di richiesta per ottenere l'elenco delle tabelle disponibili.</h4>
     */
    private static final int GET_TABLES_REQUEST = 3;

    /**
     * <h4>Codice di richiesta per verificare l'esistenza di un file di salvataggio.</h4>
     */
    private static final int CHECK_FILE_EXISTS_REQUEST = 4;

    /**
     * <h4>Socket per la comunicazione con il client.</h4>
     */
    private final Socket socket;
    /**
     * <h4>Stream di input per ricevere messaggi dal client.</h4>
     */
    private final ObjectInputStream in;
    /**
     * <h4>Stream di output per mandare messaggi al client.</h4>
     */
    private final ObjectOutputStream out;
    /**
     * <h4>Oggetto di tipo {@link HierachicalClusterMiner} per effettuare l'A-CLus.</h4>
     */
    private HierachicalClusterMiner h_clus;

    /**
     * <h4>Costruttore che inizializza gli attributi della classe e avvia il thread.</h4>
     *
     * @param s Socket per la comunicazione con il client.
     * @throws IOException in caso di errore nella comunicazione.
     * @see Socket
     */
    ServerOneClient(Socket s) throws IOException {
        socket = s;
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
        start();
    }

    /**
     * <h4>Metodo eseguito al lancio del thread. Gestisce il ciclo principale di comunicazione con il client,
     * monitorando le richieste e inviando le risposte appropriate.</h4>
     */
    public void run() {
        try {
            while (true) {
                try {
                    int requestType = (Integer) in.readObject();
                    switch (requestType) {
                        case LOAD_DATA_REQUEST -> loadData();
                        case MINE_DENDROGRAM_REQUEST -> mineDendrogram();
                        case LOAD_DENDROGRAM_REQUEST -> loadDendrogramFromFile();
                        case GET_TABLES_REQUEST -> geTables();
                        case CHECK_FILE_EXISTS_REQUEST -> checkFileExists();
                        default -> sendErrorResponse("Il tipo di richiesta non è valido.");
                    }
                } catch (EOFException | SocketException e) {
                    // Il client ha chiuso la connessione
                    return;
                } catch (IOException e) {
                    sendErrorResponse("Errore I/O: " + e.getMessage());
                    break;
                } catch (Exception e) {
                    sendErrorResponse(e.getMessage());
                }
            }
        } finally {
            closeResources();
        }
    }

    /**
     * <h4>Invia un messaggio di errore al client.</h4>
     *
     * @param message Il messaggio di errore da inviare.
     */
    private void sendErrorResponse(String message) {
        try {
            System.err.println(message);
            out.writeObject(message);
        } catch (IOException e) {
            System.err.println("Errore durante l'invio del messaggio di errore al client: " + e.getMessage());
        }
    }

    /**
     * <h4>Verifica l'esistenza di un file di salvataggio specificato dal client.</h4>
     *
     * @throws IOException            Se si verifica un errore I/O durante la comunicazione.
     * @throws ClassNotFoundException Se l'oggetto ricevuto dal client non può essere letto.
     */
    private void checkFileExists() throws IOException, ClassNotFoundException {
        String fileName = (String) in.readObject();

        if (!new BinaryFileHandler().checkFileExists(fileName)) {
            // Se il file non esiste, invia un messaggio di conferma
            out.writeObject("OK");
        } else {
            // Se il file esiste, invia un messaggio di errore
            out.writeObject("Esiste già un file di salvataggio con il nome " + fileName + ". Non è possibile sovrascriverlo, scegli un altro nome.");
        }
    }

    /**
     * <h4>Carica i dati da una tabella specificata nel database.</h4>
     *
     * @throws IOException            Se si verifica un errore I/O durante la comunicazione.
     * @throws ClassNotFoundException Se l'oggetto ricevuto dal client non può essere letto.
     */
    private void loadData() throws IOException, ClassNotFoundException {
        String tableName = (String) in.readObject();
        try {
            Data data = new Data(tableName);
            out.writeObject("OK");
            out.writeObject(data.toString());
            out.writeObject(data.getNumberOfExamples());
        } catch (NoDataException e) {
            sendErrorResponse("Si è verificato un errore durante il recupero dei dati dal database: " + e.getMessage());
        }

    }

    /**
     * <h4>Recupera l'elenco delle tabelle disponibili nel database.</h4>
     *
     * @throws IOException Se si verifica un errore I/O durante la comunicazione.
     */
    private void geTables() throws IOException {
        try {
            out.writeObject("OK");
            out.writeObject(new Tables(new DbAccess()).getTables());
        } catch (SQLException e) {
            sendErrorResponse("Si è verificato un errore durante il recupero delle tabelle dal database: " + e.getMessage());
        }
    }

    /**
     * <h4>Esegue il clustering gerarchico sui dati caricati dal client.</h4>
     *
     * @throws IOException            Se si verifica un errore I/O durante la comunicazione.
     * @throws ClassNotFoundException Se l'oggetto ricevuto dal client non può essere letto.
     */
    private void mineDendrogram() throws IOException, ClassNotFoundException {
        String tableName = (String) in.readObject();
        String distanceType = (String) in.readObject();
        int depth = (Integer) in.readObject();
        String filename = (String) in.readObject();

        ClusterDistance distance = distanceType.equalsIgnoreCase("Single Link")
                ? new SingleLinkDistance()
                : new AverageLinkDistance();
        try {
            Data data = new Data(tableName);
            h_clus = new HierachicalClusterMiner(depth);
            h_clus.mine(data, distance);
            h_clus.salva(filename);
            out.writeObject("OK");
            out.writeObject(h_clus.toString(data));
        } catch (NoDataException e) {
            sendErrorResponse("Si è verificato un errore durante il recupero dei dati dal database: " + e.getMessage());
        } catch (InvalidSizeException | InvalidDepthException | NegativeDepthException e) {
            sendErrorResponse("Si è verificato un errore durante l'esecuzione del clustering: " + e.getMessage());
        } catch (IOException | IllegalArgumentException e) {
            sendErrorResponse("Si è verificato un errore durante il salvataggio dei risultati di clustering: " + e.getMessage());
        }
    }

    /**
     * <h4>Carica un dendrogramma da un file binario e lo invia al client.</h4>
     *
     * @throws IOException            Se si verifica un errore I/O durante la comunicazione.
     * @throws ClassNotFoundException Se l'oggetto ricevuto dal client non può essere letto.
     * @throws NoDataException        Se si verifica un errore di caricamento dei dati.
     */
    private void loadDendrogramFromFile() throws IOException, ClassNotFoundException, NoDataException {
        String result = "OK";
        String tableName = (String) in.readObject();
        String filename = (String) in.readObject();

        h_clus = HierachicalClusterMiner.loadHierachicalClusterMiner(filename);

        if(h_clus.getDepth()> new Data(tableName).getNumberOfExamples()) {
            String message = "Profondità del dendrogramma maggiore del numero di esempi nella tabella scelta.";
            System.out.println(message);
            out.writeObject(message);
        } else if(h_clus.getLevel0Length() > new Data(tableName).getNumberOfExamples()) {
            String message = "Il numero di esempi nella tabella scelta e' minore del " +
                    "numero di esempi con cui e' stato salvato l'oggetto A-CLus precedentemente serializzato.";
            System.out.println(message);
            out.writeObject(message);
        }{
            out.writeObject(result);
            out.writeObject(h_clus.toString(new Data(tableName)));
        }
    }

    /**
     * <h4>Chiude le risorse di comunicazione (socket e stream) utilizzate per la comunicazione con il client.</h4>
     */
    private void closeResources() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) socket.close();
            System.out.println("Comunicazione chiusa con il client correttamente.");
        } catch (IOException e) {
            System.err.println("Errore nella chiusura della comunicazione: " + e.getMessage());
        }
    }
}

