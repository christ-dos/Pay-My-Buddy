package com.openclassrooms.paymybuddy.DTO;

/**
 * Interface that is implemented by class {@link DisplayingTransaction}
 * @author Christine Duarte
 */
public interface IDisplayingTransaction {
    /**
     * Method that get the first name of the receiver of the transaction
     */
    String getFirstName();

    /**
     * Method that get the description of the transaction
     */
    String getDescription();

    /**
     * Method that get amount of the transaction
     */
    Double getAmount();
//
//    String getEmitterEmail();
}
