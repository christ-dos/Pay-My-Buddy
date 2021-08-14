package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.DTO.DisplayingTransaction;
import com.openclassrooms.paymybuddy.SecurityUtilities;
import com.openclassrooms.paymybuddy.exception.BalanceInsufficientException;
import com.openclassrooms.paymybuddy.model.Transaction;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.repository.ITransactionRepository;
import com.openclassrooms.paymybuddy.repository.IUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
     *
     * @param transactionRepository Instance of {@link ITransactionRepository}
     * @param userRepository        Instance of {@link IUserRepository}
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
     * @return A list of {@link DisplayingTransaction}, a data transfer object
     */
    @Override
    public List<DisplayingTransaction> getCurrentUserTransactionsByEmail() {
        List<Transaction> transactions = transactionRepository.findTransactionsByEmitterEmailOrReceiverEmailOrderByDateDesc(SecurityUtilities.userEmail, SecurityUtilities.userEmail);

        List<DisplayingTransaction> displayingListUser = transactions.stream()
                .map(transaction -> {
                    if (transaction.getEmitterEmail().equals(SecurityUtilities.userEmail)) {
                        User userReceiver = userRepository.findByEmail(transaction.getReceiverEmail());
                        return new DisplayingTransaction(userReceiver.getFirstName(), transaction.getDescription(), -transaction.getAmount());
                    } else {
                        User userEmitter = userRepository.findByEmail(transaction.getEmitterEmail());
                        return new DisplayingTransaction(userEmitter.getFirstName(), transaction.getDescription(), transaction.getAmount());
                    }
                })
                .collect(Collectors.toList());
        log.debug("Service: displaying list of transaction for userEmail: " + SecurityUtilities.userEmail);
        return displayingListUser;
    }

    /**
     * Method which add a transaction in database
     *
     * @param transaction An object {@link Transaction} to save
     * @return A {@link Transaction} object
     */
    @Transactional
    @Override
    public Transaction addTransaction(Transaction transaction) {
        User  userEmitterTransaction = userRepository.findByEmail(transaction.getEmitterEmail());
        if ((transaction.getAmount() + calculateFees(transaction.getAmount())) > userEmitterTransaction.getBalance()) {
            log.error("Service: account balance is insufficient");
            throw new BalanceInsufficientException("Insufficient account balance, your balance is: " + userEmitterTransaction.getBalance());
        }
        transaction.setFees(calculateFees(transaction.getAmount()));
        transaction.setDate(LocalDateTime.now());
        transaction.setUserEmitter(getUserEmitterNewBalance(userEmitterTransaction, transaction.getAmount()));
        transaction.setUserReceiver(getUserReceiverNewBalance(transaction.getReceiverEmail(), transaction.getAmount()));
        // user emitter save with new balance
//        userRepository.save(getUserEmitterNewBalance(userEmitterTransaction, transaction.getAmount()));
        // user receiver save with new balance
//        userRepository.save(getUserReceiverNewBalance(transaction.getReceiverEmail(), transaction.getAmount()));
        return transactionRepository.save(transaction);
    }

    /**
     * Method private which calculate the fees for the transaction
     *
     * @param amount A Double that containing value of transaction
     * @return A Double with the value of the fees for the transaction
     */
    private Double calculateFees(Double amount) {
        double feesPercentage = 0.5;

        return (amount * feesPercentage) / 100;
    }

    /**
     * Method private that get the new balance after transaction for the receiver
     *
     * @param friendEmail A String containing friend email, the receiver of transaction
     * @param amount      A Double that containing value of transaction
     * @return A User receiver with the new balance updated
     */
    private User getUserReceiverNewBalance(String friendEmail, Double amount) {
        User userReceiverTransaction = userRepository.findByEmail(friendEmail);
        Double newBalanceReceiver = (userReceiverTransaction.getBalance()) + amount;
        userReceiverTransaction.setBalance(newBalanceReceiver);

        return userReceiverTransaction;
    }

    /**
     * Method private that get the new balance after transaction for the emitter
     *
     * @param userEmitterTransaction A User Emitter of the transaction
     * @param amount                 A Double that containing value of transaction
     * @return A User emitter with the new balance updated
     */
    private User getUserEmitterNewBalance(User userEmitterTransaction, Double amount) {
        Double newBalanceEmitter = (userEmitterTransaction.getBalance()) - (amount + calculateFees(amount));
        userEmitterTransaction.setBalance(newBalanceEmitter);

        return userEmitterTransaction;
    }

}
