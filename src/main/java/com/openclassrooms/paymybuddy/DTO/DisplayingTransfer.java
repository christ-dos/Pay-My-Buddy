package com.openclassrooms.paymybuddy.DTO;

import com.openclassrooms.paymybuddy.model.TransferTypeEnum;
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

    @NotNull(message = "Type cannot be empty")
    private TransferTypeEnum transferType;

    @Min(value = 1, message = "Amount cannot be less than 1")
    @NotNull(message = "Amount cannot be null")
    private Double amount;

    private Double postTradeBalance;

    @Override
    public String toString() {
        return "DisplayingTransfer{" +
                "description='" + description + '\'' +
                ", transferType=" + transferType +
                ", amount=" + amount +
                ", postTradeBalance=" + postTradeBalance +
                '}';
    }
}
