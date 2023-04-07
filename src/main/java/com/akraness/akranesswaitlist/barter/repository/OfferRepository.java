package com.akraness.akranesswaitlist.barter.repository;

import com.akraness.akranesswaitlist.barter.model.Offer;
import com.akraness.akranesswaitlist.chimoney.entity.SubAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OfferRepository extends JpaRepository<Offer, Long> {
    List<Offer> findByAkranexTag(String akranexTag);
}
