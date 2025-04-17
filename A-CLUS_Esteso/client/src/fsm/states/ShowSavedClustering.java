package fsm.states;

import communication.ClientSocket;
import fsm.State;
import fsm.StateContext;
import keyboard.KeyboardFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

/**
 * <h2>La classe {@code ShowSavedClustering} è una sottoclasse di {@code State} che gestisce la visualizzazione del
 * risultato del clustering salvato.</h2>
 * <p>Dopo che il clustering è stato completato, questa classe permette all'utente
 * di visualizzare il dendrogramma associato a una tabella specifica e un file di salvataggio.
 * L'utente può anche scegliere se caricare un altro dendrogramma o tornare al menu principale.</p>
 * La classe invia la richiesta al server per caricare il dendrogramma salvato, quindi presenta il risultato
 * in formato leggibile all'utente, insieme a un'opzione per scegliere se caricare un altro dendrogramma o tornare
 * al menu principale.
 *
 * @see State
 * @see ClientSocket
 * @see KeyboardFactory
 */
public class ShowSavedClustering extends State {
    /**
     * <h4>Esegue l'operazione preliminare per caricare e visualizzare il risultato del clustering salvato.</h4>
     * <p>Invia una richiesta al server per caricare il dendrogramma salvato per la tabella e il nome del file
     * specificati. Restituisce quindi il risultato da mostrare all'utente con le opzioni di navigazione.</p>
     *
     * @param context Il contesto dello stato che mantiene il dato e lo stato corrente.
     * @param update L'oggetto che contiene l'aggiornamento dell'utente, come il messaggio ricevuto.
     * @return Un {@link SendMessage} contenente il messaggio da inviare all'utente con il risultato del clustering
     *         e le opzioni di scelta.
     * @throws Exception Se si verifica un errore durante il caricamento del risultato dal server.
     */
    @Override
    protected SendMessage executePreHandle(StateContext context, Update update) throws Exception {
        ClientSocket clientSocket = new ClientSocket();
        String tableName = context.getData().getTableName();
        String fileName = context.getData().getSavedFileName();
        String result = clientSocket.sendLoadDendrogramFromFileRequest(tableName, fileName);
        clientSocket.disconnect();

        String message = "<b>Risultato del clustering</b> per la tabella <i>" + tableName + "</i> salvato nel file <i>" + fileName + "</i>:\n\n"
                + result + "\n"
                + "⬇\uFE0F️ Seleziona un'opzione dalla tastiera:";

        return SendMessage.builder()
                .chatId(update.getMessage().getChatId())
                .text(message)
                .parseMode("HTML")
                .replyMarkup(new KeyboardFactory().createKeyboard(List.of("Carica un altro dendrogramma", "Torna al menu principale")))
                .build();
    }

    /**
     * <h4>Esegue l'operazione dopo che l'utente ha selezionato un'opzione dalla tastiera.</h4>
     * <p>Gestisce la selezione dell'utente per caricare un altro dendrogramma o tornare al menu principale.
     * Se l'utente seleziona "Carica un altro dendrogramma", viene riportato allo stato {@link SelectTable}.
     * Se seleziona "Torna al menu principale", viene riportato allo stato {@link Start}.</p>
     *
     * @param context Il contesto dello stato che mantiene il dato e lo stato corrente.
     * @param update L'oggetto che contiene l'aggiornamento dell'utente, come il messaggio ricevuto.
     * @return Un {@link SendMessage} contenente il messaggio da inviare all'utente in base alla sua selezione.
     * @throws Exception Se si verifica un errore durante il passaggio a un nuovo stato.
     */

    @Override
    protected SendMessage executePostHandle(StateContext context, Update update) throws Exception {
        String message = update.getMessage().getText();
        if (message.equalsIgnoreCase("Carica un altro dendrogramma")) {
            context.changeState(new SelectTable());
            return context.handleMessage(update);
        }
        if (message.equalsIgnoreCase("Torna al menu principale")) {
            context.changeState(new Start());
            return context.handleMessage(update);
        }

        return SendMessage.builder()
                .chatId(update.getMessage().getChatId())
                .text("Scelta non valida, seleziona un'opzione dalla tastiera")
                .replyMarkup(new KeyboardFactory().createKeyboard(List.of("Carica un altro dendrogramma", "Torna al menu principale")))
                .build();
    }
}
