package com.example.springboot.Spring.result;

/**
 * Rappresenta i risultati del clustering.
 */
public class ClusterResult {
    private String clusterId;       // Identificatore del cluster
    private String data;            // Dettagli del cluster
    private String description;     // Descrizione aggiuntiva

    // Costruttore
    public ClusterResult(String clusterId, String data, String description) {
        this.clusterId = clusterId;
        this.data = data;
        this.description = description;
    }

    // Getter e Setter
    public String getClusterId() {
        return clusterId;
    }

    public void setClusterId(String clusterId) {
        this.clusterId = clusterId;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "ClusterResult{" +
                "clusterId='" + clusterId + '\'' +
                ", data='" + data + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}