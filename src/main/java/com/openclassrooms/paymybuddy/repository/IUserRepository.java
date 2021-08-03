package com.openclassrooms.paymybuddy.repository;

import com.openclassrooms.paymybuddy.model.Friend;
import com.openclassrooms.paymybuddy.model.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Interface that handles database queries for users
 *
 * @author Christine Duarte
 */
@Repository
public interface IUserRepository extends CrudRepository<User, String> {

    /**
     * Query to find a user by email
     *
     * @param Email A String containing user's email
     * @return A user object
     */
    User findByEmail(String Email);

    /**
     * Query that insert friends in table friend
     *
     * @param userEmail   A String containing the user's email
     * @param friendEmail A String containing the friend's  email
     */
//    @Modifying(clearAutomatically = true, flushAutomatically = true)
//    @Transactional
//    @Query(value = "INSERT INTO friend (user_email, friend_email, date_added) VALUES (:userEmail, :friendEmail, NOW())", nativeQuery = true)
//    void saveFriend(@Param("userEmail") String userEmail, @Param("friendEmail") String friendEmail);
//    User save(User user);

    /**
     * Query that permit find by user's email the list of his friends order by date
     *
     * @param userEmail A String containing the user's email
     * @return A {@link User}
     */
    @Query(value = "SELECT friend_email, f ",nativeQuery = true)
    List<Friend> findAllByEmailOrderBydateAdded();
}
