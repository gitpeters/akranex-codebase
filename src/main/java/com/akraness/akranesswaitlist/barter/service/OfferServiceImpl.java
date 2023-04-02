package com.akraness.akranesswaitlist.barter.service;

import com.akraness.akranesswaitlist.barter.dto.*;
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
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<?> createOffer(OfferRequest request) {
        Optional<User> userOpt = userRepository.findByAkranexTag(request.getAkranexTag());
        if(!userOpt.isPresent()){
            return ResponseEntity.badRequest().body(new BarterResponse(false, "No user found"));
        }
        User user = userOpt.get();
        if (!request.getAkranexTag().equals(user.getAkranexTag())) {
            return ResponseEntity.badRequest().body(new BarterResponse(false, "You are not authorized to create offer"));
        }
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
        return ResponseEntity.ok().body(new BarterResponse(true, "Successfully created offer"));
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
    public List<MyBidResponse> getAllBidsByUser(String akranexTag) {
        List<BidOffer> offers = (List<BidOffer>) bidRepository.findByAkranexTag(akranexTag);
        return offers.stream()
                .map(this::mapToBidResponse)
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
                    .offerId(offer.getId())
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
            return ResponseEntity.badRequest().body(new BarterResponse(false, "Offer not found"));
        }
        Offer offer = offerObj.get();
        if (request.getBidAmount()==0) {
            return ResponseEntity.badRequest().body(new BarterResponse(false, "Bid amount cannot be 0.00"));
        }

        if(request.getBidAmount()> offer.getAmountToBePaid()){
            return ResponseEntity.badRequest().body(new BarterResponse(false,"Bid amount cannot more than offer amount"));
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
            return ResponseEntity.badRequest().body(new BarterResponse(false, "Offer not found"));
        }
        Offer offer = offerObj.get();
        List<BidOffer> bids = bidRepository.findByOfferId(offerId);

        if(bids.isEmpty()){
            return ResponseEntity.badRequest().body(new BarterResponse(false, "No record found"));
        }
        Optional<BidOffer> bidOfferOpt = bidRepository.findById(offer.getId());
        if(!bidOfferOpt.isPresent()){
            return ResponseEntity.badRequest().body(new BarterResponse(false, "Bid not found"));
        }
        List<BidResponse> bidResponses = bids.stream()
                .filter(bid -> bid.getBidStatus().equalsIgnoreCase(String.valueOf(BidStatus.PENDING)))
                .map(bid ->
                        BidResponse.builder()
                                .bidId(bid.getId())
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

        if (bidResponses.isEmpty()) {
            return ResponseEntity.badRequest().body(new BarterResponse(false, "No pending bids found"));
        }

        OfferResponse offerResponse = OfferResponse.builder()
                .offerId(offer.getId())
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
    public ResponseEntity<?> approveBid(Long bidId) {
        Optional<BidOffer> bidOfferOpt = bidRepository.findById(bidId);
        if(!bidOfferOpt.isPresent()){
            return ResponseEntity.badRequest().body(new BarterResponse(false,"Bid offer not found"));
        }
        BidOffer bidOffer = bidOfferOpt.get();
        Optional<User> userOpt = userRepository.findByAkranexTag(bidOffer.getAkranexTag());
        if(!userOpt.isPresent()){
            return ResponseEntity.badRequest().body(new BarterResponse(false, "No user found for this bid"));
        }
        User user = userOpt.get();
        if(!bidOffer.getAkranexTag().equalsIgnoreCase(user.getAkranexTag())){
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new BarterResponse(false,"You are not authorized to approve this bid offer"));
        }
        if(!bidOffer.getBidStatus().equalsIgnoreCase(String.valueOf(BidStatus.PENDING))){
            return ResponseEntity.badRequest().body(new BarterResponse(false, "It is either this bid as been accepted or declined"));
        }
        bidOffer.setBidStatus(String.valueOf(BidStatus.ACCEPTED));
        bidRepository.save(bidOffer);
        return ResponseEntity.ok().body(new BarterResponse(true, "Successfully accepted bid offer"));
    }

    @Override
    public ResponseEntity<?> declineBid(Long bidId) {
        Optional<BidOffer> bidOfferOpt = bidRepository.findById(bidId);
        if(!bidOfferOpt.isPresent()){
            return ResponseEntity.badRequest().body(new BarterResponse(false,"Bid offer not found"));
        }
        BidOffer bidOffer = bidOfferOpt.get();
        Optional<User> userOpt = userRepository.findByAkranexTag(bidOffer.getAkranexTag());
        if(!userOpt.isPresent()){
            return ResponseEntity.badRequest().body(new BarterResponse(false, "No user found for this bid"));
        }
        User user = userOpt.get();
        if(!bidOffer.getAkranexTag().equalsIgnoreCase(user.getAkranexTag())){
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new BarterResponse(false,"You are not authorized to decline this bid offer"));
        }
        if(!bidOffer.getBidStatus().equalsIgnoreCase(String.valueOf(BidStatus.PENDING))){
            return ResponseEntity.badRequest().body(new BarterResponse(false, "It is either this bid as been accepted or declined"));
        }
        bidOffer.setBidStatus(String.valueOf(BidStatus.DECLINED));
        bidRepository.save(bidOffer);
        return ResponseEntity.ok().body(new BarterResponse(true, "Successfully declined bid offer"));
    }

    @Override
    public ResponseEntity<?> editOffer(Long offerId, OfferRequest offerRequest) {
        Optional<Offer> offerObj = offerRepository.findById(offerId);
        if (!offerObj.isPresent()) {
            return ResponseEntity.badRequest().body(new BarterResponse(false, "Offer not found"));
        }
        Offer offer = offerObj.get();
        if (!offer.getAkranexTag().equals(offerRequest.getAkranexTag())) {
            return ResponseEntity.badRequest().body(new BarterResponse(false, "You are not authorized to update this offer"));
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
                .offerId(offer.getId())
                .amountToBePaid(offer.getAmountToBePaid())
                .amountToBeReceived(offer.getAmountToBeReceived())
                .rate(offer.getRate())
                .transactionFee(offer.getTransactionFee())
                .receivingCurrency(offer.getReceivingCurrency())
                .tradingCurrency(offer.getTradingCurrency())
                .akranexTag(offer.getAkranexTag())
                .build();
    }

    private MyBidResponse mapToBidResponse(BidOffer bidOffer) {
        return MyBidResponse.builder()
                .bidId(bidOffer.getId())
                .bidAmount(bidOffer.getAmountToBePaid())
                .askedAmount(bidOffer.getOfferAmount())
                .rate(bidOffer.getRate())
                .status(bidOffer.getBidStatus())
                .build();
    }
}
