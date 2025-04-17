package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * <h2>La classe {@code DbAccess} gestisce l'accesso e la connessione al database per il recupero
 * dei dati, fornendo metodi per inizializzare, ottenere e chiudere la connessione.</h2>
 * <p>
 * La connessione viene inizializzata utilizzando i parametri predefiniti per il database MySQL
 * locale, inclusi il nome del database, utente, password e porta.
 * È possibile lanciare eccezioni personalizzate, come {@link DatabaseConnectionException}, nel caso si verifichino
 * errori durante l'inizializzazione o il recupero della connessione.
 * </p>
 */
 public class DbAccess {
    /**
     * <h4>Nome della classe del driver JDBC per MySQL.</h4>
     */
    private final String DRIVER_CLASS_NAME = "com.mysql.cj.jdbc.Driver";

    /**
     * <h4>Protocollo e sottoprotocollo di comunicazione.</h4>
     */
    private final String DBMS = "jdbc:mysql";

    /**
     * <h4>Indirizzo del server.</h4>
     */
    private final String SERVER = "localhost";

    /**
     * <h4>Nome del database.</h4>
     */
    private final String DATABASE = "MapDB";

    /**
     * <h4>Porta sulla quale è in ascolto il server del database.</h4>
     */
    private final int PORT = 3306;

    /** <h4>Nome utente per l'accesso al database.</h4> */
    private final String USER_ID = "MapUser";

    /** <h4>Password per l'accesso al database.</h4> */
    private final String PASSWORD = "map";

    /** <h4>Gestisce la connessione al database.</h4> */
    private Connection conn;

    /**
     * <h4>Inizializza la connessione al database.</h4>
     * <p>L'oggetto della classe Connection verrà usato per formulare query e manipolare la base di dati.</p>
     *
     * @throws DatabaseConnectionException Eccezione lanciata se la connessione al database fallisce.
    */
    public void initConnection() throws DatabaseConnectionException
    {
        try {
            // Carica il driver JDBC
            Class.forName(DRIVER_CLASS_NAME);
        } catch(ClassNotFoundException e) {
            System.out.println("[!] Driver not found: " + e.getMessage());
            throw new DatabaseConnectionException(e.toString());
        }
        // Creazione della stringa di connessione
        String connectionString = DBMS + "://" + SERVER + ":" + PORT + "/" + DATABASE
                + "?user=" + USER_ID + "&password=" + PASSWORD + "&serverTimezone=UTC";
        try {
            // Connessione al database
            conn = DriverManager.getConnection(connectionString);
        } catch(SQLException e) {
            throw new DatabaseConnectionException(e.toString());
        }
    }

    /**
     * <h4>Restituisce l'oggetto {@code Connection} per interagire con il database.</h4>
     * <p>
     * Se la connessione non è ancora stata inizializzata, viene chiamato il metodo {@link #initConnection()}.
     * </p>
     *
     * @return Un oggetto {@code Connection} che rappresenta la connessione al database.
     * @throws DatabaseConnectionException Se la connessione al database fallisce durante l'inizializzazione.
     */
    public Connection getConnection() throws DatabaseConnectionException{
        this.initConnection();
        return conn;
    }

    /**
     * <h4>Chiude la connessione attiva con il database.</h4>
     * <p>
     * Questo metodo chiude l'oggetto {@code Connection} aperto per liberare le risorse.
     * </p>
     *
     * @throws SQLException Se si verifica un errore durante la chiusura della connessione.
     */
    public void closeConnection() throws SQLException {
        conn.close();
    }

}
