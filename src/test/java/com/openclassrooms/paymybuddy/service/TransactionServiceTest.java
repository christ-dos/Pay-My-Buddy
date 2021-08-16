package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.DTO.DisplayingTransaction;
import com.openclassrooms.paymybuddy.DTO.SendTransaction;
import com.openclassrooms.paymybuddy.SecurityUtilities;
import com.openclassrooms.paymybuddy.exception.BalanceInsufficientException;
import com.openclassrooms.paymybuddy.model.Transaction;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.repository.ITransactionRepository;
import com.openclassrooms.paymybuddy.repository.IUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    ITransactionService transactionServiceTest;

    @Mock
    ITransactionRepository transactionRepositoryMock;

    @Mock
    IUserRepository userRepositoryMock;

    @BeforeEach
    void setUp() {
        transactionServiceTest = new TransactionService(transactionRepositoryMock, userRepositoryMock);
    }

    @Test
    void getTransactionsTest_thenReturnIterableContainingThreeTransactions() {
        //GIVEN
        List<Transaction> transactions = new ArrayList<>();
        Transaction transaction1 = Transaction.builder()
                .transactionId(1).amount(25.0).description("diner paula")
                .build();
        Transaction transaction2 = Transaction.builder()
                .transactionId(2).amount(15.0).description("shopping  casa china")
                .build();
        Transaction transaction3 = Transaction.builder()
                .transactionId(3).amount(18.0).description("movies tickets")
                .build();
        transactions.add(transaction1);
        transactions.add(transaction2);
        transactions.add(transaction3);
        int count = 0;
        when(transactionRepositoryMock.findAll()).thenReturn(transactions);
        //WHEN
        Iterable<Transaction> transactionsResult = transactionServiceTest.getTransactions();
        Iterator<Transaction> it = transactionsResult.iterator();
        //method that count the iterable
        while (it.hasNext()) {
            it.next();
            count++;
        }
        //THEN
        //verify that transactionsResult contain 3 elements
        assertEquals(3, count);
        assertEquals(transactions, transactionsResult);
        verify(transactionRepositoryMock, times(1)).findAll();
    }

    @Test
    void getTransactionsByEmailTest_whenTransactionWithEmailEmitterIsDadaOrReceiverEmailIsDada_thenReturnListDisplayingTransactionForEmailDadaWithSignNegativeIfEmitterEmailIsDada() {
        //GIVEN
        String emitterEmail = SecurityUtilities.userEmail;

        List<Transaction> transactions = new ArrayList<>();
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

        User userLise = User.builder()
                .email("lisa@email.fr")
                .password("monTropToppassword")
                .firstName("Lisette")
                .lastName("Dumarche")
                .balance(30.50)
                .accountBank(170974).build();

        when(transactionRepositoryMock.findTransactionsByUserEmitterEmailOrUserReceiverEmailOrderByDateDesc(isA(String.class), isA(String.class))).thenReturn(transactions);
        when(userRepositoryMock.findByEmail(isA(String.class))).thenReturn(userLise);
        //WHEN
        List<DisplayingTransaction> listTransactionsResult = transactionServiceTest.getCurrentUserTransactionsByEmail();
        //THEN
        assertEquals(3, listTransactionsResult.size());
        assertEquals("Lisette", listTransactionsResult.get(0).getFirstName());
        assertEquals(-25, listTransactionsResult.get(0).getAmount());
        assertEquals("Lisette", listTransactionsResult.get(2).getFirstName());
        assertEquals(18, listTransactionsResult.get(2).getAmount());
        verify(userRepositoryMock, times(3)).findByEmail(isA(String.class));
    }

    @Test
    void getTransactionByEmailTest_whenEmitterEmailTransactionNotExist_thenReturnNull() {
        //GIVEN
        List<DisplayingTransaction> emptyList = new ArrayList<>();
        //WHEN
        List<DisplayingTransaction> transactionsResult = transactionServiceTest.getCurrentUserTransactionsByEmail();
        //THEN
        assertEquals(emptyList, transactionsResult);
    }

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
                .amount(100.0).fees(0.50)
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

    @Test
    public void addTransaction_whenUserBalanceNotEnough_thenThrowBalanceInsufficientException() {
        //GIVEN
        User emitter = User.builder()
                .email("kikine@email.fr")
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