package com.example.springboot.Spring.Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;


/**
 * <h2> La classe MainTest funge da client per il server di clustering. </h2>
 * <p>
 * Il client si connette al server e gli invia delle richieste.
 * In particolare, il client può chiedere al server di caricare una tabella da un database e:
 * <ul>
 *     <li>Caricare un dendrogramma da file precedentemente serializzato</li>
 *     <li>Eseguire il clustering</li>
 * </ul>
 */
public class MainTest {
    /**
     * <h4>Canale di output verso il server.</h4>
     * Viene usato per inviare richieste al server.
     */
    private ObjectOutputStream out;

    /**
     * <h4>Canale di input dal server.</h4>
     * Viene usato per ricevere risposte dal server.
     */
    private ObjectInputStream in ;


    /**
     * <h4> Costruttore. </h4>
     * <p>
     * Si connette al server e inizializza i canali di input e output.
     *
     * @param ip   l'indirizzo ip del server
     * @param port la porta sulla quale il processo server è in ascolto
     * @throws IOException se si verifica un errore di I/O
     */
    public MainTest(String ip, int port) throws IOException{
        InetAddress addr = InetAddress.getByName(ip); //ip, l'InetAddress del Server da contattare
        System.out.println("addr = " + addr);
        Socket socket = new Socket(addr, port); //Port
        System.out.println(socket);

        out = new ObjectOutputStream(socket.getOutputStream()); //accesso allo stream in scrittura
        in = new ObjectInputStream(socket.getInputStream());
    }

    /**
     * <h4> Permette di scegliere una opzione tra quelle disponibili. </h4>
     * <p>
     * Stampa a video le opzioni disponibili e chiede all'utente di inserire un numero.
     * Controlla che il numero inserito sia valido e in caso contrario richiede all'utente di inserire un nuovo numero.
     * Le opzioni sono:
     * <ul>
     *  <li>Caricare un Dendrogramma da File</li>
     *  <li>Apprendere un Dendrogramma da Database</li>
     * </ul>
     *
     * @return La risposta dell'utente
     */
    private int menu(){
        int answer;
        System.out.println("Scegli una opzione");
        do{
            System.out.println("(1) Carica Dendrogramma da File");
            System.out.println("(2) Apprendi Dendrogramma da Database");
            System.out.print("Risposta:");
            answer=Keyboard.readInt();
        }while(answer<=0 || answer>2); //deve inserire o 1 o 2, altrimenti cicla sempre
        return answer;
    }

    /**
     * <h4> Permette di inviare al server il nome della tabella da caricare dal database. </h4>
     * <p>
     * Chiede all'utente il nome della tabella da caricare.
     * Invia al server il nome inserito e riceve dal server un messaggio di conferma.
     * Termina quando il messaggio di risposta dal server è "OK".
     *
     * @throws IOException            se si verifica un errore di I/O
     * @throws ClassNotFoundException se si verifica un errore di classe
     */
    private void loadDataOnServer() throws IOException, ClassNotFoundException {
        boolean flag=false;
        do {
            System.out.println("Nome tabella:");
            String tableName=Keyboard.readString();
            out.writeObject(0);
            out.writeObject(tableName);
            String risposta=(String)(in.readObject());
            if(risposta.equals("OK"))
                flag=true;
            else System.out.println(risposta);
        }while(!flag);
    }

    /**
     * <h4> Permette di caricare un clustering da un file. </h4>
     * <p>
     * Chiede all'utente di inserire il nome dell'archivio (comprensivo di estensione) da cui caricare il clustering.
     * Invia al server il nome del file e riceve dal server un messaggio di conferma.
     * Se il messaggio di conferma è "OK" il metodo riceve dal server il clustering e lo stampa a video,
     * altrimenti viene stampato il messaggio di errore inviato dal Server e termina l'esecuzione.
     *
     * @throws IOException            se si verifica un errore di I/O
     * @throws ClassNotFoundException se si verifica un errore di classe
     */
    private void loadDedrogramFromFileOnServer() throws IOException, ClassNotFoundException {
        System.out.println("Inserire il nome dell'archivio (comprensivo di estensione):");
        String fileName=Keyboard.readString();
        out.writeObject(2);
        out.writeObject(fileName);
        String risposta= (String)(in.readObject());
        if(risposta.equals("OK")) {
            System.out.println(in.readObject()); // stampo il dendrogramma che il server mi sta inviando
        }
        else
            System.out.println(risposta); // stampo il messaggio di errore
    }
    /**
     * <h4> Permette di richiedere al server di creare un clustering. </h4>
     * <p>
     * Il cluster viene creato sulla tabella scelta in <code>loadDataOnServer</code>.
     * Il metodo chiede all'utente di introdurre la profondita' del dendrogramma e di scegliere il tipo di distanza tra Cluster.
     * Infine, chiede di inserire il nome dell'archivio (comprensivo di estensione) su cui serializzare il clustering.
     * Se il messaggio di conferma è "OK" allora viene stampato a video il dendrogramma creato.
     * Se il messaggio di conferma non è "OK" il metodo stampa il messaggio di errore ricevuto.
     *
     * @throws IOException            se si verifica un errore di I/O
     * @throws ClassNotFoundException se una classe non viene trovata
     */
    private void mineDedrogramOnServer() throws IOException, ClassNotFoundException {
        out.writeObject(1);
        System.out.println("Introdurre la profondita'  del dendrogramma");
        int depth = Keyboard.readInt();
        out.writeObject(depth);
        int dType=-1;
        do {
            System.out.println("Distanza: single-link (1), average-link (2):");
            dType=Keyboard.readInt();
        }while (dType<=0 || dType>2);
        out.writeObject(dType);

        String risposta = (String) (in.readObject());
        if(risposta.equals("OK")) {
            System.out.println(in.readObject()); // stampo il dendrogramma che il server mi sta inviando
            System.out.println("Inserire il nome dell'archivio (comprensivo di estensione):");
            String fileName=Keyboard.readString();
            out.writeObject(fileName);
            risposta = (String) in.readObject();
            System.out.println(risposta);
        }
        else
            System.out.println(risposta); // stampo il messaggio di errore
    }

    /**
     * <h4> Permette di avviare il client. </h4>
     * <p>
     * Prende in input da linea di comando l'indirizzo ip e la porta del server a cui connettersi.
     * Viene creato un oggetto di tipo MainTest e viene gestita l'interazione tra client e server.
     * Termina quando termina di eseguire le richieste dell'utente o quando si verifica un errore che non può essere gestito.
     *
     * @param args indirizzo ip/dns e porta del server a cui connettersi
     */
    public static void main(String[] args) {
        String ip = "127.0.0.1";
        int port = 8080;
        MainTest main;
            try {
                main = new MainTest(ip, port);

                main.loadDataOnServer();
                int scelta = main.menu();
                if (scelta == 1)
                    main.loadDedrogramFromFileOnServer();
                else
                    main.mineDedrogramOnServer();
            } catch (IOException | ClassNotFoundException e) {
                System.out.println(e.getMessage());
            }
    }
}
