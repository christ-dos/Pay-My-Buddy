package com.openclassrooms.paymybuddy.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * Class that configure the key composite of the entity Friend
 *
 * @author Christine Duarte
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FriendId implements Serializable {
    /**
     *Attribute containing a String with the userEmail
     */
    private String userEmail;
    /**
     *Attribute containing a String with the friendEmail
     */
    private String friendEmail;
}
