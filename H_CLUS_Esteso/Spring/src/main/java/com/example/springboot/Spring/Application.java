package com.example.springboot.Spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
    "com.example.springboot.Spring", // Package principale
    "Server.src.data",               // Package di Data
    "Server.src.clustering"         // Package di HierachicalClusterMiner
})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}