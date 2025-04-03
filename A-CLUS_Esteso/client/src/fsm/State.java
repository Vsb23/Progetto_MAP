package fsm;

import keyboard.KeyboardFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * <h2>La classe astratta {@code State} rappresenta uno stato generico in un sistema a stati finiti (FSM).</h2>
 * <p>Ogni stato può eseguire delle operazioni prima e dopo l'elaborazione di un aggiornamento ricevuto
 * da Telegram, e fornisce messaggi di errore in caso di problemi.
 * Gli stati derivati da questa classe devono implementare i metodi {@link #executePreHandle(StateContext, Update)}
 * e {@link #executePostHandle(StateContext, Update)} per gestire le operazioni specifiche prima e dopo l'elaborazione
 * dell'aggiornamento.</p>
 */
public abstract class State {
    /** <h4>Indica se lo stato è in modalità post-handle (ovvero, se è stato già trattato).</h4> */
    private boolean isPostHandle = false;

    /** <h4>Indica se il ritorno allo stato precedente è consentito.</h4> */
    private boolean allowBack = true;

    /**
     * <h4>Restituisce se lo stato è stato trattato in modalità post-handle.</h4>
     *
     * @return {@code true} se lo stato è in modalità post-handle, {@code false} altrimenti.
     */
    boolean isPostHandle() {
        return isPostHandle;
    }

    /**
     * <h4>Resetta lo stato a pre-handle, impostando {@code isPostHandle} su {@code false}.</h4>
     */
    void resetState() {
        this.isPostHandle = false;
    }

    /**
     * <h4>Crea un messaggio di errore da inviare all'utente in caso di eccezione.</h4>
     * <p>Il messaggio di errore fornisce dettagli sull'errore e, se consentito, include la possibilità di tornare allo stato precedente.</p>
     *
     * @param update L'aggiornamento ricevuto dal bot.
     * @param errorMessage Il messaggio di errore da visualizzare.
     * @return Il messaggio di errore formattato come {@link SendMessage}.
     */
    private SendMessage getErrorMessage(Update update, String errorMessage) {
        if(allowBack) {
            update.getMessage().getReplyMarkup();
            return SendMessage.builder()
                    .chatId(update.getMessage().getChatId())
                    .text("<b>❗</b>" + errorMessage + "\n" +
                            "Riprova oppure digita <b>'/indietro'</b> per tornare allo stato precedente.")
                    .parseMode("HTML")
                    .replyMarkup(new KeyboardFactory().createRollbackKeyboard())
                    .build();
        }
        return SendMessage.builder()
                .chatId(update.getMessage().getChatId())
                .text("<b>❗</b>" + errorMessage + "\n" +
                        "Invia un nuovo messaggio valido per continuare.")
                .parseMode("HTML")
                .build();
    }

    /**
     * <h4>Esegue le operazioni necessarie prima dell'elaborazione dell'aggiornamento.</h4>
     * <p>Viene invocato dal {@link StateContext} e permette di gestire eventuali errori.</p>
     *
     * @param context Il contesto dello stato corrente.
     * @param update L'aggiornamento ricevuto dal bot.
     * @return Il messaggio {@link SendMessage} da inviare come risposta.
     */
    SendMessage preHandle(StateContext context, Update update) {
        try {
            this.isPostHandle = true;
            return executePreHandle(context, update);
        }  catch (Exception e) {
            context.rollbackState();
            return getErrorMessage(update, e.getMessage());
        }
    }

    /**
     * <h4>Esegue le operazioni necessarie dopo l'elaborazione dell'aggiornamento.</h4>
     * <p>Viene invocato dal {@link StateContext} e permette di gestire eventuali errori.</p>
     *
     * @param context Il contesto dello stato corrente.
     * @param update L'aggiornamento ricevuto dal bot.
     * @return Il messaggio {@link SendMessage} da inviare come risposta.
     */
    SendMessage postHandle(StateContext context, Update update) {
        try {
            return executePostHandle(context, update);
        } catch (Exception e) {
            return getErrorMessage(update, e.getMessage());
        }
    }
    /**
     * <h4>Restituisce se il ritorno allo stato precedente è consentito.</h4>
     *
     * @return {@code true} se il ritorno è consentito, {@code false} altrimenti.
     */
    boolean isAllowBack() {
        return allowBack;
    }

    /**
     * <h4>Imposta se il ritorno allo stato precedente è consentito.</h4>
     *
     * @param allowBack {@code true} per consentire il ritorno, {@code false} altrimenti.
     */
    protected void setAllowBack(boolean allowBack) {
        this.allowBack = allowBack;
    }

    /**
     * <h4>Esegue l'elaborazione specifica da parte dello stato prima che il messaggio venga gestito.</h4>
     * <p>Questo metodo deve essere implementato nelle classi derivate per eseguire operazioni specifiche.</p>
     *
     * @param context Il contesto dello stato corrente.
     * @param update L'aggiornamento ricevuto dal bot.
     * @return Il messaggio {@link SendMessage} da inviare come risposta.
     * @throws Exception Se si verifica un errore durante l'elaborazione.
     */
    protected abstract SendMessage executePreHandle(StateContext context, Update update) throws Exception;

    /**
     * <h4>Esegue l'elaborazione specifica da parte dello stato dopo che il messaggio è stato gestito.</h4>
     * <p>Questo metodo deve essere implementato nelle classi derivate per eseguire operazioni specifiche.</p>
     *
     * @param context Il contesto dello stato corrente.
     * @param update L'aggiornamento ricevuto dal bot.
     * @return Il messaggio {@link SendMessage} da inviare come risposta.
     * @throws Exception Se si verifica un errore durante l'elaborazione.
     */
    protected abstract SendMessage executePostHandle(StateContext context, Update update) throws Exception;
}

