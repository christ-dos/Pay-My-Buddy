package com.openclassrooms.paymybuddy.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

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

    private LocalDateTime date;

    private String type;

    private Double amount;

    private String description;

    @Column(name = "user_email")
    private String userEmail;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_email", insertable = false, updatable = false)
    private User user;

}
