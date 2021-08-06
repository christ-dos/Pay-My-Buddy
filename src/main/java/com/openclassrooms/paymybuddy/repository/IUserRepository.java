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
     * @return A {@link User} object
     */
    User findByEmail(String Email);
}
