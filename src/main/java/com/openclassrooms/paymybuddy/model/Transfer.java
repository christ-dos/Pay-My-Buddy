package com.openclassrooms.paymybuddy.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Transfer {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transfer_id")
    private Integer transferId;

    private LocalDateTime date;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "transfer_type")
    private TransferTypeEnum transferType;

    private Double amount;

    private String description;

    @Column(name = "post_trade_balance")
    private Double postTradeBalance;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_email")
    private User user;

    @Override
    public String toString() {
        return "Transfer{" +
                "transferId=" + transferId +
                ", date=" + date +
                ", transferType=" + transferType +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                ", postTradeBalance=" + postTradeBalance +
                ", user=" + user +
                '}';
    }
}
