package fsm.states;

import communication.ClientSocket;
import fsm.State;
import fsm.StateContext;
import keyboard.KeyboardFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * <h2>La classe {@code GetResultFileName} rappresenta uno stato in cui l'utente deve inserire il nome del file
 * (senza estensione) in cui è stato salvato il risultato del clustering.</h2>
 * <p>Questo stato viene utilizzato per raccogliere il nome del file e quindi passare allo stato successivo
 * dove verrà mostrato il risultato del clustering salvato.</p>
 *
 * @see State
 * @see StateContext
 * @see ShowSavedClustering
 */
public class GetResultFileName extends State {
    /**
     * <h4>Esegue l'operazione di pre-handle prima di gestire l'input dell'utente.</h4>
     * <p>Invia un messaggio all'utente chiedendo di digitare il nome del file (senza estensione)
     * dove ha salvato il risultato del clustering.</p>
     *
     * @param context Il contesto dello stato corrente che mantiene i dati e la cronologia degli stati.
     * @param update L'aggiornamento ricevuto che contiene i dati del messaggio dell'utente.
     * @return Il messaggio {@link SendMessage} da inviare all'utente chiedendo di inserire il nome del file.
     * @throws Exception Se si verifica un errore durante l'esecuzione del pre-handle.
     */

    @Override
    protected SendMessage executePreHandle(StateContext context, Update update) throws Exception {
        return SendMessage.builder()
                .chatId(update.getMessage().getChatId())
                .text("Digita il nome del file (senza estensione) dove hai salvato il risultato del clustering.")
                .replyMarkup(new KeyboardFactory().removeKeyboard())
                .build();
    }

    /**
     * <h4>Esegue l'operazione di post-handle dopo che l'utente ha fornito il nome del file.</h4>
     * <p>Una volta che l'utente ha inserito il nome del file, questo viene salvato nei dati e lo stato viene
     * cambiato per mostrare il clustering salvato.</p>
     *
     * @param context Il contesto dello stato corrente che mantiene i dati e la cronologia degli stati.
     * @param update L'aggiornamento ricevuto che contiene i dati del messaggio dell'utente.
     * @return Il messaggio {@link SendMessage} da inviare all'utente con il risultato del clustering salvato.
     * @throws Exception Se si verifica un errore durante l'esecuzione del post-handle.
     */

    @Override
    protected SendMessage executePostHandle(StateContext context, Update update) throws Exception {
        String fileName = update.getMessage().getText();
        context.getData().setSavedFileName(fileName);

        context.changeState(new ShowSavedClustering());
        return context.handleMessage(update);
    }
}
