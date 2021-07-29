package com.openclassrooms.paymybuddy.DTO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ReceivingDataTransactionView {

    private String UserEmail;

    private String friendEmail;

    private Double amount;
}
