package com.openclassrooms.paymybuddy.controller;

import com.openclassrooms.paymybuddy.DTO.FriendList;
import com.openclassrooms.paymybuddy.model.Transfer;
import com.openclassrooms.paymybuddy.service.ITransferService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
@Slf4j
public class TransferController {

    @Autowired
    private ITransferService transferService;

    @PostMapping(value = "/transfer")
    public String addTransferCurrentUser(@Valid Transfer transfer, BindingResult result, Model model) {
        return null;
    }

}
