package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.model.Transaction;
import com.openclassrooms.paymybuddy.repository.ITransactionRepository;
import com.openclassrooms.paymybuddy.repository.IUserRepository;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    TransactionService transactionServiceTest;

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
        Transaction transaction2= Transaction.builder()
                .transactionId(2).amount(15.0).description("shopping  casa china")
                .build();
        Transaction transaction3= Transaction.builder()
                .transactionId(3).amount(18.0).description("movie location")
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
        assertEquals(transactions,transactionsResult);
        verify(transactionRepositoryMock,times(1)).findAll();
    }

    @Test
    void getTransactionByIdTest_whenTransactionWithIdOneExist_thenReturnTransactionWithIdOne() {
        //GIVEN
        Transaction transaction = new Transaction();
        transaction.setTransactionId(1);
        transaction.setAmount(20.0);
        transaction.setDescription("diner shanna");
        transaction.setFees(0.0);
        when(transactionRepositoryMock.findById(isA(Integer.class))).thenReturn(Optional.of(transaction));
        //WHEN
        Optional<Transaction> transactionResult = transactionServiceTest.getTransactionById(transaction.getTransactionId());
        //THEN
        assertEquals(true,transactionResult.isPresent());
        assertEquals(1,transactionResult.get().getTransactionId());
        assertEquals("diner shanna",transactionResult.get().getDescription());
        assertEquals(20.0,transactionResult.get().getAmount());
        verify(transactionRepositoryMock,times(1)).findById(transaction.getTransactionId());


    }
}