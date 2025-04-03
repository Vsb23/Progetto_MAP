package keyboard;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

/**
 * <h2>La classe {@code KeyboardFactory} fornisce metodi per creare tastiere personalizzate per l'interazione con
 * l'utente attraverso il bot Telegram.</h2>
 * <p>Questa classe utilizza la libreria Telegram Bots API per creare tastiere
 * di tipo {@link ReplyKeyboardMarkup} o per rimuovere la tastiera con {@link ReplyKeyboardRemove}.
 * I metodi di questa classe permettono di costruire tastiere con diverse configurazioni di bottoni,
 * inclusi bottoni di azione come "indietro" e personalizzazioni della disposizione dei bottoni.</p>
 *
 * @see ReplyKeyboardMarkup
 * @see ReplyKeyboardRemove
 * @see KeyboardButton
 * @see KeyboardRow
 */
public class KeyboardFactory {
    /**
     * <h4>Crea una tastiera con bottoni personalizzati, aggiungendo anche un bottone "indietro" alla fine.</h4>
     * <p>Il metodo riceve una lista di etichette di bottoni e costruisce una tastiera con una riga per ogni
     * bottone, aggiungendo un bottone "indietro" alla fine della lista.</p>
     *
     * @param buttonLabels Una lista di etichette per i bottoni della tastiera.
     * @return Una tastiera Telegram con i bottoni personalizzati, inclusa l'opzione "indietro".
     */
    public ReplyKeyboardMarkup createKeyboard(List<String> buttonLabels) {
        List<String> labels = new ArrayList<>(buttonLabels);
        labels.add("indietro");
        return buildKeyboard(labels);
    }

    /**
     * <h4>Crea una tastiera con un solo bottone "indietro".</h4>
     * <p>Questo metodo crea una tastiera con una sola opzione: il bottone "indietro", che permette
     * all'utente di tornare al passo precedente.</p>
     *
     * @return Una tastiera Telegram con solo il bottone "indietro".
     */
    public ReplyKeyboardMarkup createRollbackKeyboard() {
        List<String> labels = new ArrayList<>();
        labels.add("indietro");
        return buildKeyboard(labels);
    }

    /**
     * <h4>Crea una tastiera con i bottoni specificati come argomento.</h4>
     * <p>Il metodo riceve una lista di etichette di bottoni e costruisce una tastiera con una riga per ogni
     * bottone. Non viene aggiunto il bottone "indietro" in questo caso.</p>
     *
     * @param buttonLabels Una lista di etichette per i bottoni della tastiera.
     * @return Una tastiera Telegram con i bottoni personalizzati specificati.
     */
    public ReplyKeyboardMarkup createFirstKeyboard(List<String> buttonLabels) {
        return buildKeyboard(new ArrayList<>(buttonLabels));
    }

    /**
     * <h4>Rimuove la tastiera dalla finestra di chat.</h4>
     * <p>Questo metodo crea una tastiera di tipo {@link ReplyKeyboardRemove} che rimuove la tastiera attualmente
     * presente nella finestra di chat, rendendo invisibile qualsiasi tastiera precedente.</p>
     *
     * @return Un oggetto {@link ReplyKeyboardRemove} per rimuovere la tastiera.
     */
    public ReplyKeyboardRemove removeKeyboard() {
        return ReplyKeyboardRemove.builder()
                .removeKeyboard(true)
                .build();
    }

    /**
     * <h4>Costruisce una tastiera con i bottoni specificati nella lista di etichette.</h4>
     * <p>Questo metodo crea una tastiera di tipo {@link ReplyKeyboardMarkup}, imposta la proprietà
     * {@code resizeKeyboard} su {@code true} e costruisce una riga per ogni bottone.</p>
     *
     * @param labels Una lista di etichette per i bottoni della tastiera.
     * @return Una tastiera Telegram con i bottoni specificati.
     */

    private ReplyKeyboardMarkup buildKeyboard(List<String> labels) {
        List<KeyboardRow> keyboard = createKeyboardRows(labels);
        return ReplyKeyboardMarkup.builder()
                .keyboard(keyboard)
                .resizeKeyboard(true)
                .build();
    }

    /**
     * <h4>Crea una lista di righe di tastiera a partire dalla lista di etichette.</h4>
     * <p>Ogni etichetta viene trasformata in un bottone su una riga della tastiera. La lista risultante
     * viene utilizzata per costruire la tastiera finale.</p>
     *
     * @param labels Una lista di etichette per i bottoni della tastiera.
     * @return Una lista di righe di tastiera, dove ogni riga contiene un bottone per ogni etichetta.
     */
    private List<KeyboardRow> createKeyboardRows(List<String> labels) {
        List<KeyboardRow> keyboard = new ArrayList<>();
        labels.forEach(label -> {
            KeyboardRow row = new KeyboardRow();
            row.add(new KeyboardButton(label));
            keyboard.add(row);
        });
        return keyboard;
    }
}

