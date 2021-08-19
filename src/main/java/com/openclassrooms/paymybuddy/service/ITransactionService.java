package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.DTO.DisplayingTransaction;
import com.openclassrooms.paymybuddy.DTO.SendTransaction;
import com.openclassrooms.paymybuddy.model.Transaction;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;

public interface ITransactionService {
    public Iterable<Transaction> getTransactions();


    List<DisplayingTransaction> getCurrentUserTransactionsByEmail();

    @Transactional
    Transaction addTransaction(SendTransaction sendTransaction);
}
