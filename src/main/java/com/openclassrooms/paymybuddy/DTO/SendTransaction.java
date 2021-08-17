package com.openclassrooms.paymybuddy.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class SendTransaction {

    @NotBlank(message = "cannot be empty")
    private String receiverEmail;

    @NotNull(message = "Amount cannot be null")
    @Min(value = 1, message = "Amount cannot be less than 1")
    @Max(value = 1000, message = "Amount cannot be greater than 1000")
    private Double amount ;

    private String description;

    @Override
    public String toString() {
        return "SendTransaction{" +
                "receiverEmail='" + receiverEmail + '\'' +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                '}';
    }
}
