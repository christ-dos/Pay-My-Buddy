package com.openclassrooms.paymybuddy.controller;

import com.openclassrooms.paymybuddy.DTO.DisplayingTransfer;
import com.openclassrooms.paymybuddy.SecurityUtilities;
import com.openclassrooms.paymybuddy.exception.BalanceInsufficientException;
import com.openclassrooms.paymybuddy.model.Transfer;
import com.openclassrooms.paymybuddy.service.ITransferService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
@Slf4j
public class TransferController {

    @Autowired
    private ITransferService transferService;

    @GetMapping(value = "/transfer")
    public String getTransfersViewTransfer(@ModelAttribute("displayingTransfer") DisplayingTransfer displayingTransfer, Model model) {
        model.addAttribute("transfers",transferService.getCurrentUserTransfers());
        log.info("Controller: The View transfer displaying");

        return "transfer";
    }

    @PostMapping(value = "/transfer")
    public String addTransferCurrentUser(@Valid DisplayingTransfer displayingTransfer, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("transfers",transferService.getCurrentUserTransfers());
            log.error("Controller: Error in fields");

            return "transfer";
        }
        if (result.getRawFieldValue("type").equals("")) {
            result.rejectValue("type", "NotBlank", "Amount cannot be null");
            log.error("Controller: Error in field type is blank");
            return "tarnsfer";
        }
        try {
            transferService.addTransfer(displayingTransfer);
            log.debug("Controller: Transfer added for userEmail: " + SecurityUtilities.userEmail);
        } catch (BalanceInsufficientException ex) {
            result.rejectValue("amount", "BalanceInsufficient", ex.getMessage());
            log.error("Controller: Balance is insufficient to proceed a transfer");
        }
        model.addAttribute("transfers",transferService.getCurrentUserTransfers());
        return "transfer";
    }

}
