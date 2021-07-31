package com.openclassrooms.paymybuddy.DTO;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Setter
@Getter
public class ReceivingDataTransactionView {

    private String UserEmail;

    @NotBlank(message = "Friend email cannot be null")
    //@NotNull
    private String friendEmail;

    @NotNull(message = "Amount cannot be equals to 0")
    @Min(value = 1, message = "Amount cannot be less than 1")
    @Max(value = 1000, message = "amount cannot be greater than 1000")
    private Double amount;

    private String description;
}
