package fsm.states;

import communication.ClientSocket;
import fsm.State;
import fsm.StateContext;
import keyboard.KeyboardFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * <h2>La classe {@code ChooseName} rappresenta uno stato in cui l'utente deve inserire il nome del file in cui
 * salvare il risultato del clustering.</h2>
 * <p>Questo stato si occupa di chiedere all'utente un nome di file univoco (senza estensione) per salvare i risultati dell'analisi.
 * Dopo che l'utente ha fornito il nome del file, il sistema verifica se il nome del file esiste già utilizzando
 * un socket di rete. Se il file esiste, viene chiesto all'utente di scegliere un altro nome. Se il nome è valido,
 * il sistema passa al prossimo stato, {@link Clustering}, per avviare il processo di clustering.</p>
 *
 * @see State
 * @see StateContext
 * @see Clustering
 * @see ClientSocket
 */
public class ChooseName extends State {
    /**
     * <h4>Esegue l'operazione di pre-handle prima di gestire l'input dell'utente.</h4>
     * <p>Invia un messaggio all'utente chiedendo di fornire un nome di file (senza estensione)
     * per il salvataggio del risultato del clustering.</p>
     *
     * @param context Il contesto dello stato corrente che mantiene i dati e la cronologia degli stati.
     * @param update L'aggiornamento ricevuto che contiene i dati del messaggio dell'utente.
     * @return Il messaggio {@link SendMessage} da inviare all'utente.
     * @throws Exception Se si verifica un errore durante l'esecuzione del pre-handle.
     */
    @Override
    protected SendMessage executePreHandle(StateContext context, Update update) throws Exception {
        String message = "\uD83D\uDCBE <b>Salvataggio del Risultato del Clustering</b>\n\n"
                + "Per favore, digita il <b>nome del file</b> (senza estensione) in cui desideri salvare il risultato del clustering.\n\n"
                + "<i>Nota:</i> Assicurati che il nome del file sia univoco per evitare sovrascritture.";

        return SendMessage.builder()
                .chatId(update.getMessage().getChatId())
                .text(message)
                .parseMode("HTML")
                .replyMarkup(new KeyboardFactory().createRollbackKeyboard())
                .build();
    }

    /**
     * <h4>Esegue l'operazione di post-handle dopo che l'utente ha fornito il nome del file.</h4>
     * <p>Verifica se il nome del file esiste già, utilizzando un socket di rete per inviare una richiesta al server.
     * Se il file esiste, l'utente viene avvisato di scegliere un altro nome. Se il nome è valido, il sistema
     * salva il nome del file nel contesto e passa al prossimo stato, {@link Clustering}.</p>
     *
     * @param context Il contesto dello stato corrente che mantiene i dati e la cronologia degli stati.
     * @param update L'aggiornamento ricevuto che contiene i dati del messaggio dell'utente.
     * @return Il messaggio {@link SendMessage} da inviare all'utente.
     * @throws Exception Se si verifica un errore durante la gestione del nome del file o se il file esiste già.
     */
    @Override
    protected SendMessage executePostHandle(StateContext context, Update update) throws Exception {
        String fileName = update.getMessage().getText();

        ClientSocket clientSocket = new ClientSocket();
        clientSocket.sendCheckFileExistsRequest(fileName);
        clientSocket.disconnect();
        context.getData().setSavedFileName(fileName);

        context.changeState(new Clustering());
        return context.handleMessage(update);
    }
}
