package com.akraness.akranesswaitlist.barter.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="bid_offer_tbl")
public class BidOffer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "offer_id")
    private Long offerId;
    @Column(name = "amount_to_be_paid")
    private double amountToBePaid;
    @Column(name = "amount_to_be_received")
    private double amountToBeReceived;
    @Column(name = "rate")
    private double rate;
    @Column(name = "offer_amount")
    private double offerAmount;
    @Column(name = "offer_rate")
    private double offerRate;
    @Column(name = "trading_currency")
    private String tradingCurrency;
    @Column(name = "receiving_currency")
    private String receivingCurrency;
    @Column(name = "akranex_tag")
    private String akranexTag;
    @Column(name = "bid_status")
    private String bidStatus;
}
