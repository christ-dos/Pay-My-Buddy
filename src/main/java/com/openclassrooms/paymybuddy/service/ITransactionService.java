package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.DTO.DisplayingTransaction;
import com.openclassrooms.paymybuddy.DTO.SendTransaction;
import com.openclassrooms.paymybuddy.model.Transaction;

import javax.transaction.Transactional;
import java.util.List;

public interface ITransactionService {
    public Iterable<Transaction> getTransactions();

//    public List<DisplayingTransaction> getTransactionsByEmail(String userEmail);

//    public void addTransaction(String userEmail, String friendEmail, Double amount, String description);


    List<DisplayingTransaction> getCurrentUserTransactionsByEmail();

    @Transactional
    Transaction addTransaction(SendTransaction sendTransaction);
}
