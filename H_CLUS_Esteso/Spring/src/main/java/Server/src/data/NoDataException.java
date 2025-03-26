package Server.src.data;

// Eccezione personalizzata per gestire il caso in cui non ci siano dati disponibili
/**
 * <h2>La classe NoDataException gestisce l'eccezione lanciata
 * per gestire il caso in cui non ci siano dati disponibili nel database. </h2>
 * @see Exception
 */
public class NoDataException extends Exception {
    /**
     * <h4>Costruisce un oggetto <code>NoDataException</code> con i dettagli specificati nel messaggio.</h4>
     *
     * @param message messaggio di errore
     */
    NoDataException(String message) {
        super(message);
    }
}
