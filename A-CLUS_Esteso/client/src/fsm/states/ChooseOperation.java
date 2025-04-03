package fsm.states;

import fsm.State;
import fsm.StateContext;
import keyboard.KeyboardFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

/**
 * <h2>La classe {@code ChooseOperation} rappresenta uno stato in cui l'utente deve scegliere un'operazione da
 * eseguire sui dati selezionati, come il clustering o il caricamento di un risultato precedente.</h2>
 * <p>In questo stato, viene mostrata una panoramica del dataset selezionato e l'utente può scegliere
 * tra due operazioni: eseguire il clustering sui dati o caricare un risultato precedente. In base
 * alla scelta dell'utente, il sistema passa al prossimo stato appropriato. Se l'utente fornisce un'opzione
 * non valida, verrà richiesto di scegliere una delle opzioni disponibili.</p>
 *
 * @see State
 * @see StateContext
 * @see ChooseDistance
 * @see GetResultFileName
 */
public class ChooseOperation extends State {
    /**
     * <h4>Esegue l'operazione di pre-handle prima di gestire l'input dell'utente.</h4>
     * <p>Invia un messaggio all'utente che mostra i dettagli del dataset selezionato e le due operazioni disponibili
     * tra cui scegliere: "Clustering" o "Carica risultato".</p>
     *
     * @param context Il contesto dello stato corrente che mantiene i dati e la cronologia degli stati.
     * @param update L'aggiornamento ricevuto che contiene i dati del messaggio dell'utente.
     * @return Il messaggio {@link SendMessage} da inviare all'utente con le informazioni sul dataset e le opzioni.
     * @throws Exception Se si verifica un errore durante l'esecuzione del pre-handle.
     */
    @Override
    protected SendMessage executePreHandle(StateContext context, Update update) throws Exception {
        String datasetName = context.getData().getTableName();
        String dataset = context.getData().getDataset();
        String message = "<b>Hai selezionato la tabella:</b> <i>" + datasetName + "</i>, con i seguenti dati:\n\n"
                + dataset + "\n"
                + "⬇\uFE0F️ Seleziona sulla tastiera un'operazione valida per continuare:";

        return SendMessage.builder()
                .chatId(update.getMessage().getChatId())
                .text(message)
                .parseMode("HTML")
                .replyMarkup(new KeyboardFactory().createKeyboard(List.of("Clustering", "Carica risultato")))
                .build();
    }

    /**
     * <h4>Esegue l'operazione di post-handle dopo che l'utente ha scelto un'operazione.</h4>
     * <p>Se l'utente ha scelto "Clustering", il sistema cambia stato a {@link ChooseDistance}.
     * Se l'utente ha scelto "Carica risultato", il sistema cambia stato a {@link GetResultFileName}.
     * Se l'utente fornisce un'opzione non valida, viene richiesto di selezionare un'opzione valida.</p>
     *
     * @param context Il contesto dello stato corrente che mantiene i dati e la cronologia degli stati.
     * @param update L'aggiornamento ricevuto che contiene i dati del messaggio dell'utente.
     * @return Il messaggio {@link SendMessage} da inviare all'utente con le informazioni sull'operazione scelta
     *         o un messaggio di errore se l'opzione è invalida.
     * @throws Exception Se si verifica un errore durante la gestione della scelta dell'operazione.
     */
    @Override
    protected SendMessage executePostHandle(StateContext context, Update update) throws Exception {
        if(update.getMessage().getText().equalsIgnoreCase("Clustering")) {
            context.changeState(new ChooseDistance());
            return context.handleMessage(update);
        }
        if(update.getMessage().getText().equalsIgnoreCase("Carica risultato")) {
            context.changeState(new GetResultFileName());
            return context.handleMessage(update);
        }

        return SendMessage.builder()
                .chatId(update.getMessage().getChatId())
                .text("Scelta non valida, seleziona un'opzione dalla tastiera")
                .replyMarkup(new KeyboardFactory().createKeyboard(List.of("Clustering", "Carica risultato")))
                .build();
    }
}
