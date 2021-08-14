package com.openclassrooms.paymybuddy.DTO;

import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class DisplayingTransfer {

    private String description;

    @NotBlank(message = "Type cannot be blank")
    private String type;

    @Min(value = 1, message = "Amount cannot be less than 1")
    @NotNull(message = "Amount cannot be null")
    private Double amount;

    private Double postTradeBalance;

    @Override
    public String toString() {
        return "DisplayingTransfer{" +
                "description='" + description + '\'' +
                ", type='" + type + '\'' +
                ", amount=" + amount +
                '}';
    }
}
