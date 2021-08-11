DROP DATABASE IF EXISTS paymybuddy;

CREATE DATABASE paymybuddy;

USE paymybuddy;

CREATE TABLE user
(
    email        VARCHAR(100) NOT NULL,
    password     VARCHAR(30)  NOT NULL,
    first_name   VARCHAR(100) NOT NULL,
    last_name    VARCHAR(100) NOT NULL,
    balance      DECIMAL(8, 2),
    account_bank INTEGER(6)   NOT NULL,
    PRIMARY KEY (email)
)
    ENGINE = innoDB;

INSERT INTO user(email, password, first_name, last_name, balance, account_bank)
VALUES ('tela@email.fr', 'monsuperpassword', 'Stella', 'Durant', 20.50, 251250),
       ('dada@email.fr', 'pass', 'Damien', 'Sanchez', 200, 255896),
       ('ggpassain@email.fr', 'corsica', 'Geraldine', 'Passain', 50, 359840),
       ('luluM@email.fr', 'portugalia', 'Lubin', 'Mendes', 20.0, 259873),
       ('lili@email.fr', 'ronaldo', 'Elisabeth', 'Dupond', 189.00, 783600)
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
# INSERT INTO transaction(amount, description, fees, emitter_email, receiver_email)
# VALUES (15.0, 'books', 0.0, 'dada@email.fr', 'luluM@email.fr'),
#        (5.0, 'diner', 0.0, 'dada@email.fr', 'ggpassain@email.fr'),
#        (25.0, 'cimena', 0.0, 'luluM@email.fr', 'dada@email.fr'),
#        (5.0, 'diner', 0.0, 'lili@email.fr', 'dada@email.fr'),
#        (25.0, 'cimena', 0.0, 'luluM@email.fr', 'lili@email.fr');

CREATE TABLE transfer
(
    transfer_id TINYINT AUTO_INCREMENT NOT NULL,
    date        DATETIME               NOT NULL,
    type        VARCHAR(10)            NOT NULL,
    amount      DECIMAL(8, 2)          NOT NULL,
    description VARCHAR(300),
    user_email  VARCHAR(100)           NOT NULL,
    PRIMARY KEY (transfer_id)
)
    ENGINE = innoDB;


CREATE TABLE friend
(
    user_email   VARCHAR(100) NOT NULL,
    friend_email VARCHAR(100) NOT NULL,
    date_added   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_email, friend_email)

)
    ENGINE = innoDB;
INSERT INTO friend(user_email, friend_email)
VALUES ('dada@email.fr', 'ggpassain@email.fr'),
       ('luluM@email.fr', 'dada@email.fr'),
       ('dada@email.fr', 'luluM@email.fr');

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


