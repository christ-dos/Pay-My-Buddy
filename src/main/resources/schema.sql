
DROP DATABASE IF EXISTS paymybuddy;

CREATE DATABASE paymybuddy;

USE paymybuddy;

CREATE TABLE  user (
                email VARCHAR(100) NOT NULL,
                password VARCHAR(30) NOT NULL,
                first_name VARCHAR(30) NOT NULL,
                last_name VARCHAR(30) NOT NULL,
                balance DECIMAL(8,2),
                account_bank INTEGER(6) NOT NULL,
                PRIMARY KEY (email)
);

INSERT INTO  user(email, password, first_name, last_name, balance, account_bank)
       VALUES('tela@email.fr', 'monsuperpassword', 'Stella', 'Durant', 20.50, 251250),
	     ('dada@email.fr', 'monpassword', 'Daminen', 'Sanchez', 3.50, 255896),
	     ('ggpassain@email.fr', 'corsica', 'Geraldine', 'Passain', 22.80, 359840),
	     ('luluM@email.fr', 'portugalia', 'Lubin', 'Mendes', 18.98, 259873),
	     ('lili@email.fr', 'ronaldo', 'Elisabeth', 'Dupond', 189.00, 783600)
;

CREATE TABLE transaction (
                id TINYINT AUTO_INCREMENT NOT NULL,
                date DATETIME NOT NULL,
                amount DECIMAL(8,8) NOT NULL,
                description VARCHAR(300),
                fees DECIMAL(8,8) NOT NULL,
                emitter_email VARCHAR(100) NOT NULL,
                receiver_email VARCHAR(100) NOT NULL,
                PRIMARY KEY (id)
);


CREATE TABLE transfer (
                id TINYINT AUTO_INCREMENT NOT NULL,
                date DATETIME NOT NULL,
                type VARCHAR(10) NOT NULL,
                amount DECIMAL(8,2) NOT NULL,
                description VARCHAR(300),
                user_email VARCHAR(100) NOT NULL,
                PRIMARY KEY (id)
);


CREATE TABLE friend (
                user_email VARCHAR(100) NOT NULL,
                friend_email VARCHAR(100) NOT NULL,
                PRIMARY KEY (user_email, friend_email)
);


ALTER TABLE friend ADD CONSTRAINT user_friend_fk
FOREIGN KEY (user_email)
REFERENCES user (email)
ON DELETE NO ACTION
ON UPDATE CASCADE;

ALTER TABLE friend ADD CONSTRAINT user_friend_fk1
FOREIGN KEY (friend_email)
REFERENCES user (email)
ON DELETE NO ACTION
ON UPDATE CASCADE ;

ALTER TABLE transfer ADD CONSTRAINT user_transfer_fk
FOREIGN KEY (user_email)
REFERENCES user (email)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE transaction ADD CONSTRAINT user_transaction_fk
FOREIGN KEY (emitter_email)
REFERENCES user (email)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE transaction ADD CONSTRAINT user_transaction_fk1
FOREIGN KEY (receiver_email)
REFERENCES user (email)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

commit;


