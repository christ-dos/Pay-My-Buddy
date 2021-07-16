package com.openclassrooms.paymybuddy.model;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity
@Table(name="user")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
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

    @ManyToMany(
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )
    @JoinTable(
            name = "friend",
            joinColumns = @JoinColumn(name="user_email", referencedColumnName = "email"),
            inverseJoinColumns = @JoinColumn(name = "friend_email", referencedColumnName = "email")
    )
    private Set<User> friends = new HashSet<>();

    @ManyToMany(mappedBy = "friends")
    private Set<User> users = new HashSet<>();

    public User(String email, String password, String firstName, String lastName, Double balance, Integer accountBank) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.balance = balance;
        this.accountBank = accountBank;
    }

}
