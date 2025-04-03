package clustering;

import data.Data;
import data.InvalidSizeException;
import distance.ClusterDistance;
import java.io.Serializable;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.nio.file.FileAlreadyExistsException;
import java.util.regex.Pattern;
import java.util.List;
import java.util.Arrays;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;

/**
 * <h2>La classe HierarchicalClusterMiner gestisce il processo di clustering gerarchico.</h2>
 * <p>
 * Questa classe esegue l'algoritmo di agglomerative clustering, creando un
 * dendrogramma che rappresenta la fusione dei cluster a vari livelli di profondità.
 * Il miner può salvare e caricare il suo stato da un file.
 * </p>
 */
public class HierachicalClusterMiner implements Serializable {
	/** <h4>Dendrogramma che rappresenta i cluster a vari livelli.</h4> */
	private final Dendrogram dendrogram;

	/** <h4>Percorso della directory di salvataggio/caricamento degli oggetti serializzati</h4> */
	private static final String DIRECTORY_PATH = "../Server/saved/";

	/**
	 * <h4>Costruttore per inizializzare un HierarchicalClusterMiner con una profondità specificata.</h4>
	 *
	 * @param depth la profondità del dendrogramma
	 * @throws NegativeDepthException se la profondità è negativa
	 */
	public HierachicalClusterMiner(int depth) throws NegativeDepthException {
		dendrogram = new Dendrogram(depth);
	}

	/**
	 * <h4>Esegue l'algoritmo di clustering agglomerativo sui dati forniti.</h4>
	 *
	 * <p>
	 * Inizialmente, ogni esempio è considerato un cluster singolo (livello 0).
	 * Successivamente, a partire dal livello 1, i cluster vengono fusi in base
	 * alla distanza calcolata tramite l'oggetto ClusterDistance fornito.
	 * </p>
	 *
	 * @param data i dati da cui estrarre gli esempi per il clustering
	 * @param distance oggetto che calcola la distanza tra i cluster
	 * @throws InvalidSizeException se la dimensione dei dati non è valida
	 * @throws InvalidDepthException se la profondità del dendrogramma è maggiore del numero di esempi nella tabella
	 */
    public void mine(Data data, ClusterDistance distance) throws InvalidSizeException, InvalidDepthException {
		// Verifica che la profondità del dendrogramma sia valida
		if (dendrogram.getDepth() > data.getNumberOfExamples()) {
			throw new InvalidDepthException("Profondità del dendrogramma maggiore del numero di esempi nella tabella scelta.");
		}
		//livello 0
        ClusterSet lev0 = new ClusterSet(data.getNumberOfExamples());
        for (int i = 0; i < data.getNumberOfExamples(); i++) {
			Cluster c = new Cluster();
            c.addData(i);
            lev0.add(c);
        }
        dendrogram.setClusterSet(lev0, 0);
		//livello >=1 & < dendrogram.getDepth()
		for (int i = 1; i < dendrogram.getDepth(); i++) {
			ClusterSet level = dendrogram.getClusterSet(i-1).mergeClosestClusters(distance, data);
			dendrogram.setClusterSet(level, i);
		}
    }

	/**
	 * <h4>Restituisce la profondità del dendrogramma.</h4>
	 *
	 * @return la profondità del dendrogramma
	 */
	public int getDepth() {
		return dendrogram.getDepth();
	}

	/**
	 * <h4>Metodo statico per caricare un'istanza di HierachicalClusterMiner da un file</h4>
	 *
	 * @param fileName nome del file da cui caricare l'istanza
	 * @return l'istanza caricata di HierachicalClusterMiner
	 * @throws FileNotFoundException se il file non viene trovato
	 * @throws IOException se si verifica un errore di input/output
	 * @throws ClassNotFoundException se la classe dell'oggetto serializzato non viene trovata
	 * @throws IllegalArgumentException se il nome del file è nullo o vuoto
	 */
	public static HierachicalClusterMiner loadHierachicalClusterMiner(String fileName) throws FileNotFoundException, IOException, ClassNotFoundException, IllegalArgumentException {
		if (fileName == null || fileName.trim().isEmpty()) {
			throw new IllegalArgumentException("Il nome del file non può essere nullo o vuoto.");
		}
		String filePath = DIRECTORY_PATH + fileName;
		File file = new File(filePath);
		if (!file.exists()) {
			throw new FileNotFoundException("File non trovato: " + fileName);
		}
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
			return (HierachicalClusterMiner) ois.readObject();
		}
	}

	/**
	 * <h4>Metodo per salvare l'istanza corrente di HierachicalClusterMiner su un file.</h4>
	 *
	 * @param fileName nome del file su cui salvare l'istanza
	 * @throws FileNotFoundException se il file non viene trovato
	 * @throws IOException se si verifica un errore di input/output
	 * @throws IllegalArgumentException se il nome del file è nullo o vuoto
	 */
	public void salva(String fileName) throws FileNotFoundException, IOException, IllegalArgumentException {
		final Pattern invalidPattern = Pattern.compile("[<>:\"|?*]");
		final List<String> validExtensions = Arrays.asList("txt", "csv", "json", "xml", "dat", "bin", "ser");
		// Validazione input
		if (fileName == null || fileName.trim().isEmpty()) {
			throw new IllegalArgumentException("Il nome del file non può essere nullo o vuoto.");
		}
		if (invalidPattern.matcher(fileName).find()) {
			throw new IOException("Il nome del file contiene caratteri non validi.");
		}
		// Verifica l'estensione
		int lastDot = fileName.lastIndexOf('.');
		if (lastDot == -1 || lastDot == fileName.length() - 1) {
			throw new IOException("Il nome del file deve contenere un'estensione valida.");
		}
		String fileExtension = fileName.substring(lastDot + 1).toLowerCase();
		if (!validExtensions.contains(fileExtension)) {
			throw new IOException("Estensione del file non valida.\nAssicurati che il nome del file termini con una delle seguenti estensioni: " + validExtensions);
		}
		// Costruisce il percorso del file e crea la directory se non esiste
		Path directoryPath = Paths.get(DIRECTORY_PATH);
		File directory = directoryPath.toFile();
		if (!directory.exists() && !directory.mkdirs()) {
			throw new IOException("Impossibile creare la directory: " + DIRECTORY_PATH);
		}
		Path filePath = directoryPath.resolve(fileName).normalize();
		if (Files.exists(filePath)) {
			throw new FileAlreadyExistsException("Errore. Il file esiste già: " + fileName);
		}
		// Serializzazione dell'oggetto
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath.toFile()))) {
			oos.writeObject(this);
		}
	}

	/**
	 * <h4>Restituisce la lunghezza del set di cluster al livello 0 del dendrogramma.</h4>
	 *
	 * @return la lunghezza del set di cluster al livello 0 del dendrogramma
	 */
	public int getLevel0Length() {
		return dendrogram.getClusterSet(0).getLength();
	}

	/**
	 * <h4>Restituisce una rappresentazione in forma di stringa del dendrogramma.</h4>
	 *
	 * @return una stringa contenente i dettagli del dendrogramma
	 */
	public String toString() {
		return dendrogram.toString();
	}

	/**
	 * <h4>Restituisce una rappresentazione in forma di stringa del dendrogramma,
	 * utilizzando l'oggetto Data per ottenere i dettagli degli esempi.</h4>
	 *
	 * @param data l'oggetto Data utilizzato per ottenere la rappresentazione degli esempi
	 * @return una stringa contenente i dettagli del dendrogramma e degli esempi
	 */
	public String toString(Data data) {
		return dendrogram.toString(data);
	}
}
