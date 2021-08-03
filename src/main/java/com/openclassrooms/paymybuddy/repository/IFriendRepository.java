package com.openclassrooms.paymybuddy.repository;

import com.openclassrooms.paymybuddy.model.Friend;
import com.openclassrooms.paymybuddy.model.FriendId;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IFriendRepository extends CrudRepository<Friend, FriendId> {

List<Friend> findByUserEmailOrderByDateAddedDesc(String userEmail);
}
