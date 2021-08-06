package com.openclassrooms.paymybuddy.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.time.LocalDateTime;

/**
 * Class That models the entity Friend
 *
 * @author Christine Duarte
 */
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
@IdClass(FriendId.class)
public class Friend {
    /**
     * key composite with the userEmail
     */
    @Id
    @Column(name = "user_email")
    private String userEmail;
    /**
     * key composite with the friendEmail
     */
    @Id
    @Column(name = "friend_email")
    private String friendEmail;
    /**
     * Date when the friend user was added
     */
    @Column(name = "date_added")
    private LocalDateTime dateAdded;
}
