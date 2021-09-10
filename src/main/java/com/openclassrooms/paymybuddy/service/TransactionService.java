package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.DTO.DisplayingTransaction;
import com.openclassrooms.paymybuddy.DTO.SendTransaction;
import com.openclassrooms.paymybuddy.SecurityUtilities;
import com.openclassrooms.paymybuddy.exception.BalanceInsufficientException;
import com.openclassrooms.paymybuddy.model.Transaction;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.repository.ITransactionRepository;
import com.openclassrooms.paymybuddy.repository.IUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * Class of service that manage Transaction entity
 * implements {@link ITransactionService}
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
     * Method which get all transactions for the current user with pagination
     *
     * @param pageable Abstract interface for pagination information.
     * @return A list of {@link DisplayingTransaction}
     */
    @Override
    public Page<DisplayingTransaction> getCurrentUserTransactionsByEmail(Pageable pageable) {
        Page<Transaction> transactions = transactionRepository.findTransactionsByUserEmitterEmailOrUserReceiverEmailOrderByDateDesc(
                SecurityUtilities.getCurrentUser(), SecurityUtilities.getCurrentUser(), pageable);
        int totalElements = (int) transactions.getTotalElements();
        log.debug("Service: displaying list of transaction for userEmail: " + SecurityUtilities.getCurrentUser());

        return new PageImpl<>(transactions.stream()
                .map(transaction -> {
                    if (transaction.getUserEmitter().getEmail().equals(SecurityUtilities.getCurrentUser())) {
                        User userReceiver = userRepository.findByEmail(transaction.getUserReceiver().getEmail());
                        return new DisplayingTransaction(userReceiver.getFirstName(), transaction.getDescription(), -transaction.getAmount());
                    } else {
                        User userEmitter = userRepository.findByEmail(transaction.getUserEmitter().getEmail());
                        return new DisplayingTransaction(userEmitter.getFirstName(), transaction.getDescription(), transaction.getAmount());
                    }
                }).collect(Collectors.toList()), pageable, totalElements);
    }

    /**
     * Method which add a transaction in database
     *
     * @param sendTransaction An object {@link SendTransaction}
     * @return A {@link Transaction} object
     * @throws BalanceInsufficientException when the balance is insufficient to realize the operation
     */
    @Transactional
    @Override
    public Transaction addTransaction(SendTransaction sendTransaction) {
        Double fees = calculateFees(sendTransaction.getAmount());
        User userEmitterTransaction = userRepository.findByEmail(SecurityUtilities.getCurrentUser());
        if ((sendTransaction.getAmount() + fees) > userEmitterTransaction.getBalance()) {
            log.error("Service: account balance is insufficient");
            throw new BalanceInsufficientException("Insufficient account balance, your balance is: " + userEmitterTransaction.getBalance());
        }
        Transaction transaction = new Transaction();
        transaction.setFees(fees);
        transaction.setDate(LocalDateTime.now());
        transaction.setAmount(sendTransaction.getAmount());
        transaction.setDescription(sendTransaction.getDescription());
        transaction.setUserEmitter(getUserEmitterNewBalance(userEmitterTransaction, transaction.getAmount()));
        transaction.setUserReceiver(getUserReceiverNewBalance(sendTransaction.getReceiverEmail(), transaction.getAmount()));

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
