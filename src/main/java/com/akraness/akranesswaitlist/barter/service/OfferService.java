package com.akraness.akranesswaitlist.barter.service;

import com.akraness.akranesswaitlist.barter.dto.*;
import com.akraness.akranesswaitlist.chimoney.dto.BalanceDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.firebase.messaging.FirebaseMessagingException;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface OfferService {
    ResponseEntity<?> createOffer(OfferRequest request) throws JsonProcessingException, ExecutionException, InterruptedException, FirebaseMessagingException;
    List<OfferResponse> getAllOffers();
    List<OfferResponse> getAllOffersByUser(String akranexTag);
    List<MyBidResponse> getAllBidsByUser(String akranexTag);
    OfferResponse getOffer(Long offerId);

    ResponseEntity<?> bidOffer(Long offerId, BidRequest request) throws ExecutionException, InterruptedException, FirebaseMessagingException;
    ResponseEntity<?> getBids(Long offerId);
    ResponseEntity<?> approveBid(Long bidId) throws ExecutionException, InterruptedException, FirebaseMessagingException;
    ResponseEntity<?> declineBid(Long bidId) throws ExecutionException, InterruptedException, FirebaseMessagingException;
    ResponseEntity<?> deleteOffer(Long offerId) throws ExecutionException, InterruptedException, FirebaseMessagingException;
    ResponseEntity<?> editOffer(Long offerId, OfferRequest offerRequest);
    ResponseEntity<?> buyOffer(Long offerId, BuyDtoWrapper buyRequest) throws JsonProcessingException, ExecutionException, InterruptedException, FirebaseMessagingException;
    BalanceDto getSubAccountBalance(Long userId, String subAccountId) throws JsonProcessingException;
}
