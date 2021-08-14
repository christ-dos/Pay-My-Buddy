package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.DTO.DisplayingTransfer;
import com.openclassrooms.paymybuddy.model.Transfer;

import java.util.List;

public interface ITransferService {

    List<DisplayingTransfer> getCurrentUserTransfers();

    Transfer addTransfer(DisplayingTransfer displayingTransfer);
}
