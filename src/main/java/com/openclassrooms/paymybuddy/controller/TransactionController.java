package com.openclassrooms.paymybuddy.controller;

import com.openclassrooms.paymybuddy.DTO.SendTransaction;
import com.openclassrooms.paymybuddy.exception.BalanceInsufficientException;
import com.openclassrooms.paymybuddy.model.Transaction;
import com.openclassrooms.paymybuddy.service.ITransactionService;
import com.openclassrooms.paymybuddy.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

/**
 * Class Controller that manage transactions requests
 *
 * @author Christine Duarte
 */
@Controller
@Slf4j
public class TransactionController {

    /**
     * Dependency {@link ITransactionService } injected
     */
    @Autowired
    private ITransactionService transactionService;

    /**
     * Dependency  {@link IUserService } injected
     */
    @Autowired
    private IUserService userService;

    /**
     * Method GET to displaying the view index mapped in "/transaction"
     *
     * @param sendTransaction A model DTO {@link SendTransaction} that displaying transactions
     *                        information in view : receiver's name, reason of transaction and amount
     * @param model           Interface that defines a support for model attributes.
     * @return A String containing the name of view
     */
    @GetMapping(value =  "/transaction")
    public String getTransactionsIndexView(@ModelAttribute("sendTransaction") SendTransaction sendTransaction, Model model) {
        model.addAttribute("transactions", transactionService.getCurrentUserTransactionsByEmail());
        model.addAttribute("friendLists", userService.getFriendListByCurrentUserEmail());
        log.info("Controller: The View index displaying");

        return "transaction";
    }

    /**
     * Method POST that process data receiving by the view index in endpoint "/transaction"
     * for adding transactions in table transaction in database
     *
     * @param sendTransaction A model DTO {@link SendTransaction} that displaying transactions
     *                    information in view : receiver's name, reason of transaction and amount
     * @param result      An Interface that permit check validity of entries on fields with annotation @Valid
     * @param model       Interface that defines a support for model attributes
     * @return A String containing the name of view
     */
    @PostMapping(value = "/transaction")
    public String addTransaction(@Valid @ModelAttribute("sendTransaction") SendTransaction sendTransaction, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("transactions", transactionService.getCurrentUserTransactionsByEmail());
            model.addAttribute("friendLists", userService.getFriendListByCurrentUserEmail());
            log.error("Controller: Error in fields");
            return "transaction";
        }
        try {
            transactionService.addTransaction(sendTransaction);
        } catch (BalanceInsufficientException ex) {
            result.rejectValue("amount", "BalanceInsufficientException", ex.getMessage());
            log.error("Controller: Insufficient account balance");
        }
        model.addAttribute("transactions", transactionService.getCurrentUserTransactionsByEmail());
        model.addAttribute("friendLists", userService.getFriendListByCurrentUserEmail());
        log.info("Controller: form index submitted");

        return "transaction";
    }

}
