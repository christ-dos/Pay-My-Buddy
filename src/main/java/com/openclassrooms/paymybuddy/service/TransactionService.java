package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.DTO.IDisplayingTransaction;
import com.openclassrooms.paymybuddy.model.Transaction;
import com.openclassrooms.paymybuddy.repository.ITransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@Slf4j
public class TransactionService implements ITransactionService {

    private final ITransactionRepository transactionRepository;

    @Autowired
    public TransactionService(ITransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public Iterable<Transaction> getTransactions() {
        return transactionRepository.findAll();
    }

    @Override
    public Set<IDisplayingTransaction> getTransactionsByEmail(String userEmail) {
        return transactionRepository.findTransactionsByEmail(userEmail);
    }

    @Override
    public void addTransaction(String userEmail, String friendEmail, Double amount, String description) {
        transactionRepository.saveTransaction(userEmail, friendEmail, amount, description);
    }
}
