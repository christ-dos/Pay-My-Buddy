package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.DTO.DisplayingTransaction;
import com.openclassrooms.paymybuddy.DTO.SendTransaction;
import com.openclassrooms.paymybuddy.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * An Interface for TransactionService
 *
 * @author  Christine Duarte
 */
public interface ITransactionService {
    /**
     * Method which get all transactions in database
     *
     * @return An Iterable of Transaction
     */
    Iterable<Transaction> getTransactions();

    /**
     * Method which get all transactions for the current user with pagination
     *
     * @param pageable Abstract interface for pagination information.
     * @return A list of {@link DisplayingTransaction}
     */
    Page<DisplayingTransaction> getCurrentUserTransactionsByEmail(Pageable pageable);

    /**
     * Method which add a transaction in database
     *
     * @param sendTransaction An object {@link SendTransaction}
     * @return A {@link Transaction} object
     */
    Transaction addTransaction(SendTransaction sendTransaction);

}