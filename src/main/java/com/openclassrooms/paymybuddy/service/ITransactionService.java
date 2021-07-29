package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.model.Transaction;

import java.util.Optional;

public interface ITransactionService {
    public Iterable<Transaction> getTransactions();

    public Optional<Transaction> getTransactionById(Integer transactionId);
}
