package com.openclassrooms.paymybuddy.controller;

import com.openclassrooms.paymybuddy.model.Transfer;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

@WebMvcTest(TransferController.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
public class TransferControllerTest {

    public Transfer addTransferCurrentUser(){
        return null;
    }
}
