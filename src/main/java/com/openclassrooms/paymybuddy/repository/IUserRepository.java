package com.openclassrooms.paymybuddy.repository;

import com.openclassrooms.paymybuddy.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

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
