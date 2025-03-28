drop user MapUser@localhost;
flush privileges;
CREATE USER 'MapUser'@'localhost' IDENTIFIED BY 'map';
GRANT ALL PRIVILEGES ON MapDB.* TO 'MapUser'@'localhost';

DROP DATABASE IF EXISTS mapdb;
CREATE DATABASE IF NOT EXISTS mapdb;
USE mapdb;

CREATE TABLE exampleTab (
    X1 float,
    X2 float,
    X3 float
);

INSERT INTO exampleTab (X1, X2, X3) VALUES (1, 2, 0);
INSERT INTO exampleTab (X1, X2, X3) VALUES (0, 1, -1);
INSERT INTO exampleTab (X1, X2, X3) VALUES (1, 3, 5);
INSERT INTO exampleTab (X1, X2, X3) VALUES (1, 3, 4);
INSERT INTO exampleTab (X1, X2, X3) VALUES (2, 2, 0);
