package com.openclassrooms.paymybuddy.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Integer transactionId;

    @NotNull(message = "Amount cannot be equals to 0")
    @Min(value = 1, message = "Amount cannot be less than 1")
    @Max(value = 1000, message = "amount cannot be greater than 1000")
    private Double amount;

    private String description;

    private Double fees;

    private LocalDateTime date;

    @Column(name = "emitter_email")
    private String emitterEmail;

    @NotBlank(message = "Friend email cannot be null")
    @Column(name = "receiver_email")
    private String receiverEmail;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "emitter_email", insertable = false, updatable = false)
    private User user;

}
