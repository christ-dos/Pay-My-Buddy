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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@Slf4j
public class TransferController {

    @Autowired
    private ITransferService transferService;

    @GetMapping(value = "/transfer")
    public String getTransfersViewTransfer(@ModelAttribute("displayingTransfer") DisplayingTransfer displayingTransfer, Model model,
                                           @RequestParam("page") Optional<Integer> page,
                                           @RequestParam("size") Optional<Integer> size) {
        transferModelsPageable(model, page, size);
        log.info("Controller: The View transfer displaying");

        return "transfer";
    }

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
            log.debug("Controller: Transfer added for userEmail: " + SecurityUtilities.userEmail);
        } catch (BalanceInsufficientException ex) {
            result.rejectValue("amount", "BalanceInsufficient", ex.getMessage());
            log.error("Controller: Balance is insufficient to proceed a transfer");
        }
        transferModelsPageable(model, page, size);

        return "transfer";
    }

    private void transferModelsPageable(Model model, @RequestParam("page") Optional<Integer> page,
                                        @RequestParam("size") Optional<Integer> size) {
        int currentPage = page.orElse(0);
        int pageSize = size.orElse(5);

        Page<DisplayingTransfer> transfers = transferService.getCurrentUserTransfers(PageRequest.of(currentPage , pageSize));

        model.addAttribute("transfers", transfers);
        model.addAttribute("totalPages", transfers.getTotalPages());
        model.addAttribute("currentPage",page);
        model.addAttribute("transferTypes", TransferTypeEnum.values());

//        int totalPages = transfers.getTotalPages();
//        if (totalPages > 0) {
//            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
//                    .boxed()
//                    .collect(Collectors.toList());
//            model.addAttribute("pageNumbers", pageNumbers);
//        }

    }

}
