package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.DTO.DisplayingTransaction;
import com.openclassrooms.paymybuddy.DTO.SendTransaction;
import com.openclassrooms.paymybuddy.exception.BalanceInsufficientException;
import com.openclassrooms.paymybuddy.model.Transaction;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.repository.ITransactionRepository;
import com.openclassrooms.paymybuddy.repository.IUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

/**
 * Class that test {@link TransactionService}
 *
 * @author Christine Duarte
 */
@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {
    /**
     * An instance of {@link ITransactionService}
     */
    private ITransactionService transactionServiceTest;

    /**
     * A mock of {@link ITransactionRepository}
     */
    @Mock
    private ITransactionRepository transactionRepositoryMock;

    /**
     * A mock of {@link IUserRepository}
     */
    @Mock
    private IUserRepository userRepositoryMock;

    /**
     * An instance of {@link Pageable}
     */
    private Pageable pageable;

    /**
     * A List of {@link Transaction}
     */
    private List<Transaction> transactions;

    /**
     * A mock of {@link Authentication}
     */
    @Mock
    private Authentication authentication;

    /**
     * Method that initialise instances to perform each test
     */
    @BeforeEach
    public void setUpPerTest() {
        transactionServiceTest = new TransactionService(transactionRepositoryMock, userRepositoryMock);

        authentication = Mockito.mock(Authentication.class);
        UserDetails userDetails = Mockito.mock(UserDetails.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(authentication.getPrincipal()).thenReturn(userDetails);
        Mockito.when(userDetails.getUsername()).thenReturn("dada@email.fr");

        pageable = PageRequest.of(0, 5);
        String emitterEmail = "dada@email.fr";

        transactions = new ArrayList<>();
        Transaction transaction1 = Transaction.builder()
                .transactionId(1).amount(25.0).description("diner paula")
                .userEmitter(User.builder().email(emitterEmail).build()).userReceiver(User.builder().email("lisa@email.fr").build())
                .date(LocalDateTime.now())
                .build();
        Transaction transaction2 = Transaction.builder()
                .transactionId(2).amount(15.0).description("shopping  casa china")
                .userEmitter(User.builder().email(emitterEmail).build())
                .userReceiver(User.builder().email("lisa@email.fr").build())
                .date(LocalDateTime.now())
                .build();
        Transaction transaction3 = Transaction.builder()
                .transactionId(3).amount(18.0).description("movies tickets")
                .userEmitter(User.builder().email("lisa@email.fr").build())
                .userReceiver(User.builder().email(emitterEmail).build())
                .date(LocalDateTime.now())
                .build();
        transactions.add(transaction1);
        transactions.add(transaction2);
        transactions.add(transaction3);
    }

    /**
     * method that test getCurrentUserTransactionsByEmail
     * when emitter email is "dada@email.fr" or receiver email is "dada@email.fr"
     * we want all transactions of user "dada@email.fr emitted or received
     * then return a List with {@link Transaction}, with sign "-" if "dada@email.fr is emitter of transaction
     */
    @Test
    public void getCurrentUserTransactionsByEmailTest_whenTransactionWithEmailEmitterIsDadaOrReceiverEmailIsDada_thenReturnListDisplayingTransactionForEmailDadaWithSignNegativeIfEmitterEmailIsDada() {
        //GIVEN
        Page<Transaction> transactionsPage;
        transactionsPage = new PageImpl<>(transactions);
        User userLise = User.builder()
                .email("lisa@email.fr")
                .password("monTropToppassword")
                .firstName("Lisette")
                .lastName("Dumarche")
                .balance(30.50)
                .accountBank(170974).build();
        when(transactionRepositoryMock.findTransactionsByUserEmitterEmailOrUserReceiverEmailOrderByDateDesc(isA(String.class), isA(String.class), isA(Pageable.class))).thenReturn(transactionsPage);
        when(userRepositoryMock.findByEmail(any(String.class))).thenReturn(userLise);
        //WHEN
        Page<DisplayingTransaction> listTransactionsResult = transactionServiceTest.getCurrentUserTransactionsByEmail(pageable);
        //THEN
        assertEquals(3, listTransactionsResult.stream().count());
        assertEquals("Lisette", listTransactionsResult.getContent().get(0).getFirstName());
        assertEquals(-25, listTransactionsResult.getContent().get(0).getAmount());
        assertEquals("Lisette", listTransactionsResult.getContent().get(2).getFirstName());
        assertEquals(18, listTransactionsResult.getContent().get(2).getAmount());
        verify(userRepositoryMock, times(3)).findByEmail(isA(String.class));
    }

    /**
     * method that test getCurrentUserTransactionsByEmail
     * when emitter email of the transaction not exist
     * then return null
     */
    @Test
    public void getCurrentUserTransactionsByEmailTest_whenEmitterEmailTransactionNotExist_thenReturnNull() {
        //GIVEN
        List<Transaction> emptyList = new ArrayList<>();
        Page<Transaction> displayingTransactionsPageEmpty = new PageImpl<>(emptyList);
        when(transactionRepositoryMock.findTransactionsByUserEmitterEmailOrUserReceiverEmailOrderByDateDesc(isA(String.class), isA(String.class), isA(Pageable.class))).thenReturn(displayingTransactionsPageEmpty);
        //WHEN
        Page<DisplayingTransaction> transactionsResult = transactionServiceTest.getCurrentUserTransactionsByEmail(pageable);
        //THEN
        //if the list is empty assert true
        assertTrue(transactionsResult.isEmpty());
    }

    /**
     * method that test addTransaction
     * when user's balance is sufficient
     * then return {@link Transaction} added
     */
    @Test
    public void addTransaction_whenUserBalanceIsSufficient_thenVerifyTransactionAdded() {
        //GIVEN
        User userEmitter = User.builder()
                .email("kikine@email.fr")
                .password("monTropToppassword")
                .firstName("Christine")
                .lastName("Deldalle")
                .balance(100.50)
                .accountBank(170974).build();

        User userReceiver = User.builder()
                .email("lisa@email.fr")
                .password("monTropToppassword")
                .firstName("lisette")
                .lastName("Duhamel")
                .balance(10.0)
                .accountBank(170974).build();

        SendTransaction sendTransaction = new SendTransaction("lisa@email.fr", 100.0, "books");

        Transaction transactionTest = Transaction.builder().transactionId(1)
                .userEmitter(userEmitter)
                .userReceiver(User.builder().email("lisa@email.fr").build())
                .amount(100.0)
                .fees(0.50)
                .date(LocalDateTime.now()).build();

        when(userRepositoryMock.findByEmail(isA(String.class))).thenReturn(userEmitter, userReceiver);
        when(transactionRepositoryMock.save(isA(Transaction.class))).thenReturn(transactionTest);
        //WHEN
        Transaction transactionResult = transactionServiceTest.addTransaction(sendTransaction);
        //THEN
        assertEquals("kikine@email.fr", transactionResult.getUserEmitter().getEmail());
        assertEquals("lisa@email.fr", transactionResult.getUserReceiver().getEmail());
        assertEquals(100, transactionResult.getAmount());
        assertEquals(0.5, transactionResult.getFees());
        verify(transactionRepositoryMock, times(1)).save(isA(Transaction.class));
    }

    /**
     * method that test addTransaction
     * when user's balance not enough
     * then throw {@link BalanceInsufficientException}
     */
    @Test
    public void addTransaction_whenUserBalanceNotEnough_thenThrowBalanceInsufficientException() {
        //GIVEN
        User emitter = User.builder()
                .email("dada@email.fr")
                .password("monTropToppassword")
                .firstName("Christine")
                .lastName("Deldalle")
                .balance(200.0)
                .accountBank(170974).build();

        SendTransaction sendTransaction = new SendTransaction("lisa@email.fr", 250.0, "books");
        when(userRepositoryMock.findByEmail(isA(String.class))).thenReturn(emitter);
        //WHEN
        //THEN
        assertThrows(BalanceInsufficientException.class, () -> transactionServiceTest.addTransaction(sendTransaction));
        verify(userRepositoryMock, times(1)).findByEmail(isA(String.class));
    }
}