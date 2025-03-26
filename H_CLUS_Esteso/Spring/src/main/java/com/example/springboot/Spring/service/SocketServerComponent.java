package com.example.springboot.Spring.service;

import com.example.springboot.Spring.result.ClusterResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import Server.src.data.Data;
import Server.src.clustering.HierachicalClusterMiner;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class SocketServerComponent {

    private final ClusterService clusterService;
    private ServerSocket serverSocket;
    private ExecutorService executor;
    private boolean running = false;
    private Data currentData;

    @Autowired
    public SocketServerComponent(ClusterService clusterService) {
        this.clusterService = clusterService;
    }

    @PostConstruct
    public void start() {
        try {
            serverSocket = new ServerSocket(8080);
            executor = Executors.newFixedThreadPool(10);
            running = true;
            
            new Thread(() -> {
                while (running) {
                    try {
                        Socket clientSocket = serverSocket.accept();
                        executor.submit(() -> handleClientConnection(clientSocket));
                    } catch (IOException e) {
                        if (running) {
                            System.err.println("Errore durante l'accettazione della connessione client: " + e.getMessage());
                        }
                    }
                }
            }).start();
            
            System.out.println("Server socket avviato sulla porta 8080");
        } catch (IOException e) {
            System.err.println("Impossibile avviare il server socket: " + e.getMessage());
        }
    }

    private void handleClientConnection(Socket clientSocket) {
        try (
            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())
        ) {
            boolean continueProcessing = true;
            
            // Prima richiesta: caricamento dei dati
            try {
                int operation = (int) in.readObject();
                if (operation == 0) {
                    String tableName = (String) in.readObject();
                    try {
                        this.currentData = clusterService.loadData(tableName);
                        out.writeObject("OK");
                    } catch (Exception e) {
                        out.writeObject("Errore nel caricamento della tabella: " + e.getMessage());
                    }
                }
            } catch (Exception e) {
                out.writeObject("Errore nella comunicazione: " + e.getMessage());
                return;
            }
            
            // Seconda richiesta: operazione principale
            try {
                int operation = (int) in.readObject();
                
                if (operation == 1) {  // Mining del dendrogramma
                    int depth = (int) in.readObject();
                    int distanceType = (int) in.readObject();
                    
                    if (currentData == null) {
                        out.writeObject("Errore: nessun dato caricato.");
                        return;
                    }
                    
                    try {
                        Map<String, Object> result = clusterService.mineDendrogram(currentData, depth, distanceType);
                        out.writeObject("OK");
                        out.writeObject(result.toString());  // Invia il risultato come stringa
                        
                        // Richiesta del nome del file per il salvataggio
                        String fileName = (String) in.readObject();
                        String saveResult = clusterService.saveDendrogram(fileName);
                        out.writeObject(saveResult);
                    } catch (Exception e) {
                        out.writeObject("Errore nell'esecuzione del clustering: " + e.getMessage());
                    }
                }
                else if (operation == 2) {  // Caricamento del dendrogramma da file
                    String fileName = (String) in.readObject();
                    
                    if (currentData == null) {
                        out.writeObject("Errore: nessun dato caricato.");
                        return;
                    }
                    
                    try {
                        Map<String, Object> result = clusterService.loadDendrogramFromFile(fileName, currentData);
                        out.writeObject("OK");
                        out.writeObject(result.toString());  // Invia il risultato come stringa
                    } catch (Exception e) {
                        out.writeObject("Errore nel caricamento del dendrogramma: " + e.getMessage());
                    }
                }
            } catch (Exception e) {
                out.writeObject("Errore nella comunicazione: " + e.getMessage());
            }
            
        } catch (IOException e) {
            System.err.println("Errore durante la gestione della connessione client: " + e.getMessage());
        }
    }

    @PreDestroy
    public void stop() {
        running = false;
        if (executor != null) {
            executor.shutdown();
        }
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                System.err.println("Errore durante la chiusura del server socket: " + e.getMessage());
            }
        }
        System.out.println("Server socket fermato");
    }
}