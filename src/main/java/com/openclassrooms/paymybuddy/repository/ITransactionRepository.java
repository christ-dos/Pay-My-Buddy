package com.openclassrooms.paymybuddy.repository;

import com.openclassrooms.paymybuddy.DTO.DisplayingTransaction;
import com.openclassrooms.paymybuddy.model.Transaction;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * Interface that handles database queries for transaction's table
 *
 * @author Christine Duarte
 */
@Repository
public interface ITransactionRepository extends CrudRepository<Transaction, Integer> {
    /**
     * Query that permit find transactions emitted and received by a user
     *
     * @param userEmitterEmail A String containing the email of the emitter
     * @param userReceiverEmail A String containing the email of the receiver
     * @return A list of {@link Transaction}
     */
        List<Transaction> findTransactionsByUserEmitterEmailOrUserReceiverEmailOrderByDateDesc(@NotBlank(message = "User's email cannot be blank") String userEmitterEmail, @NotBlank(message = "User's email cannot be blank") String userReceiverEmail);

}
