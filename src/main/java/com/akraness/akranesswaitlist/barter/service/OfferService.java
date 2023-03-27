package com.akraness.akranesswaitlist.barter.service;

import com.akraness.akranesswaitlist.barter.dto.OfferRequest;
import com.akraness.akranesswaitlist.barter.dto.OfferResponse;
import com.akraness.akranesswaitlist.barter.model.Offer;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface OfferService {
    void createOffer(OfferRequest request);
    List<OfferResponse> getAllOffers();
    List<OfferResponse> getAllOffersByUser(String akranexTag);
    OfferResponse getOffer(Long offerId);
}
