package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.DTO.IDisplayingTransaction;
import com.openclassrooms.paymybuddy.model.Transaction;

import java.util.Set;

public interface ITransactionService {
    public Iterable<Transaction> getTransactions();

    public Set<IDisplayingTransaction> getTransactionByEmail(String userEmail);

    public void addTransaction(String userEmail, String friendEmail, Double amount);
}
