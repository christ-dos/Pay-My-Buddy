package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.model.Transfer;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.repository.ITransferRepository;
import com.openclassrooms.paymybuddy.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransferService implements ITransferService {

    private ITransferRepository transferRepository;

    private IUserRepository userRepository;

    @Autowired
    public TransferService(ITransferRepository transferRepository, IUserRepository userRepository) {
        this.transferRepository = transferRepository;
        this.userRepository = userRepository;
    }


    @Override
    public Transfer addTransfer(Transfer transfer) {
        User currentUser = userRepository.findByEmail(transfer.getUserEmail());
        //upadate of balanec after transfer
        currentUser.setBalance(currentUser.getBalance() + transfer.getAmount());
        transfer.setUser(currentUser);
        return transferRepository.save(transfer);
    }
}
