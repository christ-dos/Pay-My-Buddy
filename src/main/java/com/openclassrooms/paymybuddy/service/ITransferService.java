package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.DTO.DisplayingTransfer;
import com.openclassrooms.paymybuddy.model.Transfer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * An Interface for TransferService
 *
 * @author Christine Duarte
 */
public interface ITransferService {
    /**
     * Method that find transfers of the current user with pagination
     *
     * @param pageable Abstract interface for pagination information.
     * @return A Page of {@link Transfer} for the current user
     */
    Page<DisplayingTransfer> getCurrentUserTransfers(Pageable pageable);

    /**
     * Method to add a transfer in the database
     *
     * @param displayingTransfer a model DTO to display information in view
     * @return The {@link Transfer} added
     */
    Transfer addTransfer(DisplayingTransfer displayingTransfer);
}
