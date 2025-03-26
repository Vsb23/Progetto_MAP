package Server.src.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <h2>La classe {@code TableSchema} fornisce una rappresentazione dello schema di una tabella del database,
 * permettendo di ottenere informazioni sulle colonne della tabella e sul loro tipo di dati.</h2>
 * <p>
 * Questa classe utilizza i metadati del database per recuperare le informazioni sulle colonne di una tabella specifica.
 * Ogni colonna viene rappresentata tramite un oggetto {@link Column} interno, che contiene il nome e il tipo di dato.
 * Il tipo di dato viene mappato a un tipo più astratto (es. "number" o "string") per facilitarne la gestione nel codice.
 * L'oggetto {@link DbAccess} è utilizzato per stabilire la connessione al database e ottenere i metadati.
 * </p>
 */
public class TableSchema {
	/** <h4>Oggetto {@link DbAccess} utilizzato per connettersi al database.</h4> */
	private final DbAccess db;

	/**
	 * <h2>Classe interna {@code Column} che rappresenta una colonna della tabella.</h2>
	 * <p>
	 * Ogni colonna contiene il nome e il tipo di dato. I tipi di dato sono mappati a tipi più generici
	 * (es. "number" o "string") per un uso più semplice all'interno dell'applicazione.
	 * </p>
	 */
	public class Column{
		/** <h4>Nome della colonna.</h4> */
		private final String name;

		/** <h4>Tipo della colonna (es. "number" o "string").</h4> */
		private final String type;

		/**
		 * <h4>Costruttore della classe {@code Column}.</h4>
		 *
		 * @param name Il nome della colonna.
		 * @param type Il tipo di dato della colonna (mappato a "number" o "string").
		 */
		Column(String name,String type){
			this.name=name;
			this.type=type;
		}

		/**
		 * <h4>Restituisce il nome della colonna.</h4>
		 *
		 * @return Il nome della colonna.
		 */
		public String getColumnName(){
			return name;
		}

		/**
		 * <h4>Verifica se il tipo della colonna è "number".</h4>
		 * <p>
		 * Questo metodo permette di distinguere tra colonne numeriche e non numeriche.
		 * </p>
		 *
		 * @return {@code true} se la colonna è di tipo "number", {@code false} altrimenti.
		 */
		public boolean isNumber(){
			return type.equals("number");
		}

		/**
		 * <h4>Restituisce una rappresentazione in stringa della colonna, con il nome e il tipo di dato.</h4>
		 *
		 * @return Una stringa che rappresenta la colonna, nel formato "nome:type".
		 */
		public String toString(){
			return name+":"+type;
		}
	}

	/** <h4>Lista delle colonne dello schema della tabella.</h4> */
	private final List<Column> tableSchema = new ArrayList<>();

	/**
	 * <h4>Costruttore della classe {@code TableSchema}.</h4>
	 * <p>
	 * Inizializza lo schema della tabella, recuperando le informazioni sulle colonne e sui loro tipi di dato
	 * dal database tramite i metadati. I tipi di dato SQL vengono mappati a tipi Java più astratti.
	 * </p>
	 *
	 * @param db L'accesso al database tramite {@link DbAccess}.
	 * @param tableName Il nome della tabella di cui si vuole ottenere lo schema.
	 * @throws SQLException Se si verifica un errore durante l'accesso ai metadati del database.
	 * @throws DatabaseConnectionException Se la connessione al database fallisce.
	 */
	public TableSchema(DbAccess db, String tableName) throws SQLException, DatabaseConnectionException{
		this.db=db;

		// Mappa dei tipi SQL verso tipi astratti ("number" e "string")
		HashMap<String,String> mapSQL_JAVATypes=new HashMap<>();
		//http://java.sun.com/j2se/1.3/docs/guide/jdbc/getstart/mapping.html
		mapSQL_JAVATypes.put("CHAR","string");
		mapSQL_JAVATypes.put("VARCHAR","string");
		mapSQL_JAVATypes.put("LONGVARCHAR","string");
		mapSQL_JAVATypes.put("BIT","string");
		mapSQL_JAVATypes.put("SHORT","number");
		mapSQL_JAVATypes.put("INT","number");
		mapSQL_JAVATypes.put("LONG","number");
		mapSQL_JAVATypes.put("FLOAT","number");
		mapSQL_JAVATypes.put("DOUBLE","number");

		// Recupera la connessione e i metadati dal database
		Connection con=db.getConnection(); //viene inizializzata la connessione al DB usando i metodi in DBAccess
		 DatabaseMetaData meta = con.getMetaData(); //contiene metadata sul database a cui siamo collegati, ovvero info su tabelle, grammatica SQL supportata ecc.

		// Ottiene i metadati delle colonne della tabella specificata
		ResultSet res = meta.getColumns(null, null, tableName, null); //restituisce una descrizione delle colonne della tabella specificata

		// Popola la lista delle colonne dello schema
		while (res.next()) {
			if(mapSQL_JAVATypes.containsKey(res.getString("TYPE_NAME")))
				tableSchema.add(new Column(
						res.getString("COLUMN_NAME"),
								mapSQL_JAVATypes.get(res.getString("TYPE_NAME")))
				);
		}
		res.close();
	}

	/**
	 * <h4>Restituisce il numero di colonne (attributi) presenti nello schema della tabella.</h4>
	 *
	 * @return Il numero di colonne della tabella.
	 */
	public int getNumberOfAttributes(){
			return tableSchema.size();
		}

	/**
	 * <h4>Restituisce una colonna dello schema della tabella in base all'indice fornito.</h4>
	 *
	 * @param index L'indice della colonna da ottenere (basato su zero).
	 * @return La colonna alla posizione specificata.
	 */
	public Column getColumn(int index){
			return tableSchema.get(index);
		}
}