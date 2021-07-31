package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.DTO.IDisplayingTransaction;
import com.openclassrooms.paymybuddy.exception.BalanceInsufficientException;
import com.openclassrooms.paymybuddy.model.Transaction;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.repository.ITransactionRepository;
import com.openclassrooms.paymybuddy.repository.IUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@Slf4j
public class TransactionService implements ITransactionService {

    private final ITransactionRepository transactionRepository;

    private final IUserRepository userRepository;

    @Autowired
    public TransactionService(ITransactionRepository transactionRepository, IUserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
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
        User userEmitterTransaction = userRepository.findByEmail(userEmail);
        if (amount > userEmitterTransaction.getBalance()) {
            log.error("Service: account balance is insufficient");
            throw new BalanceInsufficientException("Insufficient account balance, your balance is: " + userEmitterTransaction.getBalance());
        }
        transactionRepository.saveTransaction(userEmail, friendEmail, amount, description);
        //update the user's balance
        updateBalanceEmitter(userEmitterTransaction, amount);

    }

    private User updateBalanceEmitter(User userEmitterTransaction, Double amount) {
        Double newBalance = (userEmitterTransaction.getBalance()) - amount;
        userEmitterTransaction.setBalance(newBalance);
        return userRepository.save(userEmitterTransaction);
    }

}
