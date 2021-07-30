package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.DTO.DisplayingTransaction;
import com.openclassrooms.paymybuddy.DTO.IDisplayingTransaction;
import com.openclassrooms.paymybuddy.model.Transaction;
import com.openclassrooms.paymybuddy.repository.ITransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    ITransactionService transactionServiceTest;

    @Mock
    ITransactionRepository transactionRepositoryMock;

    @BeforeEach
    void setUp() {
        transactionServiceTest = new TransactionService(transactionRepositoryMock);
    }

    @Test
    void getTransactionsTest_thenReturnIterableContainingThreeTransactions() {
        //GIVEN
        Set<Transaction> transactions = new HashSet<>();
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
    void getTransactionByEmailTest_whenTransactionWithEmailkikineExist_thenReturnSetTransactionsForEmailkikine() {
        //GIVEN
        String userEmail = "kikine@email.fr";

        DisplayingTransaction transaction1 = new DisplayingTransaction();
        transaction1.setFirstName("Christine");
        transaction1.setAmount(35.50);
        transaction1.setDescription("diner shanna");

        DisplayingTransaction transaction2 = new DisplayingTransaction();
        transaction2.setFirstName("Christine");
        transaction2.setAmount(15.50);
        transaction2.setDescription("movies tickets shanna");

        Set<IDisplayingTransaction> listDisplayingTransactionsTest = new HashSet<>();
        listDisplayingTransactionsTest.add(transaction1);
        listDisplayingTransactionsTest.add(transaction2);

        when(transactionRepositoryMock.findTransactionsByEmail(isA(String.class))).thenReturn((listDisplayingTransactionsTest));
        //WHEN
        Set<IDisplayingTransaction> setTransactionsResult = transactionServiceTest.getTransactionsByEmail(userEmail);
        //THEN
        assertEquals(listDisplayingTransactionsTest, setTransactionsResult);
        verify(transactionRepositoryMock, times(1)).findTransactionsByEmail(isA(String.class
        ));
    }

    @Test
    public void addTransaction_whenUserEmailExistInDB_thenVerifyTransactionAdded() {
        //GIVEN
        String userEmail = "kikine@email.fr";
        String friendEmail = "lise@email.fr";
        Double amount = 25.98;
        String description = "cinema";
        doNothing().when(transactionRepositoryMock).saveTransaction(isA(String.class), isA(String.class), isA(Double.class), isA(String.class));
        //WHEN
        transactionServiceTest.addTransaction(userEmail, friendEmail, amount, description);
        //THEN
        verify(transactionRepositoryMock, times(1)).saveTransaction(isA(String.class), isA(String.class), isA(Double.class), isA(String.class));
    }

    @Test
    void getTransactionByEmailTest_whenEmitterEmailTransactionNotExist_thenReturnNull() {
        //GIVEN
        String userEmail = "barbapapa@email.fr";
        when(transactionRepositoryMock.findTransactionsByEmail(isA(String.class))).thenReturn(isNull());
        //WHEN
        Set<IDisplayingTransaction> transactionsResult = transactionServiceTest.getTransactionsByEmail(userEmail);
        //THEN
        assertNull(transactionsResult);
    }

}