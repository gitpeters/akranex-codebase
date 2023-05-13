package com.akraness.akranesswaitlist.barter.repository;

import com.akraness.akranesswaitlist.barter.model.Offer;
import com.akraness.akranesswaitlist.chimoney.entity.SubAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OfferRepository extends JpaRepository<Offer, Long> {
    List<Offer> findByAkranexTag(String akranexTag);
    @Query("SELECT o FROM Offer o LEFT JOIN BidOffer bo ON o.id = bo.offerId WHERE o.id = :offerId OR bo.offerId = :offerId")
    Optional<Offer> findOfferWithBidOfferById(Long offerId);
}
