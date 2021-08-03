package com.openclassrooms.paymybuddy.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
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

    private Double amount;

    private String description;

    private Double fees;

    private Date date;

    @Column(name = "emitter_email")
    private String emitterEmail;

    @Column(name = "receiver_email")
    private String receiverEmail;

//    @ManyToOne
//    @JoinColumn(name = "transactionsEmitter", nullable = false)
//    private User user;
}
