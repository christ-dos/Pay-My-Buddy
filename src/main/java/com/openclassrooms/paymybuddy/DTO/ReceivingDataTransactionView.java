package com.openclassrooms.paymybuddy.DTO;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Setter
@Getter
public class ReceivingDataTransactionView {

    private String UserEmail;

    @NonNull
    private String friendEmail;

    @NonNull
    private Double amount;

    private String description;
}
