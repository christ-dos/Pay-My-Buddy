package com.openclassrooms.paymybuddy.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Builder;
import lombok.Data;

@Entity
@Table(name="user")
@Data
@Builder
public class User {
    @Id
    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name="balance")
    private Double balance;

    @Column(name="account_bank")
    private Integer accountBank;
}
