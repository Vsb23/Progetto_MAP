package com.example.springboot.Spring.service;

import Server.src.clustering.HierachicalClusterMiner;
import Server.src.data.Data;
import Server.src.data.NoDataException;
import Server.src.distance.SingleLinkDistance;
import Server.src.distance.AverageLinkDistance;
import Server.src.distance.ClusterDistance;

import org.springframework.stereotype.Service;

import com.example.springboot.Spring.result.ClusterResult;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

@Service
public class ClusterService {
    private HierachicalClusterMiner currentMiner;
    
    // Metodo esistente
    public List<ClusterResult> getClusterResults() {
        List<ClusterResult> results = new ArrayList<>();
        
        // Verifica se esiste un miner e se ha risultati
        if (currentMiner != null) {
            try {
                // Supponiamo che currentMiner abbia un metodo per ottenere i risultati
                // Questo è un esempio e potrebbe dover essere adattato in base all'implementazione reale
                String dendrogramString = currentMiner.toString();
                results.add(new ClusterResult("cluster1", dendrogramString, "Cluster generato"));
            } catch (Exception e) {
                // Gestione dell'errore
                e.printStackTrace();
            }
        } else {
            // Aggiungi un messaggio per indicare che non ci sono risultati
            results.add(new ClusterResult("no-data", "", "Nessun cluster disponibile. Esegui prima il clustering,coglione."));
        }
        
        return results;
    }
    
    // Carica i dati
    public Data loadData(String tableName) throws NoDataException {
        return new Data(tableName);
    }
    
    // Esegue il clustering e restituisce i risultati
    public Map<String, Object> mineDendrogram(Data data, int depth, int distanceType) throws Exception {
        ClusterDistance distance = (distanceType == 1) ? 
            new SingleLinkDistance() : new AverageLinkDistance();
            
        currentMiner = new HierachicalClusterMiner(depth);
        currentMiner.mine(data, distance);
        
        Map<String, Object> result = new HashMap<>();
        result.put("status", "OK");
        result.put("dendrogram", currentMiner.toString(data));
        
        return result;
    }
    
    // Salva il dendrogramma
    public String saveDendrogram(String fileName) throws Exception {
        if (currentMiner == null) {
            throw new IllegalStateException("Nessun dendrogramma disponibile per il salvataggio");
        }
        
        currentMiner.salva(fileName);
        return "Dendrogramma salvato correttamente.";
    }
    
    // Carica il dendrogramma da file
    public Map<String, Object> loadDendrogramFromFile(String fileName, Data data) throws Exception {
        currentMiner = HierachicalClusterMiner.loadHierachicalClusterMiner(fileName);
        
        if (currentMiner.getDepth() > data.getNumberOfExamples()) {
            throw new IllegalStateException("Profondità del dendrogramma maggiore del numero di esempi nella tabella scelta.");
        } else if (currentMiner.getLevel0Length() > data.getNumberOfExamples()) {
            throw new IllegalStateException("Il numero di esempi nella tabella scelta è minore del numero di esempi con cui è stato salvato l'oggetto h-clus precedentemente serializzato.");
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("status", "OK");
        result.put("dendrogram", currentMiner.toString(data));
        
        return result;
    }
}