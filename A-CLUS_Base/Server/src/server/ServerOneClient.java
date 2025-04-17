package server;

import clustering.HierachicalClusterMiner;
import clustering.InvalidDepthException;
import clustering.NegativeDepthException;
import data.Data;
import data.InvalidSizeException;
import data.NoDataException;
import distance.SingleLinkDistance;
import distance.AverageLinkDistance;
import distance.ClusterDistance;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.FileAlreadyExistsException;

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
public class ServerOneClient extends Thread{
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
     * <h4>Socket utilizzato per la comunicazione con il client.</h4>
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
     * <h4>Metodo che esegue il thread per gestire la comunicazione con il client.</h4>
     * <p>
     * Gestisce le richieste del client, come il caricamento dei dati, il mining di un dendrogramma,
     * o il caricamento di un dendrogramma da un file.
     * </p>
     */
    public void run() {
        Data data = null;
        while (true) {
            try {
                int choice = (Integer) in.readObject();  // Riceve la scelta del client
                switch (choice) {
                    case LOAD_DATA_REQUEST -> data = loadData(); // Caricamento dei dati
                    case MINE_DENDROGRAM_REQUEST -> {
                        if (data == null) {
                            sendMessageToClient("Errore: nessun dato caricato.");
                        } else mineDendrogram(data); // Mining del dendrogramma
                    }
                    case LOAD_DENDROGRAM_REQUEST ->{
                        if (data == null) {
                            sendMessageToClient("Errore: nessun dato caricato.");
                        } else loadDendrogramFromFile(data); // Caricamento di un dendrogramma salvato
                    }
                }
            } catch (EOFException | SocketException e) {
                handleDisconnectionException();
                break;
            } catch (IOException e) {
                handleIOException(e);
                break;
            } catch (ClassNotFoundException e) {
                handleClassNotFoundException(e);
                break;
            }
        }
        closeResources();
    }

    /**
     * <h4>Invia un messaggio al client e lo stampa nel log del server.</h4>
     * <p>Questo metodo centralizza l'invio dei messaggi al client e cattura
     * eventuali errori di comunicazione durante l'invio.</p>
     *
     * @param message Il messaggio da inviare al client.
     */
    private void sendMessageToClient(String message) {
        System.out.println(message);
        try {
            out.writeObject(message);
        } catch (IOException e) {
            System.err.println("Errore durante l'invio del messaggio di errore al client: " + e.getMessage());
        }
    }

    /**
     * <h4>Carica i dati dalla tabella specificata.</h4>
     *
     * @return Un oggetto Data contenente i dati caricati.
     * @throws IOException            Se si verifica un errore durante la lettura.
     * @throws ClassNotFoundException Se la classe ricevuta dallo stream non esiste.
     */
    private Data loadData() throws IOException, ClassNotFoundException {
        String result = "OK";
        Data data;
        String tablename = (String) in.readObject();
        try {
            data = new Data(tablename);
        } catch (NoDataException e) {
            sendMessageToClient(e.getMessage());
            return null;
        }
        out.writeObject(result);
        return data;
    }

    /**
     * <h4>Esegue il clustering gerarchico sui dati e salva il dendrogramma in un file.</h4>
     *
     * @param data I dati sui quali eseguire il clustering.
     * @throws FileNotFoundException  Se il file specificato non è trovato.
     * @throws IOException            Se si verifica un errore durante la comunicazione o il salvataggio.
     * @throws ClassNotFoundException Se la classe ricevuta dallo stream non esiste.
     */
    private void mineDendrogram(Data data) throws FileNotFoundException, IOException, ClassNotFoundException {
        String result = "OK";
        int depth = (Integer) in.readObject();
        int distanceType = (Integer) in.readObject();
        ClusterDistance distance = (distanceType == 1) ? new SingleLinkDistance() : new AverageLinkDistance();

        try {
            h_clus = new HierachicalClusterMiner(depth);
            h_clus.mine(data, distance);
            out.writeObject(result);
            out.writeObject(h_clus.toString(data));
            String filename = (String) in.readObject();
            saveDendrogram(filename);
        }catch (InvalidSizeException | InvalidDepthException | NegativeDepthException e) {
            sendMessageToClient(e.getMessage());
        }
    }

    /**
     * <h4>Salva il dendrogramma in un file specificato.</h4>
     * <p>
     * Questo metodo tenta di salvare il dendrogramma attualmente gestito dall'oggetto
     * {@link HierachicalClusterMiner} in un file il cui nome e' fornito come parametro.
     * In caso di successo, invia un messaggio di conferma al client.
     * Se si verifica un errore durante il salvataggio, viene inviato un messaggio di errore
     * contenente la causa del problema.
     * </p>
     *
     * @param filename Il nome del file in cui salvare il dendrogramma.
     *                 Deve essere un percorso valido e accessibile dal server.
     */
    private void saveDendrogram(String filename) {
        try {
            h_clus.salva(filename);
            sendMessageToClient("Dendrogramma salvato correttamente.");
        } catch (FileAlreadyExistsException | IllegalArgumentException e) {
            sendMessageToClient(e.getMessage());
        } catch (IOException e) {
            sendMessageToClient("Errore durante il salvataggio del dendrogramma: " + e.getMessage());
        }
    }

    /**
     * <h4>Carica un dendrogramma da un file e lo invia al client.</h4>
     *
     * @param data I dati da usare per la visualizzazione del dendrogramma.
     * @throws FileNotFoundException  Se il file specificato non è trovato.
     * @throws IOException            Se si verifica un errore durante la comunicazione o il caricamento.
     * @throws ClassNotFoundException Se la classe ricevuta dallo stream non esiste.
     */
    private void loadDendrogramFromFile(Data data) throws FileNotFoundException, IOException, ClassNotFoundException {
        String result = "OK";
        String filename = (String) in.readObject();
        try {
            h_clus = HierachicalClusterMiner.loadHierachicalClusterMiner(filename);
        }catch (IllegalArgumentException e) {
            sendMessageToClient(e.getMessage());
            return;
        }
        if(h_clus.getDepth() > data.getNumberOfExamples()) {
            sendMessageToClient("Profondità del dendrogramma maggiore del numero di esempi nella tabella scelta.");
        } else if(h_clus.getLevel0Length() > data.getNumberOfExamples()) {
            sendMessageToClient("Il numero di esempi nella tabella scelta e' minore del " +
                    "numero di esempi con cui e' stato salvato l'oggetto A-CLus precedentemente serializzato.");
        } else {
                System.out.println(h_clus.toString(data));
                out.writeObject(result);
                out.writeObject(h_clus.toString(data));
            }
    }

    /**
     * <h4>Gestisce l'eccezione che si verifica quando il client si disconnette improvvisamente.</h4>
     * <p>Questo metodo stampa un messaggio di errore sul server e invia un messaggio al client
     * per indicare che la connessione è stata interrotta.</p>
     */
    private void handleDisconnectionException() {
        String result = "Il client si è disconnesso.";
        System.err.println(result);
        try {
            out.writeObject(result);
        } catch (IOException e) {
            System.err.println("Errore durante l'invio del messaggio di errore: " + e.getMessage());
        }
    }

    /**
     * <h4>Gestisce le eccezioni di tipo IOException.</h4>
     * <p>Questo metodo stampa il messaggio di errore e prova a inviarlo al client.
     * Se si verifica un altro errore durante l'invio del messaggio, lo stampa nel log del server.</p>
     *
     * @param e Eccezione IOException che si è verificata.
     */
    private void handleIOException(IOException e) {
        String result = (e.getMessage() != null) ? e.getMessage() : "Errore di I/O";
        System.err.println(result);
        try {
            out.writeObject(result);
        } catch (IOException e1) {
            System.err.println("Errore durante l'invio del messaggio di errore: " + e.getMessage());
        }
    }

    /**
     * <h4>Gestisce l'eccezione ClassNotFoundException.</h4>
     * <p>Stampa un messaggio di errore nel log del server e prova a inviarlo al client.
     * Se si verifica un errore durante l'invio del messaggio, viene stampato nel log del server.</p>
     *
     * @param e Eccezione ClassNotFoundException che si è verificata.
     */
    private void handleClassNotFoundException(ClassNotFoundException e) {
        String result = "Classe non trovata: " + e.getMessage();
        System.err.println(result);
        try {
            out.writeObject(result);
        } catch (IOException e1) {
            System.err.println("Errore durante l'invio del messaggio di errore: " + e.getMessage());
        }
    }

    /**
     * <h4>Chiude tutte le risorse di comunicazione con il client.</h4>
     * <p>Chiude lo stream di input, lo stream di output e il socket di connessione
     * per garantire che le risorse vengano rilasciate correttamente.</p>
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

