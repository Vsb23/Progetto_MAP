package fsm.states;

import communication.ClientSocket;
import fsm.State;
import fsm.StateContext;
import keyboard.KeyboardFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

/**
 * <h2>La classe {@code Clustering} rappresenta uno stato in cui viene eseguita l'operazione di clustering sui dati
 * selezionati dall'utente.</h2>
 * <p>Dopo aver completato l'operazione, il sistema invia un messaggio di conferma,
 * indicando che il clustering è stato completato con successo e il risultato è stato salvato su un file.
 * In seguito, l'utente viene invitato a scegliere se tornare al menu principale o tornare allo stato precedente.</p>
 *
 * @see State
 * @see StateContext
 * @see Start
 */
public class Clustering extends State {
    /**
     * <h4>Esegue l'operazione di pre-handle prima di gestire l'input dell'utente.</h4>
     * <p>Invia un messaggio all'utente indicando che il clustering è stato completato con successo
     * e che il risultato è stato salvato nel file indicato. L'utente viene invitato a scegliere
     * se tornare al menu principale o allo stato precedente.</p>
     *
     * @param context Il contesto dello stato corrente che mantiene i dati e la cronologia degli stati.
     * @param update L'aggiornamento ricevuto che contiene i dati del messaggio dell'utente.
     * @return Il messaggio {@link SendMessage} da inviare all'utente con il risultato del clustering
     *         e le opzioni disponibili per continuare.
     * @throws Exception Se si verifica un errore durante l'esecuzione del pre-handle.
     */
    @Override
    protected SendMessage executePreHandle(StateContext context, Update update) throws Exception {
        ClientSocket clientSocket = new ClientSocket();
        String response = clientSocket.sendMineDendrogramRequest(context.getData());
        clientSocket.disconnect();

        String message = "✅ <b>Clustering completato con successo!</b>\n\n"
                + response + "\n"
                + "ℹ️ Il risultato è stato salvato nel file " + context.getData().getSavedFileName() + " sul server. Potrai recuperarlo in futuro utilizzando il nome del file di salvataggio.\n\n"
                + "Clicca su <b>'Torna al menu principale'</b> per tornare alla schermata iniziale,"
                + "oppure su <b>'/indietro'</b> per tornare allo stato precedente.";

        return SendMessage.builder()
                .chatId(update.getMessage().getChatId())
                .text(message)
                .parseMode("HTML")
                .replyMarkup(new KeyboardFactory().createKeyboard(List.of("Torna al menu principale")))
                .build();
    }

    /**
     * <h4>Esegue l'operazione di post-handle dopo che l'utente ha scelto una delle opzioni disponibili.</h4>
     * <p>Se l'utente sceglie "Torna al menu principale", il sistema cambia stato allo stato iniziale {@link Start}.
     * Se l'utente fornisce un'opzione non valida, viene richiesto di selezionare una delle opzioni disponibili.</p>
     *
     * @param context Il contesto dello stato corrente che mantiene i dati e la cronologia degli stati.
     * @param update L'aggiornamento ricevuto che contiene i dati del messaggio dell'utente.
     * @return Il messaggio {@link SendMessage} da inviare all'utente con un messaggio di conferma o un errore,
     *         in base alla scelta dell'utente.
     * @throws Exception Se si verifica un errore durante la gestione della scelta dell'utente.
     */
    @Override
    protected SendMessage executePostHandle(StateContext context, Update update) throws Exception {
        String message = update.getMessage().getText();
        if(message.equalsIgnoreCase("Torna al menu principale")) {
            context.changeState(new Start());
            return context.handleMessage(update);
        }

        return SendMessage.builder()
                .chatId(update.getMessage().getChatId())
                .text("Scelta non valida, seleziona un'opzione dalla tastiera")
                .replyMarkup(new KeyboardFactory().createKeyboard(List.of("home")))
                .build();
    }
}
