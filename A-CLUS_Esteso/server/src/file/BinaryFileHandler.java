package file;

import clustering.HierachicalClusterMiner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.FileAlreadyExistsException;

/**
 * <h2>La classe {@code BinaryFileHandler} gestisce la serializzazione e deserializzazione
 * di oggetti {@link HierachicalClusterMiner} in file binari. Fornisce operazioni
 * per salvare e caricare oggetti in un percorso file specificato.</h2>
 * <p>
 * Questa classe permette di specificare un percorso di salvataggio e di verificare
 * l'esistenza di file con estensione binaria predefinita.
 * </p>
 */
public class BinaryFileHandler {
    /** <h4>Estensione di file predefinita per i file salvati.</h4> */
    private static final String DEFAULT_EXTENSION = ".bin";

    /** <h4>Percorso della directory in cui vengono salvati i file binari.</h4> */
    private final String path;

    /**
     * <h4>Costruttore della classe {@code BinaryFileHandler} che accetta un percorso
     * personalizzato.</h4>
     *
     * @param path Il percorso della directory di salvataggio.
     */
    public BinaryFileHandler(String path) {
        this.path = path;
    }

    /**
     * <h4>Costruttore di default che inizializza il percorso della directory a {@code "./saved"}.</h4>
     */
    public BinaryFileHandler() {
        this.path = "../Server/saved";
    }

    /**
     * <h4>Salva un oggetto {@link HierachicalClusterMiner} in un file binario con il nome specificato.</h4>
     * <p>
     * Se il file esiste già, viene lanciata un'eccezione {@link FileAlreadyExistsException}.
     * </p>
     *
     * @param obj      L'oggetto {@link HierachicalClusterMiner} da salvare.
     * @param fileName Il nome del file in cui salvare l'oggetto (senza estensione).
     * @throws IOException Se si verifica un errore di I/O durante il salvataggio o nella creazione della directory.
     */
    public void saveHierachicalClusterMiner(HierachicalClusterMiner obj, String fileName) throws IOException {
        validateFileName(fileName);

        File directory = new File(path);
        if (!directory.exists() && !directory.mkdirs()) {
            throw new IOException("Impossibile creare la directory: " + path);
        }

        File file = new File(directory, fileName + DEFAULT_EXTENSION);
        if (file.exists()) {
            throw new FileAlreadyExistsException("Errore. Il file esiste già: " + fileName);
        }

        try (FileOutputStream fileOut = new FileOutputStream(file);
             ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
            out.writeObject(obj);
        }
    }

    /**
     * <h4>Carica un oggetto {@link HierachicalClusterMiner} da un file binario con il nome specificato.</h4>
     *
     * @param fileName Il nome del file da cui caricare l'oggetto (senza estensione).
     * @return L'oggetto {@link HierachicalClusterMiner} caricato.
     * @throws IOException            Se si verifica un errore di I/O durante il caricamento.
     * @throws ClassNotFoundException Se la classe dell'oggetto caricato non può essere trovata.
     */
    public HierachicalClusterMiner loadHierachicalClusterMiner(String fileName) throws IOException, ClassNotFoundException {
        validateFileName(fileName);

        File file = new File(path, fileName + DEFAULT_EXTENSION);
        if (!file.exists()) {
            throw new FileNotFoundException("File non trovato: " + fileName);
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (HierachicalClusterMiner) ois.readObject();
        }
    }

    /**
     * <h4>Verifica se un file con il nome specificato esiste nella directory impostata.</h4>
     *
     * @param fileName Il nome del file da verificare (senza estensione).
     * @return {@code true} se il file esiste, {@code false} altrimenti.
     */
    public boolean checkFileExists(String fileName) {
        File file = new File(path, fileName);
        return file.exists();
    }

    /**
     * <h4>Valida il nome del file, assicurandosi che non sia nullo, vuoto o contenga caratteri
     * non validi per il file system.</h4>
     *
     * @param fileName Il nome del file da validare.
     * @throws IllegalArgumentException Se il nome del file è nullo o vuoto.
     * @throws IOException              Se il nome del file contiene caratteri non validi.
     */
    private void validateFileName(String fileName) throws IllegalArgumentException, IOException {
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new IllegalArgumentException("Il nome del file non può essere nullo o vuoto.");
        }

        final String invalidChars = "[<>:\"|?*]";

        if (fileName.matches(".*" + invalidChars + ".*")) {
            throw new IOException("Il nome del file contiene caratteri non validi.");
        }
    }
}
