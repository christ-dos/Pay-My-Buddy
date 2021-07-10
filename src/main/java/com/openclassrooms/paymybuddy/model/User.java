package com.openclassrooms.paymybuddy.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name="user")
@Data
public class User {
    @Id
    @Column(name="email")
    private String email;

    @Column(name="mot_passe")
    private String password;

    @Column(name="solde_compte")
    private Double accountBalance;

    @Column(name="numero_compte_bancaire")
    private Integer bankAccountNumber;

    @Column(name="amis_email")
    private String friendEmail;
}
