package database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * <h2> La classe {@code Tables} fornisce metodi per ottenere informazioni sulle tabelle di un database.</h2>
 *  <p>Utilizza un'istanza di {@link DbAccess} per connettersi al database e recuperare i nomi delle tabelle
 * disponibili.</p>
 *
 * @see DbAccess
 */
public class Tables {
    /**
     * <h4>Oggetto {@link DbAccess} utilizzato per connettersi al database.</h4>
     */
    private final DbAccess db;

    /**
     * <h4>Costruttore della classe {@code DatabaseTables}.</h4>
     *
     * @param db L'accesso al database da utilizzare.
     */
    public Tables(DbAccess db) {
        this.db = db;
    }

    /**
     * <h4>Recupera l'elenco delle tabelle presenti nel database.</h4>
     *
     * @return Una lista di stringhe contenente i nomi delle tabelle.
     * @throws SQLException Se si verifica un errore durante l'esecuzione della query SQL.
     */
    public String[] getTables() throws SQLException {
        List<String> tables = new ArrayList<>();

        try (Connection con = db.getConnection();
             Statement statement = con.createStatement();
             ResultSet resultSet = statement.executeQuery("SHOW TABLES")) {

            while (resultSet.next()) {
                tables.add(resultSet.getString(1));
            }

        } catch (DatabaseConnectionException e) {
            throw new RuntimeException("Errore di connessione al database: " + e.getMessage(), e);
        } catch (SQLException e) {
            throw new SQLException("Errore durante l'esecuzione della query: " + e.getMessage(), e);
        }

        return tables.toArray(new String[0]);
    }
}
