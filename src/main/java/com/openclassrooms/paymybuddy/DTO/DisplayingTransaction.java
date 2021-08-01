package com.openclassrooms.paymybuddy.DTO;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Class DTO that implements {@link IDisplayingTransaction} that permit display data of transactions in view
 *
 * @author Christine Duarte
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class DisplayingTransaction implements IDisplayingTransaction {
    /**
     * Attribute that containing the first name of the receiver of the transaction
     */
    private String firstName;

    /**
     * Attribute that containing the description of the transaction
     */
    private String description;

    /**
     * Attribute that containing the amout of the transaction
     */
    private Double amount;

    /**
     * Method that get attribute firstName of receiver of transaction
     *
     * @return The firstName
     */
    @Override
    public String getFirstName() {
        return firstName;
    }

    /**
     * Method that get attribute description of transaction
     *
     * @return The description
     */
    @Override
    public String getDescription() {
        return description;
    }

    /**
     * Method that get attribute amount of transaction
     *
     * @return The amount
     */
    @Override
    public Double getAmount() {
        return amount;
    }
}
