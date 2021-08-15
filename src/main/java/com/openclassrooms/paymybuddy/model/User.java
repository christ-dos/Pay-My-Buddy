package com.openclassrooms.paymybuddy.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that manage the entity User
 *
 * @author Christine Duarte
 */
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

    /**
     * A String containing the email of the user, id that identify him
     * this field cannot be blank in the UI
     */
    @Id
    @NotBlank(message = "User's email cannot be blank")
    private String email;

    /**
     * A String containing the password
     */
//    @NotBlank(message = "password cannot be blank")
//    @Max(value = 255, message = "password must be between 8 and 255")
    private String password;

    /**
     * A String containing the firstName
     */
    @Column(name = "first_name")
    private String firstName;

    /**
     * A String containing the lastName
     */
    @Column(name = "last_name")
    private String lastName;

    /**
     * A Double containing the balance
     * this value cannot be negative
     */
    @Min(value = 0, message = "User's balance cannot be negative")
    private Double balance;

    /**
     * An Integer containing the accountBank
     */
    @Column(name = "account_bank")
    private Integer accountBank;

    /**
     * A list of friends that containing friends of a user,
     * this attribute determines the relationship many to many with the table fiend
     * and permit join the table friend to the table user in the user email
     */
    @ManyToMany(
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER
    )
    @JoinTable(
            name = "friend",
            joinColumns = @JoinColumn(name = "user_email", referencedColumnName = "email"),
            inverseJoinColumns = @JoinColumn(name = "friend_email", referencedColumnName = "email")
    )
    private List<User> friends = new ArrayList<>();

    /**
     * A list of users that containing users,
     * this attribute determines the relationship many to many with the table friend
     * and permit join the table friend to the table user in the friend email
     */
    @ManyToMany(mappedBy = "friends")
    private List<User> users = new ArrayList<>();

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", balance=" + balance +
                ", accountBank=" + accountBank +
                '}';
    }
}
