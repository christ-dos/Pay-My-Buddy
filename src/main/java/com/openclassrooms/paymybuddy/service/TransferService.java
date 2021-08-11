package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.repository.ITransferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransferService implements ITransferService {

    private ITransferRepository transferRepository;

    @Autowired
    public TransferService(ITransferRepository transferRepository) {
        this.transferRepository = transferRepository;
    }
}
