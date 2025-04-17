package fsm.states;

import fsm.State;
import fsm.StateContext;
import keyboard.KeyboardFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

/**
 * <h2>La classe {@code ChooseDistance} rappresenta uno stato nel quale l'utente deve scegliere una metrica di distanza
 * per l'algoritmo di clustering.</h2>
 * <p>Questo stato presenta le opzioni di distanza disponibili (Single Link e Average Link) all'utente.</p>
 * Le due opzioni di distanza sono:
 * <ul>
 *     <li><b>Single Link</b>: Utilizza la minima distanza tra qualsiasi coppia di esempi appartenenti
 *     a due cluster distinti.</li>
 *     <li><b>Average Link</b>: Calcola la media delle distanze tra tutti i possibili accoppiamenti di esempi
 *     appartenenti a due cluster distinti.</li>
 * </ul>
 *
 * Se l'utente seleziona una delle opzioni valide, la distanza viene salvata nel contesto e lo stato successivo
 * cambia in {@link ChooseDepth}. Se l'utente fornisce una scelta non valida, viene richiesto di selezionare una
 * distanza valida dalla tastiera.
 *
 * @see State
 * @see StateContext
 * @see ChooseDepth
 */
public class ChooseDistance extends State {
    /**
     * <h4>Esegue l'operazione di pre-handle prima di gestire l'input dell'utente.</h4>
     * <p>Invia un messaggio all'utente chiedendo di selezionare una metrica di distanza,
     * spiegando brevemente le due opzioni disponibili (Single Link e Average Link).</p>
     *
     * @param context Il contesto dello stato corrente che mantiene i dati e la cronologia degli stati.
     * @param update L'aggiornamento ricevuto che contiene i dati del messaggio dell'utente.
     * @return Il messaggio {@link SendMessage} da inviare all'utente.
     * @throws Exception Se si verifica un errore durante l'esecuzione del pre-handle.
     */
    @Override
    protected SendMessage executePreHandle(StateContext context, Update update) throws Exception {
        String messageText = "<b>Scegli la distanza da utilizzare:</b>\n\n"
                + "<b>Single Link:</b> Utilizza la minima distanza tra i punti di due cluster per determinare la vicinanza. "
                + "È adatta per cluster di forma allungata e può creare strutture concatenate.\n\n"
                + "<b>Average Link:</b> Calcola la distanza media tra tutti i punti di due cluster. "
                + "È più robusta al rumore e tende a produrre cluster di forma più compatta.\n\n"
                + "⬇\uFE0F Seleziona una delle opzioni di seguito dalla tastiera:";

        return SendMessage.builder()
                .chatId(update.getMessage().getChatId())
                .text(messageText)
                .parseMode("HTML")
                .replyMarkup(new KeyboardFactory().createKeyboard(List.of("Single Link", "Average Link")))
                .build();
    }

    /**
     * <h4>Esegue l'operazione di post-handle dopo che l'utente ha fornito la propria risposta.</h4>
     * <p>Verifica se l'utente ha selezionato una delle due opzioni di distanza valide (Single Link o Average Link).
     * Se la scelta è valida, salva la distanza nel contesto e cambia lo stato successivo a {@link ChooseDepth}.
     * Se la scelta non è valida, viene inviato un messaggio di errore chiedendo di selezionare una distanza valida.</p>
     *
     * @param context Il contesto dello stato corrente che mantiene i dati e la cronologia degli stati.
     * @param update L'aggiornamento ricevuto che contiene i dati del messaggio dell'utente.
     * @return Il messaggio {@link SendMessage} da inviare all'utente.
     * @throws Exception Se si verifica un errore durante l'elaborazione della distanza o una scelta non valida viene fornita.
     */

    @Override
    protected SendMessage executePostHandle(StateContext context, Update update) throws Exception {
        String message = update.getMessage().getText();
        if(message.equalsIgnoreCase("Single Link") || message.equalsIgnoreCase("Average Link")) {
            context.getData().setDistance(message);
            context.changeState(new ChooseDepth());
            return context.handleMessage(update);
        }

        return SendMessage.builder()
                .chatId(update.getMessage().getChatId())
                .text("Scelta non valida, seleziona una distanza valida dalla tastiera")
                .replyMarkup(new KeyboardFactory().createKeyboard(List.of("Single Link", "Average Link")))
                .build();
    }
}
