package com.openclassrooms.paymybuddy.repository;

import com.openclassrooms.paymybuddy.DTO.IDisplayingTransaction;
import com.openclassrooms.paymybuddy.model.Transaction;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Repository
public interface ITransactionRepository extends CrudRepository<Transaction, Integer> {

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO transaction(date, amount,description, emitter_email, receiver_email) " +
            "VALUES (NOW(),:amount, :description, :userEmitterEmail, :userReceiverEmail)", nativeQuery = true)
    public void saveTransaction(@Param("userEmitterEmail") String userEmail, @Param("userReceiverEmail") String friendEmail, @Param("amount") Double amount, @Param("description") String description);

    @Query(value = " SELECT user.email AS userEmail, user.first_name AS firstName," +
            " transaction.description AS description, transaction.amount AS amount" +
            "  FROM user" +
            " JOIN transaction ON user.email= transaction.receiver_email" +
            " where transaction.emitter_email =?1 ORDER BY transaction.date DESC", nativeQuery = true)
    public Set<IDisplayingTransaction> findTransactionsByEmail(String userEmail);


}
