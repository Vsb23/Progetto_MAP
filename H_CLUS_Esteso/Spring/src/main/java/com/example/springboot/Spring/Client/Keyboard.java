package com.example.springboot.Spring.Client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

/**
 * <h2>La classe Keyboard gestisce l'input da tastiera.</h2>
 * <p>
 * In particolare la classe permette di leggere le seguenti tipologie di input
 * <ul>
 *     <li>Stringhe</li>
 *     <li>Interi</li>
 * </ul>
 */
public class Keyboard {
	/** <h4>Indica se stampare messaggi di errore.</h4> */
	private static boolean printErrors = true;

	/** <h4>Conta il numero di errori verificatisi durante la lettura.</h4> */
	private static int errorCount = 0;

	/**
	 * <h4>Costruttore di default per la classe Keyboard. </h4>
	 */
	public Keyboard() {
		// Costruttore vuoto
	}

	/**
	 * <h4>Incrementa il conteggio degli errori e stampa il messaggio di errore
	 * se appropriato.</h4>
	 *
	 * @param str il messaggio di errore da stampare
	 */
	private static void error(String str) {
		errorCount++;
		if (printErrors)
			System.out.println(str);
	}

	// ************* Tokenized Input Stream Section ******************
	/** <h4>Il token corrente da leggere.</h4> */
	private static String current_token = null;

	/** <h4>Il lettore per la tokenizzazione degli input.</h4> */
	private static StringTokenizer reader;

	/**
	 * <h4>Permette di leggere l'input da tastiera.</h4>
	 */
	private static final BufferedReader in = new BufferedReader(
			new InputStreamReader(System.in));

	/**
	 * <h4>Restituisce il prossimo token di input, assumendo che potrebbe
	 * trovarsi su righe successive.</h4>
	 *
	 * @return il prossimo token di input
	 */
	private static String getNextToken() {
		return getNextToken(true);
	}

	/**
	 * <h4>Restituisce il prossimo token di input, che potrebbe già essere
	 * stato letto.</h4>
	 *
	 * @param skip se vero, ignora i token di delimitazione
	 * @return il prossimo token di input
	 */
	private static String getNextToken(boolean skip) {
		String token;

		if (current_token == null)
			token = getNextInputToken(skip);
		else {
			token = current_token;
			current_token = null;
		}

		return token;
	}

	/**
	 * <h4>Ottiene il prossimo token dall'input, che può provenire dalla
	 * riga di input corrente o da una successiva.</h4>
	 *
	 * @param skip se vero, ignora i token di delimitazione
	 * @return il prossimo token di input
	 */
	private static String getNextInputToken(boolean skip) {
		final String delimiters = " \t\n\r\f";
		String token = null;

		try {
			if (reader == null)
				reader = new StringTokenizer(in.readLine(), delimiters, true);

			while (token == null || ((delimiters.contains(token)) && skip)) {
				while (!reader.hasMoreTokens())
					reader = new StringTokenizer(in.readLine(), delimiters,
							true);

				token = reader.nextToken();
			}
		} catch (Exception exception) {
			token = null;
		}

		return token;
	}

	/**
	 * <h4>Verifica se non ci sono più token da leggere sulla riga di input corrente.</h4>
	 *
	 * @return true se non ci sono più token, false altrimenti
	 */
	public static boolean endOfLine() {
		return !reader.hasMoreTokens();
	}

	// ************* Reading Section *********************************

	/**
	 * <h4>Restituisce una stringa letta dall'input standard.</h4>
	 *
	 * @return la stringa letta, o null in caso di errore
	 */
	public static String readString() {
		StringBuilder str;

		try {
			str = new StringBuilder(getNextToken(false));
			while (!endOfLine()) {
				str.append(getNextToken(false));
			}
		} catch (Exception exception) {
			error("Error reading String data, null value returned.");
			str = null;
		}
		return str.toString();
	}

	/**
	 * <h4>Restituisce un intero letto dall'input standard.</h4>
	 *
	 * @return il valore intero letto, o Integer.MIN_VALUE in caso di errore
	 */
	public static int readInt() {
		String token = getNextToken();
		int value;
		try {
			value = Integer.parseInt(token);
		} catch (Exception exception) {
			error("Error reading int data, MIN_VALUE value returned.\n " +
					"Non e' stato inserito un intero. Il valore restituito in automatico e' negativo.");
			value = Integer.MIN_VALUE;
		}
		return value;
	}
}
