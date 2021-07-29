package com.openclassrooms.paymybuddy.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;

@AllArgsConstructor
@NoArgsConstructor
@Setter
public class DisplayingTransaction implements IDisplayingTransaction {
    @Column(name = "first_name")
    private String firstName;

    private String description;

    private Double amount;

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public Double getAmount() {
        return amount;
    }
}
