package com.openclassrooms.paymybuddy.repository;

import com.openclassrooms.paymybuddy.DTO.IDisplayingTransaction;
import com.openclassrooms.paymybuddy.model.Transaction;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

/**
 * Interface that handles database queries for transactions
 *
 * @author Christine Duarte
 */
@Repository
public interface ITransactionRepository extends CrudRepository<Transaction, Integer> {
    /**
     * Query that insert transactions in table transaction
     *
     * @param userEmail   A String containing the user's email, the emitter of the transaction
     * @param friendEmail A String containing the friend's email, the receiver of the transaction
     * @param amount      A double containing the value of the rtansaction
     * @param description A String describing the reason of transaction
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "INSERT INTO transaction(date, amount,description, emitter_email, receiver_email) " +
            "VALUES (NOW(),:amount, :description, :userEmitterEmail, :userReceiverEmail)", nativeQuery = true)
    public void saveTransaction(@Param("userEmitterEmail") String userEmail, @Param("userReceiverEmail") String friendEmail,
                                @Param("amount") Double amount, @Param("description") String description);

    /**
     * Query that permit find transactions linked at a user's email
     *
     * @param userEmail A String containing the user's email
     * @return A Set of {@link IDisplayingTransaction}
     */
    @Query(value = " SELECT user.email AS userEmail, user.first_name AS firstName," +
            " transaction.description AS description, transaction.amount AS amount" +
            "  FROM user" +
            " JOIN transaction ON user.email= transaction.receiver_email" +
            " where transaction.emitter_email =?1 ORDER BY transaction.date DESC", nativeQuery = true)
    public Set<IDisplayingTransaction> findTransactionsByEmail(String userEmail);

}
