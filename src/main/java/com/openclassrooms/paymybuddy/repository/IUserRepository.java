package com.openclassrooms.paymybuddy.repository;

import com.openclassrooms.paymybuddy.DTO.FriendList;
import com.openclassrooms.paymybuddy.DTO.IFriendList;
import com.openclassrooms.paymybuddy.model.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Repository
public interface IUserRepository extends CrudRepository<User, Integer> {

    User findByEmail(String Email);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO friend (user_email, friend_email) VALUES (:userEmail, :friendEmail)", nativeQuery = true)
    void saveFriend(@Param("userEmail") String userEmail, @Param("friendEmail") String friendEmail);

    @Query(value = "SELECT * FROM user WHERE email=?", nativeQuery = true)
    User getUserByEmail(String userEmail);

   // @Transactional
    @Query(value = "SELECT user.email AS email, user.first_name AS firstName, user.last_name AS lastName FROM  user INNER JOIN friend ON friend.friend_email = user.email WHERE friend.user_email=?1", nativeQuery = true)
    Set<IFriendList> findFriendListByEmail(String userEmail);


}
