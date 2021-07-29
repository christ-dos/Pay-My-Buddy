package com.openclassrooms.paymybuddy.repository;

import com.openclassrooms.paymybuddy.DTO.IFriendList;
import com.openclassrooms.paymybuddy.model.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.Set;

@Repository
public interface IUserRepository extends CrudRepository<User, Integer> {

    User findByEmail(String Email);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO friend (user_email, friend_email, date_added) VALUES (:userEmail, :friendEmail, NOW())", nativeQuery = true)
    void saveFriend(@Param("userEmail") String userEmail, @Param("friendEmail") String friendEmail);

    //@Transactional
    @Query(value = "SELECT user.email AS email, user.first_name AS firstName, user.last_name  AS lastName, " +
            "friend.date_added AS dateAdded FROM  user INNER JOIN friend ON " +
            "friend.friend_email = user.email WHERE friend.user_email=?1 ORDER BY friend.date_added DESC ", nativeQuery = true)
    Set<IFriendList> findFriendListByEmail(String userEmail);

    @Query(value = "SELECT * FROM user WHERE email=?", nativeQuery = true)
    User getUserByEmail(String userEmail);

}
