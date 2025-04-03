package data;

import java.sql.SQLException;
import java.util.List;
import database.DbAccess;
import database.TableData;
import database.EmptySetException;
import database.DatabaseConnectionException;
import database.MissingNumberException;

/**
 * <h2>La classe {@code Data} gestisce l'acquisizione e la manipolazione dei dati provenienti
 * da una tabella di un database relazionale che verranno utilizzati nell'A-CLus.</h2>
 * <p>
 * I dati vengono recuperati da una tabella specificata al momento della costruzione dell'oggetto e
 * consistono in una lista di oggetti {@link Example} che rappresentano le transazioni.
 * La classe fornisce metodi per ottenere il numero di esempi, accedere a singoli esempi
 * e calcolare la matrice delle distanze tra ciascuna coppia di esempi.
 * </p>
 */
public class Data {
	/**<h4>Lista di oggetti {@code Example} rappresentanti le transazioni.</h4> */
	private final List<Example> data;

	/**
	 * <h4>Numero di transazioni.</h4>
	 */
	private final int numberOfExamples;

	/**
	 * <h4>Costruttore della classe {@code Data} che inizializza il dataset recuperando i dati da una
	 * tabella specificata del database.</h4>
	 * Questo costruttore si occupa di stabilire la connessione
	 * al database, recuperare i dati dalla tabella e chiudere la connessione.
	 *
	 * @param tableName Il nome della tabella da cui recuperare i dati.
	 * @throws NoDataException Se si verifica un errore nel recupero o nella connessione ai dati del database.
	 */
	public Data(String tableName) throws NoDataException {
		DbAccess dbAccess = new DbAccess(); // Inizializzazione dell'accesso al database
		List<Example> examples;
		try {
			dbAccess.initConnection(); //Connessione al database
			TableData tableData = new TableData(dbAccess); // Recupero dei dati distinti dalla tabella
			examples = tableData.getDistinctTransazioni(tableName);
			numberOfExamples = examples.size(); // Impostazione del numero totale di esempi
			dbAccess.closeConnection(); // Chiusura della connessione al database
		}catch(SQLException | EmptySetException | MissingNumberException e) {
			throw new NoDataException("Errore nel recupero dei dati: " + e.getMessage());
		}catch (DatabaseConnectionException e) {
			throw new NoDataException("Errore nella connessione al database: " + e.getMessage());
		}finally {
			try {
				dbAccess.closeConnection(); // Chiusura della connessione al database
			} catch (SQLException e) {
				System.err.println("Errore durante la chiusura della connessione: " + e.getMessage());
			}
		}
		this.data = examples; // Inizializzazione finale del dataset
	}

	/**
	 * <h4>Restituisce il numero di transazioni.</h4>
	 *
	 * @return Il numero di transizioni.
	 */
	public int getNumberOfExamples() {
		return numberOfExamples;
	}

	/**
	 * <h4>Restituisce un esempio specifico dal dataset.</h4>
	 *
	 * @param exampleIndex L'indice dell'esempio da recuperare.
	 * @return L'oggetto {@code Example} corrispondente all'indice specificato.
	 */
	public Example getExample(int exampleIndex) {
		return data.get(exampleIndex);
	}

	/**
	 * <h4>Calcola e restituisce la matrice delle distanze tra ogni coppia di esempi nel dataset.
	 * La distanza tra due esempi viene calcolata utilizzando il metodo {@code distance} della classe {@link Example}.</h4>
	 * <p>
	 * Se durante il calcolo delle distanze si verifica un errore relativo alle dimensioni degli esempi,
	 * viene stampato un messaggio di errore.
	 * </p>
	 *
	 * @return Una matrice 2D di tipo {@code double} contenente le distanze tra ciascuna coppia di punti.
	 */
	public double [][] distance() {
		double [][] dist = new double[numberOfExamples][numberOfExamples];
		for(int i = 0; i < numberOfExamples - 1; i++) {
			for (int j = i + 1, k = i + 1; j < numberOfExamples - 1 | k < numberOfExamples; j++, k++) {
				try {
					dist[i][j] = data.get(i).distance(data.get(k));
				}catch (InvalidSizeException e) {
					System.out.println(e.getMessage());
				}
			}
		}
		return dist;
	}

	/**
	 * <h4>Restituisce la stringa rappresentante il dataset.</h4>
	 *
	 * @return La stringa rappresentante il dataset
	 */
	public String toString() {
		StringBuilder str = new StringBuilder();
		int i = 0;
		for(Example e : data) {
			str.append(i).append(":").append(e.toString()).append("\n");
			i++;
		}
		return str.toString();
	}
}

