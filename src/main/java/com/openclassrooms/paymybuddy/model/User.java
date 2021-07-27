package com.openclassrooms.paymybuddy.model;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity
@Data
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

}
