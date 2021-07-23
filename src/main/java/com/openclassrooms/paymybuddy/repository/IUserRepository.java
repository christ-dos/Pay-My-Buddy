package com.openclassrooms.paymybuddy.repository;

import com.openclassrooms.paymybuddy.model.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@Repository
public interface IUserRepository extends CrudRepository<User, Integer> {

    public User findByEmail(String Email);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO friend (user_email, friend_email) VALUES (:userEmail, :friendEmail)", nativeQuery = true)
    public void saveFriend(@Param("userEmail") String userEmail, @Param("friendEmail") String friendEmail);

    @Query(value = "SELECT * FROM user WHERE email=?", nativeQuery = true)
    public User getUser(String email);

    //public Iterable<User> findUserBy

}
