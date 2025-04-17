package data;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * <h2>La classe {@code Example} rappresenta un esempio, vettore di valori reali,
 * utilizzato come entità nei calcoli del clustering agglomerativo.</h2>
 * <p>
 * Un oggetto {@code Example} è un contenitore di valori numerici (di tipo {@code Double}),
 * rappresentati come una lista, con la possibilità di iterare sui valori, aggiungerli e calcolare
 * la distanza euclidea tra esempi di dimensioni uguali.
 * </p>
 *
 * <p>Implementa l'interfaccia {@link Iterable}, permettendo l'iterazione sui valori contenuti.</p>
 */
public class Example implements Iterable<Double> {
    /** <h4>Lista di valori reali che rappresentano un esempio.</h4> */
    private final List<Double> example;

    /**
     * <h4>Costruttore della classe {@code Example} che inizializza un esempio vuoto.</h4>
     */
    public Example() {
        example = new LinkedList<>();
    }

    /**
     * <h4>Restituisce un iteratore sui valori dell'esempio.</h4>
     *
     * @return Un iteratore di tipo {@code Iterator<Double>}.
     */
    public Iterator<Double> iterator() {
        return example.iterator();
    }

    /**
     * <h4>Aggiunge un valore reale all'esempio.</h4>
     *
     * @param v Il valore di tipo {@code Double} da aggiungere all'esempio.
     */
    public void add(Double v) {
        example.add(v);
    }

    /**
     * <h4>Restituisce il valore dell'esempio presente all'indice specificato.</h4>
     *
     * @param index L'indice del valore da recuperare.
     * @return Il valore di tipo {@code Double} presente all'indice specificato.
     */
    public Double get(int index) {
        return example.get(index);
    }

    /**
     * <h4>Calcola la distanza euclidea tra l'istanza di esempio corrente e un altro oggetto {@code Example}.</h4>
     * <p>
     * La distanza viene calcolata solo se entrambi gli esempi hanno lo stesso numero di elementi.
     * Se le dimensioni sono diverse, viene lanciata un'eccezione {@link InvalidSizeException}.
     * </p>
     *
     * @param newE Un altro oggetto {@code Example} con cui calcolare la distanza.
     * @return La distanza euclidea tra i due esempi.
     * @throws InvalidSizeException Se gli esempi hanno dimensioni diverse.
     */
    public double distance(Example newE) throws InvalidSizeException {
        if(example.size() != newE.example.size())
            throw new InvalidSizeException("Gli esempi hanno dimensioni diverse!");

        double sum = 0.0;
        Iterator<Double> iterator1 = example.iterator();
        Iterator<Double> iterator2 = newE.iterator();

        while (iterator1.hasNext()) {  // Si può usare solo uno degli iteratori qui
            double diff = iterator1.next() - iterator2.next();
            sum += Math.pow(diff, 2);
        }

        return sum;
    }

    /**
     * <h4>Restituisce una rappresentazione dell'esempio, sotto forma di un vettore.</h4>
     * <p>
     * I valori sono presentati tra parentesi quadre e separati da virgole.
     * </p>
     *
     * @return Una stringa che rappresenta i valori contenuti nell'esempio.
     */
    public String toString() {
        StringBuilder s = new StringBuilder("[");

        for(Double e: example)
            s.append(e).append(",");
        String modifiedStr = s.substring(0, s.length() - 1);
        modifiedStr += "]";

        return modifiedStr;
    }
}
