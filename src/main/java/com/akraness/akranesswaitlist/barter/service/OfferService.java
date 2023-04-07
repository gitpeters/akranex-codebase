package com.akraness.akranesswaitlist.barter.service;

import com.akraness.akranesswaitlist.barter.dto.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface OfferService {
    ResponseEntity<?> createOffer(OfferRequest request);
    List<OfferResponse> getAllOffers();
    List<OfferResponse> getAllOffersByUser(String akranexTag);
    List<MyBidResponse> getAllBidsByUser(String akranexTag);
    OfferResponse getOffer(Long offerId);

    ResponseEntity<?> bidOffer(Long offerId, BidRequest request);
    ResponseEntity<?> getBids(Long offerId);
    ResponseEntity<?> approveBid(Long bidId);
    ResponseEntity<?> declineBid(Long bidId);
    ResponseEntity<?> editOffer(Long offerId, OfferRequest offerRequest);
    ResponseEntity<?> buyOffer(Long offerId, BuyDtoWrapper buyRequest) throws JsonProcessingException;
}
