package com.openclassrooms.paymybuddy.model;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "utilisateur")
public class User {

    private String email;
    private String password;
    
}
