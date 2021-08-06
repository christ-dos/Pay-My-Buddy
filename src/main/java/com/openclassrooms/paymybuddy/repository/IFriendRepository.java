package com.openclassrooms.paymybuddy.repository;

import com.openclassrooms.paymybuddy.model.Friend;
import com.openclassrooms.paymybuddy.model.FriendId;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Interface that handles database queries for friend's table
 *
 * @author Christine Duarte
 */
@Repository
public interface IFriendRepository extends CrudRepository<Friend, FriendId> {
    /**
     * Query that permit find the list of friend for a user email
     * the result is ordered in descending order by date of addition
     *
     * @param userEmail A String containing the email of the user
     * @return A list of {@link Friend} object
     */
    List<Friend> findByUserEmailOrderByDateAddedDesc(String userEmail);
}
