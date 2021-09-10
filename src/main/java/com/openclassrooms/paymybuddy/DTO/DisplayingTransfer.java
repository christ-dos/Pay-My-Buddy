package com.openclassrooms.paymybuddy.DTO;

import com.openclassrooms.paymybuddy.model.TransferTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Class DTO {@link DisplayingTransfer }that displayed data of transfer in View transfer
 *
 * @author Christine Duarte
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class DisplayingTransfer {

    /**
     * Attribute that containing the description of the transfer
     */
    private String description;

    /**
     * Attribute enum that contain type of transfer (CREDIT, DEBIT)
     */
    @NotNull(message = "Type cannot be empty")
    private TransferTypeEnum transferType;

    /**
     * Attribute that containing the amount of the transfer
     */
    @Min(value = 1, message = "Amount cannot be less than 1")
    @NotNull(message = "Amount cannot be null")
    private Double amount;

    /**
     * Attribute that contain the value of balance after transfer operation
     */
    private Double postTradeBalance;

    /**
     * Method toString
     *
     * @return a String of the object DisplayingTransfer
     */
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
