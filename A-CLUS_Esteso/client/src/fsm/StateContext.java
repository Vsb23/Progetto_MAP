package fsm;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Stack;
import java.util.regex.Pattern;

/**
 * <h2>La classe {@code StateContext} gestisce il contesto di uno stato nell'ambito di un sistema a stati finiti (FSM).</h2>
 * <p>Mantiene la cronologia degli stati, permette di cambiare stato e di eseguire le operazioni di pre-handle e post-handle
 * in base agli aggiornamenti ricevuti, come i messaggi inviati dall'utente.
 * {@code StateContext} tiene traccia della storia degli stati tramite uno stack e gestisce i dati associati
 * allo stato corrente tramite l'oggetto {@link Data}.
 * Inoltre, gestisce il comando speciale "/indietro" che permette di tornare allo stato precedente, se consentito.</p>
 */
public class StateContext {
    /** <h4>Espressione regolare per il comando "/indietro", che consente di tornare allo stato precedente.</h4> */
    private static final Pattern backRegex = Pattern.compile("(?i)^/?indietro$");

    /** <h4>Lo stack che mantiene la cronologia degli stati.</h4> */
    private final Stack<State> stateHistory = new Stack<>();

    /** <h4>I dati associati al contesto corrente.</h4> */
    private Data data;

    /**
     * <h4>Costruisce un nuovo {@code StateContext} con lo stato iniziale specificato.</h4>
     *
     * @param initialState Lo stato iniziale da cui partire.
     */
    public StateContext(State initialState) {
        stateHistory.push(initialState);
        this.data = new Data();
    }

    /**
     * <h4>Cambia lo stato corrente con uno nuovo.</h4>
     * <p>Se il nuovo stato consente il ritorno allo stato precedente, questo viene aggiunto alla cronologia degli stati.
     * Altrimenti, la cronologia viene resettata e il nuovo stato è l'unico presente nella stack.</p>
     *
     * @param newState Lo stato con cui sostituire quello corrente.
     */
    public void changeState(State newState) {
        if (newState.isAllowBack()) {
            stateHistory.push(newState);
        } else {
            stateHistory.clear();
            stateHistory.push(newState);
        }
        stateHistory.peek().resetState();
    }

    /**
     * <h4>Ripristina lo stato precedente nella cronologia degli stati, se consentito.</h4>
     * <p>Questo metodo rimuove l'ultimo stato nella pila degli stati (`stateHistory`).
     */
    public void rollbackState() {
        if (stateHistory.size() > 1 && stateHistory.peek().isAllowBack()) {
            stateHistory.pop();
        }
    }

    /**
     * <h4>Gestisce l'aggiornamento ricevuto, decidendo se eseguire l'operazione di pre-handle o post-handle
     * in base allo stato corrente.</h4>
     * <p>Se l'utente ha inviato il comando "/indietro", viene eseguita l'operazione di ritorno allo stato precedente.</p>
     *
     * @param update L'aggiornamento ricevuto dal bot, che contiene il messaggio dell'utente.
     * @return Il messaggio {@link SendMessage} da inviare come risposta all'utente.
     */
    public SendMessage handleMessage(Update update) {
        State currentState = stateHistory.peek();
        if (update.getMessage().hasText() && backRegex.matcher(update.getMessage().getText()).matches()) {
            if (stateHistory.size() > 1 && currentState.isAllowBack()) {
                stateHistory.pop();
                currentState = stateHistory.peek();
                currentState.resetState();
            }
        }

        if (!currentState.isPostHandle()) {
            return currentState.preHandle(this, update);
        }
        return currentState.postHandle(this, update);
    }

    /**
     * <h4>Restituisce i dati associati al contesto corrente.</h4>
     *
     * @return L'oggetto {@link Data} contenente i dati del contesto.
     */
    public Data getData() {
        return data;
    }

    /**
     * <h4>Imposta i dati per il contesto corrente.</h4>
     *
     * @param data L'oggetto {@link Data} che contiene i nuovi dati da impostare.
     */
    public void setData(Data data) {
        this.data = data;
    }
}
