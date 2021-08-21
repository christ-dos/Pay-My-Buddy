package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.DTO.DisplayingTransaction;
import com.openclassrooms.paymybuddy.DTO.SendTransaction;
import com.openclassrooms.paymybuddy.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;

public interface ITransactionService {
    Iterable<Transaction> getTransactions();

    Page<DisplayingTransaction> getCurrentUserTransactionsByEmail(Pageable pageable);

    @Transactional
    Transaction addTransaction(SendTransaction sendTransaction);

}