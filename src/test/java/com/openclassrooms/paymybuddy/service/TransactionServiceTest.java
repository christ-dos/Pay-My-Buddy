package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.DTO.DisplayingTransaction;
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
import java.util.stream.Collectors;

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
    void getTransactionsByEmailTest_whenTransactionWithEmailEmitterIsKikineOrReceiverEmailIsKikine_thenReturnListDisplayingTransactionForEmailkikineWithSignNegatifIfEmitterEmailIsKikine() {
        //GIVEN
        String emitterEmail = "kikine@email.fr";
        String receiverEmail = "kikine@email.fr";

        List<Transaction> transactions = new ArrayList<>();
        Transaction transaction1 = Transaction.builder()
                .transactionId(1).amount(25.0).description("diner paula")
                .emitterEmail("kikine@email.fr").receiverEmail("lisa@email.fr")
                .date(LocalDateTime.now())
                .build();
        Transaction transaction2 = Transaction.builder()
                .transactionId(2).amount(15.0).description("shopping  casa china")
                .emitterEmail("kikine@email.fr").receiverEmail("lisa@email.fr").
                date(LocalDateTime.now())
                .build();
        Transaction transaction3 = Transaction.builder()
                .transactionId(3).amount(18.0).description("movies tickets")
                .emitterEmail("lisa@email.fr").receiverEmail("kikine@email.fr")
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

//        User userMock = mock(User.class);
        when(transactionRepositoryMock.findTransactionsByEmitterEmailOrReceiverEmailOrderByDateDesc(emitterEmail, receiverEmail)).thenReturn(transactions);
        transactions.stream()
                .map(transaction -> {
                    if (transaction.getEmitterEmail().equals(emitterEmail)) {
                        return new DisplayingTransaction(userLise.getFirstName(), transaction.getDescription(), -transaction.getAmount());
                    } else {
                        when(userRepositoryMock.findByEmail(isA(String.class))).thenReturn(userLise);
                        return new DisplayingTransaction(userLise.getFirstName(), transaction.getDescription(), transaction.getAmount());
                    }
                }).collect(Collectors.toList());
        //WHEN
        List<DisplayingTransaction> listTransactionsResult = transactionServiceTest.getTransactionsByEmail(emitterEmail, receiverEmail);
        //THEN
        assertEquals(3, listTransactionsResult.size());
        assertEquals("Lisette", listTransactionsResult.get(0).getFirstName());
        assertEquals(-25, listTransactionsResult.get(0).getAmount());
        assertEquals("Lisette",listTransactionsResult.get(2).getFirstName());
        assertEquals(18, listTransactionsResult.get(2).getAmount());
        verify(userRepositoryMock, times(4)).findByEmail(isA(String.class));
    }

    @Test
    void getTransactionByEmailTest_whenEmitterEmailTransactionNotExist_thenReturnNull() {
        //GIVEN
        String emitterEmail = "barbapapa@email.fr";
        String receiverEmail = "teletubiz@email.fr";
        List<DisplayingTransaction> emptyList = new ArrayList<>();
        //WHEN
        List<DisplayingTransaction> transactionsResult = transactionServiceTest.getTransactionsByEmail(emitterEmail, receiverEmail);
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
                .balance(30.50)
                .accountBank(170974).build();

        Transaction transactionTest = Transaction.builder()
                .receiverEmail("lise@email.fr").emitterEmail("kikine@email.fr").
                amount(10.0)
                .date(LocalDateTime.now()).build();

        when(userRepositoryMock.findByEmail(isA(String.class))).thenReturn(userEmitter);
        when(transactionRepositoryMock.save(isA(Transaction.class))).thenReturn(transactionTest);
        //WHEN
        Transaction transactionResult = transactionServiceTest.addTransaction(transactionTest);
        //THEN
        verify(transactionRepositoryMock, times(1))
                .save(isA(Transaction.class));
        assertEquals("kikine@email.fr", transactionResult.getEmitterEmail());
        assertEquals("lise@email.fr", transactionResult.getReceiverEmail());
        assertEquals(10.0, transactionResult.getAmount());
    }

    @Test
    public void addTransaction_whenUserBalanceNotEnough_thenThrowBalanceInsufficientException() {
        //GIVEN
        String userEmail = "kikine@email.fr";
        User emitter = User.builder()
                .email("kikine@email.fr")
                .password("monTropToppassword")
                .firstName("Christine")
                .lastName("Deldalle")
                .balance(30.0)
                .accountBank(170974).build();

        Transaction transaction = Transaction.builder()
                .transactionId(2).amount(50.0).description("diner paula").date(LocalDateTime.now())
                .emitterEmail("kikine@email.fr")
                .build();
        when(userRepositoryMock.findByEmail(userEmail)).thenReturn(emitter);
        //WHEN
        //THEN
        assertThrows(BalanceInsufficientException.class, () -> transactionServiceTest.addTransaction(transaction));
        verify(userRepositoryMock, times(1)).findByEmail(isA(String.class));
    }
}