package com.openclassrooms.paymybuddy.controller;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

@WebMvcTest(TransfereController.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
public class TransfereControllerTest {
}
