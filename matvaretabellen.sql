DROP TABLE IF EXISTS ingredienser;
DROP TABLE IF EXISTS logg;
DROP TABLE IF EXISTS vekt;
DROP TABLE IF EXISTS brukerBenevningMål;
DROP TABLE IF EXISTS måltider;
DROP TABLE IF EXISTS benevninger;
DROP TABLE IF EXISTS matvaretabellen;
DROP TABLE IF EXISTS users;
/*DROP TRIGGER IF EXISTS userBenevning;*/

CREATE TABLE benevninger(
	benevningId INTEGER AUTO_INCREMENT,
	navn varchar(30),
	benevning varchar(10),
	PRIMARY KEY(benevningId)
);


CREATE TABLE matvaretabellen(
	`matvareId` INTEGER AUTO_INCREMENT,
	`matvare` varchar(150) NOT NULL UNIQUE,
	`Spiselig del` DECIMAL(9,2),
	`Vann` DECIMAL(9,2),
	`Kilojoule` DECIMAL(9,2),
	`Kilokalorier` DECIMAL(9,2) NOT NULL,
	`Fett` DECIMAL(9,2),
	`Mettet` DECIMAL(9,2),
	`C12:0` DECIMAL(9,2),
	`C14:0` DECIMAL(9,2),
	`C16:0` DECIMAL(9,2),
	`C18:0` DECIMAL(9,2),
	`Trans` DECIMAL(9,2),
	`Enumettet` DECIMAL(9,2),
	`C16:1 sum` DECIMAL(9,2),
	`C18:1 sum` DECIMAL(9,2),
	`Flerumettet` DECIMAL(9,2),
	`C18:2n-6` DECIMAL(9,2),
	`C18:3n-3` DECIMAL(9,2),
	`C20:3n-3` DECIMAL(9,2),
	`C20:3n-6` DECIMAL(9,2),
	`C20:4n-3` DECIMAL(9,2),
	`C20:4n-6` DECIMAL(9,2),
	`C20:5n-3 (EPA)` DECIMAL(9,2),
	`C22:5n-3 (DPA)` DECIMAL(9,2),
	`C22:6n-3 (DHA)` DECIMAL(9,2),
	`Omega-3` DECIMAL(9,2),
	`Omega-6` DECIMAL(9,2),
	`Kolesterol` DECIMAL(9,2),
	`Karbohydrat` DECIMAL(9,2),
	`Stivelse` DECIMAL(9,2),
	`Mono+disakk` DECIMAL(9,2),
	`Sukker, tilsatt` DECIMAL(9,2),
	`Kostfiber` DECIMAL(9,2),
	`Protein` DECIMAL(9,2),
	`Salt` DECIMAL(9,2),
	`Alkohol` DECIMAL(9,2),
	`Vitamin A` DECIMAL(9,2),
	`Retinol` DECIMAL(9,2),
	`Beta-karoten` DECIMAL(9,2),
	`Vitamin D` DECIMAL(9,2),
	`Vitamin E` DECIMAL(9,2),
	`Tiamin` DECIMAL(9,2),
	`Riboflavin` DECIMAL(9,2),
	`Niacin` DECIMAL(9,2),
	`Vitamin B6` DECIMAL(9,2),
	`Folat` DECIMAL(9,2),
	`Vitamin B12` DECIMAL(9,2),
	`Vitamin C` DECIMAL(9,2),
	`Kalsium` DECIMAL(9,2),
	`Jern` DECIMAL(9,2),
	`Natrium` DECIMAL(9,2),
	`Kalium` DECIMAL(9,2),
	`Magnesium` DECIMAL(9,2),
	`Sink` DECIMAL(9,2),
	`Selen` DECIMAL(9,2),
	`Kopper` DECIMAL(9,2),
	`Fosfor` DECIMAL(9,2),
	`Jod` DECIMAL(9,2),PRIMARY KEY(matvareId));

CREATE TABLE users(
	brukerId INTEGER AUTO_INCREMENT,
	brukernavn varchar(150) NOT NULL UNIQUE,
	passord varchar(150) NOT NULL,
	admin boolean NOT NULL DEFAULT 0,
	PRIMARY KEY(brukerId)
);

INSERT INTO users(brukernavn,passord,admin) VALUES('admin@tarves.no',' ',false);

CREATE TABLE vekt(
	vektId INTEGER AUTO_INCREMENT,
	brukerId INTEGER NOT NULL,
	kilo DECIMAL(5,2) NOT NULL,
	dato DATE NOT NULL,
	FOREIGN KEY(brukerId) REFERENCES users(brukerId),
	PRIMARY KEY(vektId)
);

CREATE TABLE brukerBenevningMål(
	benevningId INTEGER,
	brukerId INTEGER,
	aktiv boolean,
	øvreMål INTEGER,
	nedreMål INTEGER,
	PRIMARY KEY(benevningId,brukerId),
	FOREIGN KEY(benevningId) REFERENCES benevninger(benevningId),
	FOREIGN KEY(brukerId) REFERENCES users(brukerId)
);

CREATE TABLE måltider(
	måltidId INTEGER AUTO_INCREMENT,
	navn varchar(150) UNIQUE,
	brukerId INTEGER NOT NULL,
	PRIMARY KEY(måltidId),
	FOREIGN KEY(brukerId) REFERENCES users(brukerId)
);

CREATE TABLE ingredienser(
	ingredienseId INTEGER AUTO_INCREMENT,
	matvareId INTEGER NOT NULL,
	måltidId INTEGER NOT NULL,
	mengde DECIMAL(8,2) NOT NULL,
	PRIMARY KEY(ingredienseId),
	FOREIGN KEY(matvareId) REFERENCES matvaretabellen(matvareId),
	FOREIGN KEY(måltidId) REFERENCES måltider(måltidId)
);

CREATE TABLE logg(
	loggId INTEGER AUTO_INCREMENT,
	matvareId INTEGER NOT NULL,
	mengde DECIMAL(8,2) NOT NULL,
	dato DATE NOT NULL,
	brukerId INTEGER NOT NULL,
	PRIMARY KEY(loggId),
	FOREIGN KEY(matvareId) REFERENCES matvaretabellen(matvareId),
	FOREIGN KEY(brukerId) REFERENCES users(brukerId)
);

ALTER TABLE matvaretabellen
ADD `brukerId` INTEGER; 

UPDATE matvaretabellen
SET brukerId = 1;

ALTER TABLE matvaretabellen
ADD FOREIGN KEY (brukerId) REFERENCES users(brukerId); 

ALTER TABLE users
ADD resetToken varchar(140) UNIQUE;
ALTER TABLE users
ADD epostAktivert BOOLEAN DEFAULT false;

/*DELIMITER ::

CREATE TRIGGER userBenevning
AFTER INSERT ON users
FOR EACH ROW
BEGIN
	DECLARE done INT DEFAULT FALSE;
	DECLARE b_Id INTEGER;
	DECLARE cur CURSOR FOR SELECT benevningId FROM benevninger;
	DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
	OPEN cur;
	luup: LOOP
		FETCH cur INTO b_Id;
		IF done THEN
			LEAVE luup;
		END IF;
		INSERT INTO brukerBenevningMål(benevningId,brukerId,aktiv) VALUES(b_Id,NEW.brukerId,false);
	END LOOP;
END::

DELIMITER ;
*/
DELIMITER ::

CREATE PROCEDURE slettMåltid
(
	IN p_måltidId INTEGER,
	IN p_brukerId INTEGER,
	OUT p_affectedRows INTEGER
)

BEGIN
	DECLARE valid INTEGER;
	SELECT 1 into valid FROM måltider WHERE brukerId = p_brukerId AND måltidId = p_måltidId;
	IF valid = 1 THEN
		DELETE FROM ingredienser WHERE måltidId = p_måltidId;
		SET p_affectedRows = ROW_COUNT();
		DELETE FROM måltider WHERE brukerId = p_brukerId AND måltidId = p_måltidId;
		SET p_affectedRows = p_affectedRows + ROW_COUNT();
	ELSE
		SET p_affectedRows = 0;
	END IF;	
END::

DELIMITER ;