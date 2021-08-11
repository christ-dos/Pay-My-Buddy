package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.repository.ITransferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransfereService implements ITransfereService {

    private ITransferRepository transferRepository;

    @Autowired
    public TransfereService(ITransferRepository transferRepository) {
        this.transferRepository = transferRepository;
    }
}
