package com.openclassrooms.paymybuddy.DTO;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class DisplayingTransaction {

    private String userName;

    private String description;

    private Double amount;
}
