package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.DTO.DisplayingTransfer;
import com.openclassrooms.paymybuddy.model.Transfer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ITransferService {

//    List<DisplayingTransfer> getCurrentUserTransfers();

    Page<DisplayingTransfer> getCurrentUserTransfers(Pageable pageable);

    Transfer addTransfer(DisplayingTransfer displayingTransfer);
}
