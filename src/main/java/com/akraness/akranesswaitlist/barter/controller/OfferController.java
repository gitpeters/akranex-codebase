package com.akraness.akranesswaitlist.barter.controller;

import com.akraness.akranesswaitlist.barter.dto.CurrencyConvertRequest;
import com.akraness.akranesswaitlist.barter.dto.OfferRequest;
import com.akraness.akranesswaitlist.barter.dto.OfferResponse;
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
    public ResponseEntity createOffer(@RequestBody OfferRequest request){
        offerService.createOffer(request);
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/barter/create").toUriString());
         return ResponseEntity.created(uri).build();
    }

    @GetMapping("/offers")
    public ResponseEntity<List<OfferResponse>> getAllOffers(){
        return ResponseEntity.ok().body(offerService.getAllOffers());
    }

    @GetMapping("/my-offer/{akranexTag}")
    public ResponseEntity<List<OfferResponse>> getMyOffers(@PathVariable("akranexTag") String akranexTag){
       return ResponseEntity.ok().body(offerService.getAllOffersByUser(akranexTag));
    }

    @GetMapping("/offer/{offerId}")
    public ResponseEntity<OfferResponse> getOffer(@PathVariable("offerId") Long offerId){
        return ResponseEntity.ok().body(offerService.getOffer(offerId));
    }

    @GetMapping("/covert-currency")
    public ResponseEntity<?> convertCurrency(@RequestParam("destinationCurrency") String currencyCode, @RequestParam("amountInUSD") double amount) throws JsonProcessingException {
        CurrencyConvertRequest convertRequest = converterService.getBalanceInLocalCurrency(currencyCode, amount);
        return ResponseEntity.ok().body(convertRequest);
    }
}
