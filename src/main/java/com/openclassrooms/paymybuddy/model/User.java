package com.openclassrooms.paymybuddy.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @NotBlank(message="User's email cannot be blank")
    private String email;

    private String password;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Min(value=0, message="User's balance cannot be negative")
    private Double balance;

    @Column(name="account_bank")
    private Integer accountBank;

    @ManyToMany(
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER
    )
    @JoinTable(
            name = "friend",
            joinColumns = @JoinColumn(name="user_email", referencedColumnName = "email"),
            inverseJoinColumns = @JoinColumn(name = "friend_email", referencedColumnName = "email")
    )
    private Set<User> friends = new HashSet<>();

    @ManyToMany(mappedBy = "friends")
    private Set<User> users = new HashSet<>();

    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER)
    @JoinColumn(name = "emitter_email")
    private Set<Transaction> transactionsEmitterSet =  new HashSet<>();

    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER)
    @JoinColumn(name = "receiver_email")
    private Set<Transaction> transactionsReceiverSet =  new HashSet<>();

}
