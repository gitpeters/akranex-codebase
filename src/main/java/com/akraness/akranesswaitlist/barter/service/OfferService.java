package com.akraness.akranesswaitlist.barter.service;

import com.akraness.akranesswaitlist.barter.dto.BidRequest;
import com.akraness.akranesswaitlist.barter.dto.BidResponse;
import com.akraness.akranesswaitlist.barter.dto.OfferRequest;
import com.akraness.akranesswaitlist.barter.dto.OfferResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface OfferService {
    ResponseEntity<?> createOffer(OfferRequest request);
    List<OfferResponse> getAllOffers();
    List<OfferResponse> getAllOffersByUser(String akranexTag);
    OfferResponse getOffer(Long offerId);

    ResponseEntity<?> bidOffer(Long offerId, BidRequest request);
    ResponseEntity<?> getBids(Long offerId);
}
