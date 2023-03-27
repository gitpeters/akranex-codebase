package com.akraness.akranesswaitlist.barter.repository;

import com.akraness.akranesswaitlist.barter.model.Offer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OfferRepository extends JpaRepository<Offer, Long> {
    List<Offer> findByAkranexTag(String akranexTag);
}
