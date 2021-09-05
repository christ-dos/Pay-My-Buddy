DROP DATABASE IF EXISTS paymybuddy;

CREATE DATABASE paymybuddy;

USE paymybuddy;

CREATE TABLE user
(
    email        VARCHAR(100) NOT NULL,
    password     VARCHAR(100) NOT NULL,
    first_name   VARCHAR(100) NOT NULL,
    last_name    VARCHAR(100) NOT NULL,
    balance      DECIMAL(8, 2),
    account_bank INTEGER(6)   NOT NULL,
    PRIMARY KEY (email)
)
    ENGINE = innoDB;

INSERT INTO user(email, password, first_name, last_name, balance, account_bank)
VALUES ('tela@email.fr', 'monsuperpassword', 'Stella', 'Durant', 20.50, 251250),
       ('dada@email.fr', '$2a$10$2K11L/fq6fmlHt3K7Nq.LeBpsNYiaLsb0tCh3z3w/h4MIi2FtB66.', 'Damien', 'Sanchez', 200, 255896),
       ('ggpassain@email.fr', 'corsica', 'Geraldine', 'Passain', 50, 359840),
       ('luluM@email.fr', 'portugalia', 'Lubin', 'Mendes', 20.0, 259873),
       ('lili@email.fr', '$2y$10$wN5A7Byk5jyX0d0fb4NYOu988pzJRdBtPaY9PxA2.Ck1IN.EZusIq', 'Elisabeth', 'Dupond', 189.00, 783600)
;

CREATE TABLE transaction
(
    transaction_id TINYINT AUTO_INCREMENT NOT NULL,
    date           TIMESTAMP              NOT NULL DEFAULT NOW(),
    amount         DECIMAL(8, 2)          NOT NULL,
    description    VARCHAR(300),
    fees           DECIMAL(8, 2),
    emitter_email  VARCHAR(100)           NOT NULL,
    receiver_email VARCHAR(100)           NOT NULL,
    PRIMARY KEY (transaction_id)
)
    ENGINE = innoDB;

INSERT INTO transaction(transaction_id, date, amount, description, fees, emitter_email, receiver_email)
VALUES (1, NOW(), 15.0, 'Books',0.075,'dada@email.fr', 'luluM@email.fr');

CREATE TABLE transfer
(
    transfer_id        TINYINT AUTO_INCREMENT NOT NULL,
    date               TIMESTAMP              NOT NULL default NOW(),
    transfer_type      ENUM ('CREDIT','DEBIT'),
    amount             DECIMAL(8, 2)          NOT NULL,
    description        VARCHAR(300),
    user_email         VARCHAR(100)           NOT NULL,
    post_trade_balance DECIMAL(8, 2)          NOT NULL,
    PRIMARY KEY (transfer_id)
)
    ENGINE = innoDB;


CREATE TABLE friend
(
    user_email   VARCHAR(100) NOT NULL,
    friend_email VARCHAR(100) NOT NULL,
    date_added   TIMESTAMP    NOT NULL DEFAULT NOW(),
    PRIMARY KEY (user_email, friend_email)
)
    ENGINE = innoDB;

INSERT INTO friend(user_email, friend_email)
VALUES ('dada@email.fr', 'ggpassain@email.fr');

ALTER TABLE friend
    ADD CONSTRAINT user_friend_fk
        FOREIGN KEY (user_email)
            REFERENCES user (email)
            ON
                DELETE NO ACTION
            ON UPDATE NO ACTION;

ALTER TABLE friend
    ADD CONSTRAINT user_friend_fk1
        FOREIGN KEY (friend_email)
            REFERENCES user (email)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION;

ALTER TABLE transfer
    ADD CONSTRAINT user_transfer_fk
        FOREIGN KEY (user_email)
            REFERENCES user (email)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION;

ALTER TABLE transaction
    ADD CONSTRAINT user_transaction_fk
        FOREIGN KEY (emitter_email)
            REFERENCES user (email)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION;

ALTER TABLE transaction
    ADD CONSTRAINT user_transaction_fk1
        FOREIGN KEY (receiver_email)
            REFERENCES user (email)
            ON DELETE NO ACTION
            ON UPDATE NO ACTION;

commit;


