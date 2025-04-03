-- Rimuovi l'utente solo se esiste
DROP USER IF EXISTS 'MapUser'@'localhost';
FLUSH PRIVILEGES;

-- Crea l'utente se non esiste e assegna i privilegi
CREATE USER IF NOT EXISTS 'MapUser'@'localhost' IDENTIFIED BY 'map';
GRANT ALL PRIVILEGES ON MapDB.* TO 'MapUser'@'localhost';
FLUSH PRIVILEGES;

-- Elimina il database se esiste e ricrealo
DROP DATABASE IF EXISTS mapdb;
CREATE DATABASE mapdb;
USE mapdb;

CREATE TABLE exampleTab2 (
    X1 float,
    X2 float,
    X3 float
);

INSERT INTO exampleTab2 (X1, X2, X3) VALUES (1, 2, 0);
INSERT INTO exampleTab2 (X1, X2, X3) VALUES (0, 1, -1);
INSERT INTO exampleTab2 (X1, X2, X3) VALUES (1, 3, 5);
INSERT INTO exampleTab2 (X1, X2, X3) VALUES (1, 3, 4);
INSERT INTO exampleTab2 (X1, X2, X3) VALUES (2, 2, 0);


