package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.DTO.DisplayingTransfer;
import com.openclassrooms.paymybuddy.SecurityUtilities;
import com.openclassrooms.paymybuddy.exception.BalanceInsufficientException;
import com.openclassrooms.paymybuddy.model.Transfer;
import com.openclassrooms.paymybuddy.model.TransferTypeEnum;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.repository.ITransferRepository;
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
 * Class of service that manage {@link Transfer} entity
 * and implements ITransferService
 *
 * @author Christine Duarte
 */
@Service
@Slf4j
public class TransferService implements ITransferService {
    /**
     * An instance of {@link ITransferRepository}
     */
    private final ITransferRepository transferRepository;

    /**
     * An instance of {@link IUserRepository}
     */
    private final IUserRepository userRepository;

    /**
     * Constructor
     *
     * @param transferRepository {@link ITransferRepository}
     * @param userRepository     {@link IUserRepository}
     */
    @Autowired
    public TransferService(ITransferRepository transferRepository, IUserRepository userRepository) {
        this.transferRepository = transferRepository;
        this.userRepository = userRepository;
    }

    /**
     * Method to add a transfer in the database
     *
     * @param displayingTransfer a model DTO to display information in view
     * @return The {@link Transfer} added
     * @throws BalanceInsufficientException when the balance is insufficient
     */
    @Transactional
    @Override
    public Transfer addTransfer(DisplayingTransfer displayingTransfer) {
        User currentUser = userRepository.findByEmail(SecurityUtilities.getCurrentUser());

        //update of balance when transfer type is debit
        if (displayingTransfer.getTransferType() == TransferTypeEnum.DEBIT) {
            if (currentUser.getBalance() >= displayingTransfer.getAmount()) {
                currentUser.setBalance((currentUser.getBalance()) - (displayingTransfer.getAmount()));
            } else {
                log.error("Service: Balance insufficient for execute the transfer");
                throw new BalanceInsufficientException("Balance insufficient for execute the transfer");
            }
        } else {
            //update of balance when transfer type is credit
            currentUser.setBalance(currentUser.getBalance() + displayingTransfer.getAmount());
        }

        Transfer transfer = new Transfer();
        transfer.setTransferType(displayingTransfer.getTransferType());
        transfer.setPostTradeBalance(currentUser.getBalance());
        transfer.setDescription(displayingTransfer.getDescription());
        transfer.setDate(LocalDateTime.now());
        transfer.setAmount(displayingTransfer.getAmount());
        transfer.setUser(currentUser);
        log.info("Service: Transfer is saved with success for email: " + currentUser.getEmail());

        return transferRepository.save(transfer);
    }

    /**
     * Method that find transfers of the current user with pagination
     *
     * @param pageable Abstract interface for pagination information.
     * @return A Page of {@link Transfer} for the current user
     */
    public Page<DisplayingTransfer> getCurrentUserTransfers(Pageable pageable) {
        Page<Transfer> transfers = transferRepository.findTransfersByUserEmailOrderByDateDesc(SecurityUtilities.getCurrentUser(), pageable);
        int totalElements = (int) transfers.getTotalElements();
        log.debug("Service: displaying list of transfer for userEmail: " + SecurityUtilities.getCurrentUser());

        return new PageImpl<>(transfers.stream()
                .map(transfer -> {
                    if (transfer.getTransferType() == TransferTypeEnum.DEBIT) {
                        return new DisplayingTransfer(transfer.getDescription(), transfer.getTransferType(), -transfer.getAmount(), transfer.getPostTradeBalance());
                    } else {
                        return new DisplayingTransfer(transfer.getDescription(), transfer.getTransferType(), +transfer.getAmount(), transfer.getPostTradeBalance());
                    }
                }).collect(Collectors.toList()), pageable, totalElements);
    }
}
