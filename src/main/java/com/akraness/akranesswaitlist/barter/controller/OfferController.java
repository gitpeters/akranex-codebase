package com.akraness.akranesswaitlist.barter.controller;

import com.akraness.akranesswaitlist.barter.dto.*;
import com.akraness.akranesswaitlist.barter.service.CurrencyConverterService;
import com.akraness.akranesswaitlist.barter.service.OfferService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/barter")
@RequiredArgsConstructor
public class OfferController {
    private final OfferService offerService;
    private final CurrencyConverterService converterService;

    @PostMapping("/create")
    public ResponseEntity<?> createOffer(@RequestBody OfferRequest request){
        return ResponseEntity.ok().body(offerService.createOffer(request));
    }

    @GetMapping("/offers")
    public ResponseEntity<List<OfferResponse>> getAllOffers(){
        return ResponseEntity.ok().body(offerService.getAllOffers());
    }

    @GetMapping("/my-offer/{akranexTag}")
    public ResponseEntity<List<OfferResponse>> getMyOffers(@PathVariable("akranexTag") String akranexTag){
       return ResponseEntity.ok().body(offerService.getAllOffersByUser(akranexTag));
    }

    @GetMapping("/my-bids/{akranexTag}")
    public ResponseEntity<List<MyBidResponse>> getMyBids(@PathVariable("akranexTag") String akranexTag){
        return ResponseEntity.ok().body(offerService.getAllBidsByUser(akranexTag));
    }

    @GetMapping("/offer/{offerId}")
    public ResponseEntity<OfferResponse> getOffer(@PathVariable("offerId") Long offerId){
        return ResponseEntity.ok().body(offerService.getOffer(offerId));
    }

    @GetMapping("/convert")
    public ResponseEntity<CurrencyConversionResponse> convertCurrency(
            @RequestParam("fromCurrency") String fromCurrency,
            @RequestParam("toCurrency") String toCurrency,
            @RequestParam("amount") double amount) throws JsonProcessingException {
         CurrencyConversionResponse response =converterService.convertCurrency(fromCurrency, toCurrency, amount);
         return ResponseEntity.ok(response);
    }

    @PostMapping("/offer/{offerId}/bids")
    public ResponseEntity<?> bidOffer(@PathVariable("offerId") Long offerId, @RequestBody BidRequest request){
        return ResponseEntity.ok().body(offerService.bidOffer(offerId, request));
    }

    @PostMapping("/offer/buy/{offerId}")
    public ResponseEntity<?> buyOffer(@PathVariable("offerId") Long offerId, @RequestBody BuyDtoWrapper buyRequest) throws JsonProcessingException {
        return offerService.buyOffer(offerId, buyRequest);
    }

    @GetMapping("/get-bidding")
    public ResponseEntity<?> getBidding(@RequestParam("offerId") Long offerId){
        return ResponseEntity.ok().body(offerService.getBids(offerId));
    }

    @PutMapping("/edit/{offerId}")
    public ResponseEntity<?> editOffer(@PathVariable("offerId") Long offerId, @RequestBody OfferRequest request){
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/barter/edit").toUriString());
        return ResponseEntity.created(uri).body(offerService.editOffer(offerId, request));
    }

    @PutMapping("/bid-offers/{bidId}/approve")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<?> approveBidOffer(@PathVariable("bidId") Long bidId){
        return ResponseEntity.ok().body(offerService.approveBid(bidId));
    }

    @PutMapping("/bid-offers/{bidId}/decline")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<?> declineBidOffer(@PathVariable("bidId") Long bidId){
        return ResponseEntity.ok().body(offerService.declineBid(bidId));
    }
}
