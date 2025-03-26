package Server.src.database;

import Server.src.data.Example;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * <h2>La classe {@code TableData} gestisce l'accesso ai dati di una tabella del database
 * e permette di recuperare le transazioni distinte presenti nella tabella specificata.</h2>
 * <p>
 * Ogni transazione viene rappresentata come un oggetto di tipo {@link Example}, che contiene i valori numerici
 * degli attributi della tabella. La classe interagisce con il database tramite l'oggetto {@link DbAccess} fornito.
 * </p>
 */
public class TableData {
    /**
     * <h4>Oggetto {@link DbAccess} utilizzato per connettersi al database.</h4>
     */
    private final DbAccess db;

    /**
     * <h4>Costruttore della classe {@code TableData}.</h4>
     * <p>
     * Inizializza l'oggetto {@code TableData} con un'istanza di {@link DbAccess},
     * che verrà utilizzata per accedere ai dati del database.
     * </p>
     *
     * @param db L'istanza di {@link DbAccess} utilizzata per gestire la connessione al database.
     */
    public TableData(DbAccess db) {
        this.db = db;
    }

    /**
     * <h4>Recupera le transazioni distinte dalla tabella specificata e le restituisce sotto forma di lista di oggetti {@link Example}.</h4>
     *
     * @param table Il nome della tabella da cui recuperare le transazioni.
     * @return Una lista di oggetti {@link Example} che rappresentano le transazioni distinte della tabella.
     * @throws SQLException           Se si verifica un errore durante l'esecuzione della query SQL.
     * @throws EmptySetException      Se il set di risultati è vuoto, cioè se non ci sono transazioni nella tabella.
     * @throws MissingNumberException Se uno degli attributi della tabella non è numerico.
     */
    public List<Example> getDistinctTransazioni(String table)
            throws SQLException, EmptySetException, MissingNumberException {

        List<Example> transazioni = new ArrayList<>();

        // Ottiene la connessione e lo schema della tabella
        try (Connection con = db.getConnection()) { // Usa try-with-resources per la connessione
            TableSchema tableSchema = new TableSchema(db, table);

            try (Statement statement = con.createStatement();
                 ResultSet resultSet = statement.executeQuery("SELECT DISTINCT * FROM " + table)) {

                // Itera sui risultati della query per creare gli oggetti Example
                while (resultSet.next()) {
                    Example example = new Example();
                    for (int i = 1; i <= tableSchema.getNumberOfAttributes(); i++) {
                        TableSchema.Column column = tableSchema.getColumn(i - 1);
                        if (column.isNumber()) {
                            example.add(resultSet.getDouble(i)); // Aggiunge il valore numerico all'esempio
                        } else {
                            throw new MissingNumberException("Attributo non numerico: " + column.getColumnName());
                        }
                    }
                    transazioni.add(example); // Aggiunge la transazione alla lista
                }
                if (transazioni.isEmpty()) {
                    throw new EmptySetException("La tabella " + table + " è vuota.");
                }
            }
        } catch (DatabaseConnectionException e) {
            throw new SQLException("Errore di connessione al database: " + e.getMessage());
        }
        return transazioni;
    }

}
