package com.openclassrooms.paymybuddy.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.jpa.repository.Temporal;

import javax.persistence.*;
import java.time.LocalDateTime;


@NoArgsConstructor
@Data
@Entity
@IdClass(FriendId.class)
public class Friend {

    public Friend(String userEmail, String friendEmail, LocalDateTime dateAdded) {
        this.userEmail = userEmail;
        this.friendEmail = friendEmail;
        this.dateAdded = dateAdded;
    }
    @Id
    @Column(name = "user_email")
    private String userEmail;
    @Id
    @Column(name = "friend_email")
    private String friendEmail;

    @Column(name = "date_added")
    @CreationTimestamp
    private LocalDateTime dateAdded;

//    @ManyToMany(mappedBy = "friends")
//    private List<User> users =  new ArrayList<>();

}
