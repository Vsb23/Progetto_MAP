
package fsm.states;

import fsm.State;
import fsm.StateContext;
import keyboard.KeyboardFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

/**
 * <h2>La classe {@code Start} è una sottoclasse di {@code State} che rappresenta lo stato iniziale del bot.</h2>
 * <p>Questo stato gestisce la presentazione iniziale del bot all'utente, che viene accolto con un messaggio di benvenuto
 * e viene invitato a iniziare l'interazione cliccando sul pulsante "Inizia" sulla tastiera.
 * Quando l'utente clicca su "Inizia", il bot cambia stato e permette all'utente di selezionare una tabella
 * con cui eseguire operazioni di clustering o visualizzare risultati salvati.</p>
 *
 * @see State
 * @see KeyboardFactory
 */
public class Start extends State {
    /**
     * <h4>Costruttore che imposta il flag {@code allowBack} a {@code false}, impedendo il ritorno a uno stato precedente.</h4>
     */
    public Start() { setAllowBack(false); }

    /**
     * <h4>Esegue l'operazione preliminare di invio del messaggio di benvenuto all'utente.</h4>
     * <p>Quando l'utente interagisce con il bot per la prima volta, viene presentato con un messaggio di benvenuto
     * che spiega il funzionamento del bot e fornisce l'opzione per iniziare utilizzando il pulsante "Inizia".</p>
     *
     * @param context Il contesto dello stato che mantiene il dato e lo stato corrente.
     * @param update L'oggetto che contiene l'aggiornamento dell'utente, come il messaggio ricevuto.
     * @return Un {@link SendMessage} contenente il messaggio di benvenuto e la tastiera con l'opzione "Inizia".
     */
    @Override
    protected SendMessage executePreHandle(StateContext context, Update update) {
        String messageText = "<b>Client A-Clustering tramite Bot Telegram 🇮🇹</b>\n\n" +
                "Ciao " + update.getMessage().getFrom().getUserName() + "!\n\n" +
                "Questo bot ti permette di eseguire clustering o visualizzare risultati di clustering salvati nel server.\n\n" +
                "Clicca <b>'inizia'</b> sulla tastiera per continuare.";

        return SendMessage.builder()
                .chatId(update.getMessage().getChatId())
                .parseMode("HTML")
                .replyMarkup(new KeyboardFactory().createFirstKeyboard(List.of("Inizia")))
                .text(messageText)
                .build();
    }

    /**
     * <h4>Esegue l'operazione dopo che l'utente ha selezionato un'opzione dalla tastiera.</h4>
     * <p>Se l'utente seleziona "Inizia", viene cambiato lo stato del bot a {@link SelectTable}, dove l'utente
     * può selezionare una tabella con cui lavorare. Se l'utente seleziona un'opzione non valida, viene inviato
     * un messaggio di errore che invita l'utente a selezionare un'opzione valida dalla tastiera.</p>
     *
     * @param context Il contesto dello stato che mantiene il dato e lo stato corrente.
     * @param update L'oggetto che contiene l'aggiornamento dell'utente, come il messaggio ricevuto.
     * @return Un {@link SendMessage} contenente il messaggio appropriato in base alla scelta dell'utente.
     * @throws Exception Se si verifica un errore durante il passaggio a un nuovo stato.
     */
    @Override
    protected SendMessage executePostHandle(StateContext context, Update update) throws Exception {
        if(update.getMessage().getText().equalsIgnoreCase("Inizia")) {
            context.changeState(new SelectTable());
            return context.handleMessage(update);
        }

        return SendMessage.builder()
                .chatId(update.getMessage().getChatId())
                .text("Scelta non valida, seleziona un'opzione dalla tastiera")
                .replyMarkup(new KeyboardFactory().createFirstKeyboard(List.of("Inizia")))
                .build();
    }
}
