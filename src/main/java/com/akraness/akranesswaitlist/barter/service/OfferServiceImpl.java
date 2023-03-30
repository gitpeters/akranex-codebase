package com.akraness.akranesswaitlist.barter.service;

import com.akraness.akranesswaitlist.barter.dto.BidRequest;
import com.akraness.akranesswaitlist.barter.dto.BidResponse;
import com.akraness.akranesswaitlist.barter.dto.OfferRequest;
import com.akraness.akranesswaitlist.barter.dto.OfferResponse;
import com.akraness.akranesswaitlist.barter.model.BidOffer;
import com.akraness.akranesswaitlist.barter.model.BidStatus;
import com.akraness.akranesswaitlist.barter.model.Offer;
import com.akraness.akranesswaitlist.barter.model.OfferStatus;
import com.akraness.akranesswaitlist.barter.repository.BidOfferRepository;
import com.akraness.akranesswaitlist.barter.repository.OfferRepository;
import com.akraness.akranesswaitlist.entity.User;
import com.akraness.akranesswaitlist.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OfferServiceImpl implements OfferService{
    private final OfferRepository offerRepository;
    private final IUserRepository userRepository;

    private final BidOfferRepository bidRepository;

    @Override
    public ResponseEntity<OfferResponse> createOffer(OfferRequest request) {
        double transactionFee = request.getAmountToBePaid()*0.6/100;
        Offer offer = Offer.builder()
                .amountToBePaid(request.getAmountToBePaid())
                .amountToBeReceived(request.getAmountToBeReceived())
                .akranexTag(request.getAkranexTag())
                .receivingCurrency(request.getReceivingCurrency())
                .tradingCurrency(request.getTradingCurrency())
                .rate(request.getRate())
                .transactionFee(transactionFee)
                .offerStatus(String.valueOf(OfferStatus.PENDING))
                .build();
        offerRepository.save(offer);
        log.info("Offer {} successfully created", offer.getId());
        return ResponseEntity.ok().body(new OfferResponse(true, "Successfully created offer"));
    }

    @Override
    public List<OfferResponse> getAllOffers() {
        List<Offer> offers = offerRepository.findAll();
       return offers
               .stream()
               .map(this::mapToOfferResponse).collect(Collectors.toList());
    }

    @Override
    public List<OfferResponse> getAllOffersByUser(String akranexTag) {
        List<Offer> offers = (List<Offer>) offerRepository.findByAkranexTag(akranexTag);
        return offers.stream()
                .map(this::mapToOfferResponse)
                .collect(Collectors.toList());
    }

    @Override
    public OfferResponse getOffer(Long offerId) {
        Optional<Offer> offerObj = offerRepository.findById(offerId);
        if (offerObj.isPresent()) {
            Offer offer = offerObj.get();
            String username = userRepository.findByAkranexTag(offer.getAkranexTag())
                    .map(User::getUsername)
                    .orElse("User not found!");
            return OfferResponse.builder()
                    .amountToBePaid(offer.getAmountToBePaid())
                    .username(username)
                    .tradingCurrency(offer.getTradingCurrency())
                    .receivingCurrency(offer.getReceivingCurrency())
                    .rate(offer.getRate())
                    .transactionFee(offer.getTransactionFee())
                    .amountToBeReceived(offer.getAmountToBeReceived())
                    .build();
        }
        return null;
    }

    @Override
    public ResponseEntity<?> bidOffer(Long offerId, BidRequest request) {
        Optional<Offer> offerObj = offerRepository.findById(offerId);
        if (!offerObj.isPresent()) {
            return ResponseEntity.badRequest().body(new BidResponse(String.valueOf(BidStatus.DECLINED), "Offer not found"));
        }
        Offer offer = offerObj.get();
        if (request.getBidAmount()==0) {
            return ResponseEntity.badRequest().body(new BidResponse(String.valueOf(BidStatus.DECLINED), "Bid amount cannot be 0.00"));
        }

        if(request.getBidAmount()> offer.getAmountToBePaid()){
            return ResponseEntity.badRequest().body(new BidResponse(String.valueOf(BidStatus.DECLINED), "Bid amount cannot more than offer amount"));
        }
           BidOffer bidOffer = BidOffer.builder()
                .offerId(offer.getId())
                .amountToBePaid(request.getBidAmount())
                .amountToBeReceived(request.getReceivingAmount())
                   .rate(request.getRate())
                   .akranexTag(request.getAkranexTag())
                   .bidStatus(String.valueOf(BidStatus.PENDING))
                .receivingCurrency(offer.getReceivingCurrency())
                .tradingCurrency(offer.getTradingCurrency())
                .offerAmount(offer.getAmountToBePaid())
                .offerRate(offer.getRate())
                .build();
        return ResponseEntity.ok().body(bidRepository.save(bidOffer));
    }

    @Override
    public ResponseEntity<?> getBids(Long offerId) {
        Optional<Offer> offerObj = offerRepository.findById(offerId);
        if (!offerObj.isPresent()) {
            return ResponseEntity.badRequest().body(new BidResponse(String.valueOf(BidStatus.DECLINED), "Offer not found"));
        }
        Offer offer = offerObj.get();
        List<BidOffer> bids = bidRepository.findByOfferId(offerId);
        List<BidResponse> bidResponses = bids.stream().map(bid ->
                BidResponse.builder()
                        .bidAmount(bid.getAmountToBePaid())
                        .receivingAmount(bid.getAmountToBeReceived())
                        .rate(bid.getRate())
                        .offerId(bid.getOfferId())
                        .bidCurrency(bid.getTradingCurrency())
                        .receivingCurrency(bid.getReceivingCurrency())
                        .bidStatus(bid.getBidStatus())
                        .akranexTag(bid.getAkranexTag())
                        .build()
        ).collect(Collectors.toList());
        OfferResponse offerResponse = OfferResponse.builder()
                .amountToBePaid(offer.getAmountToBePaid())
                .amountToBeReceived(offer.getAmountToBeReceived())
                .receivingCurrency(offer.getReceivingCurrency())
                .tradingCurrency(offer.getTradingCurrency())
                .rate(offer.getRate())
                .bids(bidResponses)
                .build();
        return ResponseEntity.ok().body(offerResponse);
    }

    @Override
    public ResponseEntity<OfferResponse> editOffer(Long offerId, OfferRequest offerRequest) {
        Optional<Offer> offerObj = offerRepository.findById(offerId);
        if (!offerObj.isPresent()) {
            return ResponseEntity.badRequest().body(new OfferResponse(false, "Offer not found"));
        }
        Offer offer = offerObj.get();
        if (!offer.getAkranexTag().equals(offerRequest.getAkranexTag())) {
            return ResponseEntity.badRequest().body(new OfferResponse(false, "You are not authorized to update this offer"));
        }
        offer.setAmountToBePaid(offerRequest.getAmountToBePaid());
        offer.setAmountToBeReceived(offerRequest.getAmountToBeReceived());
        offer.setRate(offerRequest.getRate());
        offer.setTradingCurrency(offerRequest.getTradingCurrency());
        offer.setReceivingCurrency(offerRequest.getReceivingCurrency());

        Offer savedOffer = offerRepository.save(offer);

        // Build and return a response with the updated offer details
        OfferResponse offerResponse = OfferResponse.builder()
                .amountToBePaid(savedOffer.getAmountToBePaid())
                .amountToBeReceived(savedOffer.getAmountToBeReceived())
                .rate(savedOffer.getRate())
                .tradingCurrency(savedOffer.getTradingCurrency())
                .receivingCurrency(savedOffer.getReceivingCurrency())
                .akranexTag(savedOffer.getAkranexTag())
                .username(savedOffer.getAkranexTag())
                .offerStatus(true)
                .offerMessage("Successfully updated offer")
                .build();
        return ResponseEntity.ok().body(offerResponse);
    }

    private OfferResponse mapToOfferResponse(Offer offer) {
        return OfferResponse.builder()
                .amountToBePaid(offer.getAmountToBePaid())
                .amountToBeReceived(offer.getAmountToBeReceived())
                .rate(offer.getRate())
                .transactionFee(offer.getTransactionFee())
                .receivingCurrency(offer.getReceivingCurrency())
                .tradingCurrency(offer.getTradingCurrency())
                .akranexTag(offer.getAkranexTag())
                .build();
    }
}
