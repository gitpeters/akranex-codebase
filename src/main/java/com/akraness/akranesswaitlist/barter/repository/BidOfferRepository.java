package com.akraness.akranesswaitlist.barter.repository;

import com.akraness.akranesswaitlist.barter.model.BidOffer;
import com.akraness.akranesswaitlist.barter.model.Offer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BidOfferRepository extends JpaRepository<BidOffer, Long> {
    List<BidOffer> findByOfferId(Long offerId);
}
