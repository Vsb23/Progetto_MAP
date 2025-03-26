package com.example.springboot.Spring.controller;

import com.example.springboot.Spring.result.ClusterResult;
import com.example.springboot.Spring.service.ClusterService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import Server.src.data.Data;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cluster")
public class ClusterController {
    
    private final ClusterService clusterService;

    @Autowired
    public ClusterController(ClusterService clusterService) {
        this.clusterService = clusterService;
    }

    // --- DTO Classes ---
    public static class LoadDataRequest {
        @NotBlank(message = "Il nome della tabella è obbligatorio")
        private String tableName;

        // Getter e Setter
        public String getTableName() { return tableName; }
        public void setTableName(String tableName) { this.tableName = tableName; }
    }

    public static class MineDendrogramRequest {
        @NotBlank(message = "Il nome della tabella è obbligatorio")
        private String tableName;

        @Min(value = 1, message = "La profondità deve essere almeno 1")
        private int depth;

        @Min(value = 0, message = "Tipo di distanza non valido")
        @Max(value = 2, message = "Tipo di distanza non valido")
        private int distanceType;

        // Getter e Setter
        public String getTableName() { return tableName; }
        public int getDepth() { return depth; }
        public int getDistanceType() { return distanceType; }
        // ... setters
    }

    // --- Endpoints ---
    
    @GetMapping("/results")
    public ResponseEntity<List<ClusterResult>> getClusterResults() {
        return ResponseEntity.ok(clusterService.getClusterResults());
    }

    @PostMapping("/load-data")
    public ResponseEntity<String> loadData(@Valid @RequestBody LoadDataRequest request) {
        try {
            clusterService.loadData(request.getTableName());
            return ResponseEntity.ok("Dati caricati dalla tabella: " + request.getTableName());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Errore: " + e.getMessage());
        }
    }

    
@PostMapping("/mine-dendrogram")
public ResponseEntity<?> mineDendrogram(@Valid @RequestBody MineDendrogramRequest request) {
    try {
        Data data = clusterService.loadData(request.getTableName());
        return ResponseEntity.ok(
            clusterService.mineDendrogram(
                data,
                request.getDepth(),
                request.getDistanceType()
            )
        );
    } catch (Exception e) {
        return ResponseEntity.badRequest().body("Errore: " + e.getMessage());
    }
}

@PostMapping("/save-dendrogram")
public ResponseEntity<?> saveDendrogram(
    @RequestParam @NotBlank String fileName
) {
    try {
        return ResponseEntity.ok(
            clusterService.saveDendrogram(fileName)
        );
    } catch (Exception e) {
        return ResponseEntity.badRequest().body("Errore: " + e.getMessage());
    }
}

@GetMapping("/load-dendrogram")
public ResponseEntity<?> loadDendrogramFromFile(
    @RequestParam @NotBlank String tableName,
    @RequestParam @NotBlank String fileName
) {
    try {
        Data data = clusterService.loadData(tableName);
        return ResponseEntity.ok(
            clusterService.loadDendrogramFromFile(fileName, data)
        );
    } catch (Exception e) {
        return ResponseEntity.badRequest().body("Errore: " + e.getMessage());
    }
}
}