package com.openclassrooms.paymybuddy.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Class DTO {@link SendTransaction} that obtains input from the view transaction to adding a Transaction in table Transaction
 *
 * @author Christine Duarte
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class SendTransaction {

    /**
     * A String that contain the email of the user receiver of transaction
     */
    @NotBlank(message = "cannot be empty")
    private String receiverEmail;

    /**
     * Attribute that containing the amount of the transaction
     */
    @NotNull(message = "Amount cannot be null")
    @Min(value = 1, message = "Amount cannot be less than 1")
    @Max(value = 1000, message = "Amount cannot be greater than 1000")
    private Double amount;

    /**
     * Attribute that containing the description of the transaction
     */
    private String description;

    /**
     * Method ToString
     *
     * @return A string of the object SendTransaction
     */
    @Override
    public String toString() {
        return "SendTransaction{" +
                "receiverEmail='" + receiverEmail + '\'' +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                '}';
    }
}
