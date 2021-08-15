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

    private String type;

    private Double amount;

    private String description;

    @Column(name = "post_trade_balance")
    private Double postTradeBalance;

    @Column(name = "user_email")/* supprimer*/
    private String userEmail;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_email", insertable = false, updatable = false)
    private User user;

    @Override
    public String toString() {
        return "Transfer{" +
                "transferId=" + transferId +
                ", date=" + date +
                ", type='" + type + '\'' +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                ", userEmail='" + userEmail + '\'' +
                ", user=" + user +
                '}';
    }

}
