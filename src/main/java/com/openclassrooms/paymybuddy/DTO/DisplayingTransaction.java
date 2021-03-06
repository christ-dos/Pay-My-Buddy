package com.openclassrooms.paymybuddy.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Class DTO {@link DisplayingTransaction} that displayed data of transactions in view transaction
 *
 * @author Christine Duarte
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class DisplayingTransaction {
    /**
     * Attribute that containing the first name of the receiver of the transaction
     */
    private String firstName;

    /**
     * Attribute that containing the description of the transaction
     */
    private String description;

    /**
     * Attribute that containing the amount of the transaction
     */
    private Double amount;

    /**
     * Method toString
     *
     * @return a String of the object DisplayingTransaction
     */
    @Override
    public String toString() {
        return "DisplayingTransaction{" +
                "firstName='" + firstName + '\'' +
                ", description='" + description + '\'' +
                ", amount=" + amount +
                '}';
    }
}
