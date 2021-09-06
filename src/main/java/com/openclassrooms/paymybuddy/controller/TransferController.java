package com.openclassrooms.paymybuddy.controller;

import com.openclassrooms.paymybuddy.DTO.DisplayingTransfer;
import com.openclassrooms.paymybuddy.SecurityUtilities;
import com.openclassrooms.paymybuddy.exception.BalanceInsufficientException;
import com.openclassrooms.paymybuddy.model.TransferTypeEnum;
import com.openclassrooms.paymybuddy.service.ITransferService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.Optional;

/**
 * Class Controller that manage transfer requests
 *
 * @author Christine Duarte
 */
@Controller
@Slf4j
public class TransferController {

    /**
     * Dependency {@link ITransferService } injected
     */
    @Autowired
    private ITransferService transferService;

    /**
     * Method POST that process data receiving by the view transfer in endpoint "/transfer"
     * for adding transfer in table transfer in database
     *
     * @param displayingTransfer A model DTO {@link DisplayingTransfer} that displaying transfer
     *                           information in view : reason of transaction,  and the post trade balance after the operation
     * @param result             An Interface that permit check validity of entries on fields with annotation @Valid
     * @param model              Interface that defines a support for model attributes
     * @param page               An Optional with the page
     * @param size               An optional with the number of items in page
     * @return A String containing the name of view
     */
    @PostMapping(value = "/transfer")
    public String addTransferCurrentUser(@Valid DisplayingTransfer displayingTransfer, BindingResult result, Model model, @RequestParam("page") Optional<Integer> page,
                                         @RequestParam("size") Optional<Integer> size) {
        if (result.hasErrors()) {
            transferModelsPageable(model, page, size);
            log.error("Controller: Error in fields");

            return "transfer";
        }
        try {
            transferService.addTransfer(displayingTransfer);
            log.debug("Controller: Transfer added for userEmail: " + SecurityUtilities.getCurrentUser());
        } catch (BalanceInsufficientException ex) {
            result.rejectValue("amount", "BalanceInsufficient", ex.getMessage());
            log.error("Controller: Balance is insufficient to proceed a transfer");
        }
        transferModelsPageable(model, page, size);

        return "transfer";
    }

    /**
     * Method GET to displaying the view transfer mapped in "/transfer"
     *
     * @param displayingTransfer A model DTO {@link DisplayingTransfer} that displaying transfer
     *                           information in view : reason of transaction,  and the post trade balance after the operation
     * @param model              Interface that defines a support for model attributes
     * @param page               An Optional with the page
     * @param size               An optional with the number of items in page
     * @return A String containing the name of view
     */
    @GetMapping(value = "/transfer")
    public String getTransfersViewTransfer(@ModelAttribute("displayingTransfer") DisplayingTransfer displayingTransfer, Model model,
                                           @RequestParam("page") Optional<Integer> page,
                                           @RequestParam("size") Optional<Integer> size) {

        transferModelsPageable(model, page, size);
        log.info("Controller: The View transfer displaying");

        return "transfer";
    }

    /**
     * Method private that get models for pagination of transfer
     *
     * @param model Interface that defines a support for model attributes.
     * @param page  An Optional with the page
     * @param size  An optional with the nomber of items in page
     */
    private void transferModelsPageable(Model model, Optional<Integer> page, Optional<Integer> size) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(2);

        Page<DisplayingTransfer> transfers = transferService.getCurrentUserTransfers(PageRequest.of(currentPage - 1, pageSize));

        model.addAttribute("transfers", transfers);
        model.addAttribute("totalPages", transfers.getTotalPages());
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("transferTypes", TransferTypeEnum.values());
        model.addAttribute("totalElements", transfers.getTotalElements());
    }
}
