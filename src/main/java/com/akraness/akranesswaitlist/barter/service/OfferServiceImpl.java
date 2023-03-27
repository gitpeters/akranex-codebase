package com.akraness.akranesswaitlist.barter.service;

import com.akraness.akranesswaitlist.barter.dto.OfferRequest;
import com.akraness.akranesswaitlist.barter.dto.OfferResponse;
import com.akraness.akranesswaitlist.barter.model.Offer;
import com.akraness.akranesswaitlist.barter.repository.OfferRepository;
import com.akraness.akranesswaitlist.entity.User;
import com.akraness.akranesswaitlist.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OfferServiceImpl implements OfferService{
    private final OfferRepository offerRepository;
    private final IUserRepository userRepository;

    @Override
    public void createOffer(OfferRequest request) {
        Offer offer = Offer.builder()
                .amountToBePaid(request.getAmountToBePaid())
                .amountToBeReceived(request.getAmountToBeReceived())
                .akranexTag(request.getAkranexTag())
                .receivingCurrency(request.getReceivingCurrency())
                .tradingCurrency(request.getTradingCurrency())
                .rate(request.getRate())
                .build();
        offerRepository.save(offer);
        log.info("Offer {} successfully created", offer.getId());
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
                    .amountToBeReceived(offer.getAmountToBeReceived())
                    .build();
        }
        return null;
    }
    private OfferResponse mapToOfferResponse(Offer offer) {
        return OfferResponse.builder()
                .amountToBePaid(offer.getAmountToBePaid())
                .amountToBeReceived(offer.getAmountToBeReceived())
                .rate(offer.getRate())
                .receivingCurrency(offer.getReceivingCurrency())
                .tradingCurrency(offer.getTradingCurrency())
                .akranexTag(offer.getAkranexTag())
                .build();
    }
}
