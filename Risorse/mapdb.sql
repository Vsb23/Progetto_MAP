DROP DATABASE IF EXISTS mapdb;
CREATE DATABASE mapdb;
USE mapdb;


DROP USER IF EXISTS 'MapUser'@'localhost';
FLUSH PRIVILEGES;


CREATE USER IF NOT EXISTS 'MapUser'@'localhost' IDENTIFIED BY 'map';


GRANT ALL PRIVILEGES ON mapdb.* TO 'MapUser'@'localhost';
FLUSH PRIVILEGES;




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


