package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.DTO.DisplayingTransfer;
import com.openclassrooms.paymybuddy.SecurityUtilities;
import com.openclassrooms.paymybuddy.exception.BalanceInsufficientException;
import com.openclassrooms.paymybuddy.model.Transfer;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.repository.ITransferRepository;
import com.openclassrooms.paymybuddy.repository.IUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TransferService implements ITransferService {

    private ITransferRepository transferRepository;

    private IUserRepository userRepository;

    @Autowired
    public TransferService(ITransferRepository transferRepository, IUserRepository userRepository) {
        this.transferRepository = transferRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    @Override
    public Transfer addTransfer(DisplayingTransfer displayingTransfer) {
        User currentUser = userRepository.findByEmail(SecurityUtilities.userEmail);

        //update of balance when transfer type is debit
        if (displayingTransfer.getType().equalsIgnoreCase("debit")) {
            if (currentUser.getBalance() > displayingTransfer.getAmount()) {
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
        transfer.setType(displayingTransfer.getType());
        transfer.setPostTradeBalance(currentUser.getBalance());
        transfer.setUserEmail(currentUser.getEmail());
        transfer.setDescription(displayingTransfer.getDescription());
        transfer.setDate(LocalDateTime.now());
        transfer.setAmount(displayingTransfer.getAmount());
        transfer.setUser(currentUser);
        log.info("Service: Transfer is saved with success for email: " + currentUser.getEmail());
        return transferRepository.save(transfer);
    }

    public List<DisplayingTransfer> getCurrentUserTransfers() {
        List<Transfer> transfers = transferRepository.findTransfersByUserEmailOrderByDateDesc(SecurityUtilities.userEmail);

        List<DisplayingTransfer> displayingListTransfer = transfers.stream()
                .map(transfer -> {
                    if (transfer.getType().equalsIgnoreCase("debit")) {
                        return new DisplayingTransfer(transfer.getDescription(), transfer.getType(), -transfer.getAmount(), transfer.getPostTradeBalance());
                    } else {
                        return new DisplayingTransfer(transfer.getDescription(), transfer.getType(), +transfer.getAmount(), transfer.getPostTradeBalance());
                    }

                })
                .collect(Collectors.toList());
        log.debug("Service: displaying list of transfer for userEmail: " + SecurityUtilities.userEmail);
        return displayingListTransfer;
    }
}
