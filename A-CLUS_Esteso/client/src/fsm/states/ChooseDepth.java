package fsm.states;

import fsm.State;
import fsm.StateContext;
import keyboard.KeyboardFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * <h2>La classe {@code ChooseDepth} rappresenta uno stato nel quale l'utente è invitato a scegliere una profondità
 * valida per l'operazione successiva.</h2>
 * <p> La profondità può variare da 1 fino al massimo consentito dal dataset, e l'utente deve inserire un valore valido.
 * Se l'utente fornisce un valore non valido (minore di 1 o maggiore del massimo consentito), viene sollevata
 * un'eccezione {@link InvalidDepthException}.</p>
 */
public class ChooseDepth extends State {
    /**
     * <h4>Esegue l'operazione di pre-handle prima di gestire l'input dell'utente.</h4>
     * <p>Invia un messaggio all'utente chiedendo di inserire una profondità valida.
     * Il messaggio include informazioni sulla profondità massima consentita per il dataset selezionato.</p>
     *
     * @param context Il contesto dello stato corrente che mantiene i dati e la cronologia degli stati.
     * @param update L'aggiornamento ricevuto che contiene i dati del messaggio dell'utente.
     * @return Il messaggio {@link SendMessage} da inviare all'utente.
     * @throws Exception Se si verifica un errore durante l'esecuzione del pre-handle.
     */
    @Override
    protected SendMessage executePreHandle(StateContext context, Update update) throws Exception {
        String messageText = "\uD83D\uDCCF <b>Inserire una profondità valida:</b>\n\n"
                + "In base al dataset selezionato, puoi scegliere una profondità compresa tra "
                + "<b>1</b> e <b>" + context.getData().getMaxDepth() + "</b>.";

        return SendMessage.builder()
                .chatId(update.getMessage().getChatId())
                .text(messageText)
                .replyMarkup(new KeyboardFactory().createRollbackKeyboard())
                .parseMode("HTML")
                .build();
    }

    /**
     * <h4>Esegue l'operazione di post-handle dopo che l'utente ha fornito la propria risposta.</h4>
     * <p>Verifica che la profondità inserita sia un valore valido compreso tra 1 e la profondità massima consentita.
     * Se il valore è valido, aggiorna i dati nel contesto e cambia lo stato successivo a {@link ChooseName}.
     * In caso contrario, viene sollevata un'eccezione {@link InvalidDepthException}.</p>
     *
     * @param context Il contesto dello stato corrente che mantiene i dati e la cronologia degli stati.
     * @param update L'aggiornamento ricevuto che contiene i dati del messaggio dell'utente.
     * @return Il messaggio {@link SendMessage} da inviare all'utente.
     * @throws Exception Se si verifica un errore durante l'elaborazione della profondità o un valore non valido viene fornito.
     * @see InvalidDepthException
     */
    @Override
    protected SendMessage executePostHandle(StateContext context, Update update) throws Exception {
        int depth = Integer.parseInt(update.getMessage().getText());
        int maxDepth = context.getData().getMaxDepth();
        if(depth < 1 || depth > maxDepth) {
            throw new InvalidDepthException(maxDepth);
        }

        context.getData().setDepth(depth);
        context.changeState(new ChooseName());
        return context.handleMessage(update);
    }
}
