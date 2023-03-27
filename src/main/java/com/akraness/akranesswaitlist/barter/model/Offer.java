package com.akraness.akranesswaitlist.barter.model;

import lombok.*;

import javax.persistence.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="barter_tbl")
public class Offer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(name = "amount_to_be_paid")
    private double amountToBePaid;
    @Column(name = "amount_to_be_received")
    private double amountToBeReceived;
    @Column(name = "rate")
    private double rate;
    @Column(name = "trading_currency")
    private String tradingCurrency;
    @Column(name = "receiving_currency")
    private String receivingCurrency;
    @Column(name = "akranex_tag")
    private String akranexTag;
    @Column(name = "offer_status")
    private String offerStatus;
}
