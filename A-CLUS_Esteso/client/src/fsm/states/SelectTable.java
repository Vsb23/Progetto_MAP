package fsm.states;

import communication.ClientSocket;
import communication.LoadDataResponse;
import fsm.Data;
import fsm.State;
import fsm.StateContext;
import keyboard.KeyboardFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

/**
 * <h2>La classe {@code SelectTable} è una sottoclasse di {@code State} che gestisce la selezione di una tabella da parte
 * dell'utente.</h2>
 * <p>Viene utilizzata per recuperare una lista di tabelle dal server, presentare all'utente le opzioni di
 * selezione tramite una tastiera interattiva e caricare i dati della tabella selezionata.
 * Se non ci sono tabelle disponibili nel database, viene sollevata un'eccezione {@link NoTablesException}.
 * Se l'utente seleziona una tabella valida, i dati associati alla tabella vengono caricati e salvati nel contesto
 * per l'uso successivo.</p>
 *
 * @see State
 * @see NoTablesException
 * @see Data
 * @see ClientSocket
 */
public class SelectTable extends State {
    /**
     * <h4>Esegue l'operazione preliminare per gestire la selezione della tabella.</h4>
     * <p>Invia una richiesta al server per ottenere le tabelle disponibili. Se ci sono tabelle, presenta le
     * opzioni all'utente. Se non ci sono tabelle, viene sollevata un'eccezione {@link NoTablesException}.</p>
     *
     * @param context Il contesto dello stato che mantiene il dato e lo stato corrente.
     * @param update L'oggetto che contiene l'aggiornamento dell'utente, come il messaggio ricevuto.
     * @return Un {@link SendMessage} contenente il messaggio da inviare all'utente con la lista delle tabelle.
     * @throws NoTablesException Se non ci sono tabelle nel database.
     * @throws Exception Se si verifica un errore durante l'esecuzione della richiesta al server.
     */
    @Override
    protected SendMessage executePreHandle(StateContext context, Update update) throws Exception {
        ClientSocket clientSocket = new ClientSocket();
        String[] tables = clientSocket.sendGetDatasetsRequest();
        clientSocket.disconnect();
        if(tables.length == 0) {
            throw new NoTablesException();
        }

        String messageText = "⬇\uFE0F <b>Seleziona sulla tastiera una delle tabelle elencate,</b>\n"
                + "oppure fai clic su <b>'/indietro'</b> per tornare allo stato precedente.";

        return SendMessage.builder()
                .chatId(update.getMessage().getChatId())
                .text(messageText)
                .parseMode("HTML")
                .replyMarkup(new KeyboardFactory().createKeyboard(List.of(tables)))
                .build();
    }

    /**
     * <h4>Esegue l'operazione dopo che l'utente ha selezionato una tabella.</h4>
     * <p>Carica i dati associati alla tabella selezionata e li memorizza nel contesto dell'applicazione.
     * Successivamente, cambia lo stato in {@link ChooseOperation} per permettere all'utente di scegliere
     * l'operazione da eseguire sui dati caricati.</p>
     *
     * @param context Il contesto dello stato che mantiene il dato e lo stato corrente.
     * @param update L'oggetto che contiene l'aggiornamento dell'utente, come il messaggio ricevuto.
     * @return Un {@link SendMessage} contenente il messaggio da inviare all'utente per confermare la selezione.
     * @throws Exception Se si verifica un errore durante il caricamento dei dati.
     */

    @Override
    protected SendMessage executePostHandle(StateContext context, Update update) throws Exception {
        String message = update.getMessage().getText();
        ClientSocket clientSocket = new ClientSocket();
        LoadDataResponse response = clientSocket.sendLoadDataRequest(message);
        clientSocket.disconnect();

        Data data = new Data();
        data.setTableName(message);
        data.setDataset(response.getDataset());
        data.setMaxDepth(response.getMaxDepth());
        context.setData(data);

        context.changeState(new ChooseOperation());
        return context.handleMessage(update);
    }
}
