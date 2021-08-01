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

import javax.transaction.Transactional;
import java.util.Set;

/**
 * Class of service that manage Transaction entity
 *
 * @author Christine Duarte
 */
@Service
@Slf4j
public class TransactionService implements ITransactionService {
    /**
     * Instance of {@link ITransactionRepository}
     */
    private final ITransactionRepository transactionRepository;

    /**
     * Instance of {@link IUserRepository}
     */
    private final IUserRepository userRepository;

    /**
     * Constructor
     * @param transactionRepository Instance of {@link ITransactionRepository}
     * @param userRepository Instance of {@link IUserRepository}
     */
    @Autowired
    public TransactionService(ITransactionRepository transactionRepository, IUserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    /**
     * Method which get all transactions in database
     *
     * @return An Iterable of Transaction
     */
    @Override
    public Iterable<Transaction> getTransactions() {
        return transactionRepository.findAll();
    }

    /**
     * Method which get all transactions for a user email
     *
     * @param userEmail A String Containing the email of the user
     * @return A set of {@link IDisplayingTransaction}, a data transfer object
     */
    @Override
    public Set<IDisplayingTransaction> getTransactionsByEmail(String userEmail) {
        return transactionRepository.findTransactionsByEmail(userEmail);
    }

    /**
     * Method which add a transaction in database
     *
     * @param userEmail   A String containing user email, the emitter of transaction
     * @param friendEmail A String containing friend email, the receiver of transaction
     * @param amount      A Double that containing value of transaction
     * @param description A String that describe the transaction
     */
    @Transactional
    @Override
    public void addTransaction(String userEmail, String friendEmail, Double amount, String description) {
        User userEmitterTransaction = userRepository.findByEmail(userEmail);
        if (amount > userEmitterTransaction.getBalance()) {
            log.error("Service: account balance is insufficient");
            throw new BalanceInsufficientException("Insufficient account balance, your balance is: " + userEmitterTransaction.getBalance());
        }
        transactionRepository.saveTransaction(userEmail, friendEmail, amount, description);
        // user emitter save with new balance
        userRepository.save(getBalanceEmitter(userEmitterTransaction, amount));
        // user receiver save with new balance
        userRepository.save(getBalanceReceiver(friendEmail, amount));

    }

    /**
     * Method private which calculate the fees for the transaction
     *
     * @param amount A Double that containing value of transaction
     * @return A Double with the value of the fees for the transaction
     */
    private Double getFees(Double amount) {
        Double fees = (amount * 0.5) / 100;

        return fees;
    }

    /**
     * Method private that get the new balance after transaction for the receiver
     *
     * @param friendEmail A String containing friend email, the receiver of transaction
     * @param amount A Double that containing value of transaction
     *
     * @return A User receiver with the new balance updated
     */
    private User getBalanceReceiver(String friendEmail, Double amount) {
        User userReceiverTransaction = userRepository.findByEmail(friendEmail);
        Double newBalanceReceiver = (userReceiverTransaction.getBalance()) + amount;
        userReceiverTransaction.setBalance(newBalanceReceiver);

        return userReceiverTransaction;
    }

    /**
     * Method private that get the new balance after transaction for the emitter
     *
     * @param userEmitterTransaction A User Emitter of the transaction
     * @param amount A Double that containing value of transaction
     *
     * @return A User emitter with the new balance updated
     */
    private User getBalanceEmitter(User userEmitterTransaction, Double amount) {
        Double newBalanceEmitter = (userEmitterTransaction.getBalance()) - amount;
        userEmitterTransaction.setBalance(newBalanceEmitter);

        return userEmitterTransaction;
    }

}
