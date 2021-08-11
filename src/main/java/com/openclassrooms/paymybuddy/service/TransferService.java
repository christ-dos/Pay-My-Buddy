package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.exception.BalanceInsufficientException;
import com.openclassrooms.paymybuddy.model.Transfer;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.repository.ITransferRepository;
import com.openclassrooms.paymybuddy.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
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
    public Transfer addTransfer(Transfer transfer) {
        User currentUser = userRepository.findByEmail(transfer.getUserEmail());
        //update of balance when transfer type is debit
        if (transfer.getType().equalsIgnoreCase("debit")) {
            if (currentUser.getBalance() > transfer.getAmount()) {
                currentUser.setBalance(currentUser.getBalance() - transfer.getAmount());
            } else {
                throw new BalanceInsufficientException("Service: transfer not possible, because the balance is insufficient");
            }
        } else {
            //update of balance when transfer type is credit
            currentUser.setBalance(currentUser.getBalance() + transfer.getAmount());
        }
        transfer.setUser(currentUser);
        return transferRepository.save(transfer);
    }
}
