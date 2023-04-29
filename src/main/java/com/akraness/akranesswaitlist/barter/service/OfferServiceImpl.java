package com.akraness.akranesswaitlist.barter.service;

import com.akraness.akranesswaitlist.barter.dto.*;
import com.akraness.akranesswaitlist.barter.model.BidOffer;
import com.akraness.akranesswaitlist.barter.model.BidStatus;
import com.akraness.akranesswaitlist.barter.model.Offer;
import com.akraness.akranesswaitlist.barter.model.OfferStatus;
import com.akraness.akranesswaitlist.barter.repository.BidOfferRepository;
import com.akraness.akranesswaitlist.barter.repository.OfferRepository;
import com.akraness.akranesswaitlist.chimoney.async.AsyncRunner;
import com.akraness.akranesswaitlist.chimoney.dto.BalanceDto;
import com.akraness.akranesswaitlist.chimoney.entity.SubAccount;
import com.akraness.akranesswaitlist.chimoney.repository.ISubAccountRepository;
import com.akraness.akranesswaitlist.config.CustomResponse;
import com.akraness.akranesswaitlist.config.RestTemplateService;
import com.akraness.akranesswaitlist.entity.User;
import com.akraness.akranesswaitlist.repository.IUserRepository;
import com.akraness.akranesswaitlist.util.Utility;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OfferServiceImpl implements OfferService {
    private final OfferRepository offerRepository;
    private final IUserRepository userRepository;
    private final RestTemplateService restTemplateService;

    private final BidOfferRepository bidRepository;
    private final ISubAccountRepository subAccountRepository;
    private final StringRedisTemplate redisTemplate;

    private final CurrencyConverterService converterService;

    private final AsyncRunner asyncRunner;

    private final ObjectMapper objectMapper;
    private final Utility utility;

    @Value("${chimoney.base-url}")
    private String baseUrl;
    @Value("${chimoney.api-key}")
    private String apiKey;
    private static final String USER_BALANCE = "user_balance";

    @Override
    public ResponseEntity<?> createOffer(OfferRequest request) {
        Optional<User> userOpt = userRepository.findByAkranexTag(request.getAkranexTag());
        if (!userOpt.isPresent()) {
            return ResponseEntity.badRequest().body(new BarterResponse(false, "No user found"));
        }
        User user = userOpt.get();
        if (!request.getAkranexTag().equals(user.getAkranexTag())) {
            return ResponseEntity.badRequest().body(new BarterResponse(false, "You are not authorized to create offer"));
        }
        double transactionFee = request.getAmountToBePaid() * 0.6 / 100;
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
        if (request.getBidAmount() == 0) {
            return ResponseEntity.badRequest().body(new BarterResponse(false, "Bid amount cannot be 0.00"));
        }

        if (request.getBidAmount() > offer.getAmountToBePaid()) {
            return ResponseEntity.badRequest().body(new BarterResponse(false, "Bid amount cannot more than offer amount"));
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

        if (bids.isEmpty()) {
            return ResponseEntity.badRequest().body(new BarterResponse(false, "No record found"));
        }
        Optional<BidOffer> bidOfferOpt = bidRepository.findById(offer.getId());
        if (!bidOfferOpt.isPresent()) {
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
        if (!bidOfferOpt.isPresent()) {
            return ResponseEntity.badRequest().body(new BarterResponse(false, "Bid offer not found"));
        }
        BidOffer bidOffer = bidOfferOpt.get();
        Optional<User> userOpt = userRepository.findByAkranexTag(bidOffer.getAkranexTag());
        if (!userOpt.isPresent()) {
            return ResponseEntity.badRequest().body(new BarterResponse(false, "No user found for this bid"));
        }
        User user = userOpt.get();
        if (!bidOffer.getAkranexTag().equalsIgnoreCase(user.getAkranexTag())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new BarterResponse(false, "You are not authorized to approve this bid offer"));
        }
        if (!bidOffer.getBidStatus().equalsIgnoreCase(String.valueOf(BidStatus.PENDING))) {
            return ResponseEntity.badRequest().body(new BarterResponse(false, "It is either this bid as been accepted or declined"));
        }
        bidOffer.setBidStatus(String.valueOf(BidStatus.ACCEPTED));
        bidRepository.save(bidOffer);
        return ResponseEntity.ok().body(new BarterResponse(true, "Successfully accepted bid offer"));
    }

    @Override
    public ResponseEntity<?> declineBid(Long bidId) {
        Optional<BidOffer> bidOfferOpt = bidRepository.findById(bidId);
        if (!bidOfferOpt.isPresent()) {
            return ResponseEntity.badRequest().body(new BarterResponse(false, "Bid offer not found"));
        }
        BidOffer bidOffer = bidOfferOpt.get();
        Optional<User> userOpt = userRepository.findByAkranexTag(bidOffer.getAkranexTag());
        if (!userOpt.isPresent()) {
            return ResponseEntity.badRequest().body(new BarterResponse(false, "No user found for this bid"));
        }
        User user = userOpt.get();
        if (!bidOffer.getAkranexTag().equalsIgnoreCase(user.getAkranexTag())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new BarterResponse(false, "You are not authorized to decline this bid offer"));
        }
        if (!bidOffer.getBidStatus().equalsIgnoreCase(String.valueOf(BidStatus.PENDING))) {
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

    @Override
    public ResponseEntity<?> buyOffer(Long offerId, BuyDtoWrapper buyRequest) throws JsonProcessingException {
        Map<String, String> sellerReq = new HashMap<>();
        Map<String, String> buyerReq = new HashMap<>();
        ResponseEntity<CustomResponse> sellerResponse = null;
        ResponseEntity<CustomResponse> buyerResponse = null;

        String sellerFromSubAccountId = "";
        String sellerCurrency = "";
        String buyerCurrency ="";
        String buyerFromSubAccountId = "";
        String sellerToSubAccountId = "";
        String buyerToSubAccountId = "";

        if(buyRequest.getBuyer()==null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CustomResponse.builder().status(HttpStatus.BAD_REQUEST.name()).error("Buyer request body cannot be empty").build());
            }
        if(buyRequest.getSeller()==null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CustomResponse.builder().status(HttpStatus.BAD_REQUEST.name()).error("Seller request body cannot be empty").build());
        }
        Optional<Offer> offerOpt = offerRepository.findById(offerId);
        if (!offerOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    CustomResponse.builder()
                            .status(HttpStatus.BAD_REQUEST.name())
                            .error("No offer found")
                            .build());
        }

        Offer offer = offerOpt.get();
        Optional<User> sellerOpt = userRepository.findByAkranexTag(buyRequest.getSeller().getAkranexTag());
        Optional<User> buyerOpt = userRepository.findByAkranexTag(buyRequest.getBuyer().getAkranexTag());

        if (!sellerOpt.isPresent() || !buyerOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    CustomResponse.builder()
                            .status(HttpStatus.BAD_REQUEST.name())
                            .error("Seller or Buyer not found")
                            .build());

        }
        User buyer = buyerOpt.get();
        User seller = sellerOpt.get();

        //Optional<SubAccount> buyerSubAccountOpt = subAccountRepository.findByUserIdAndCountryCode(buyer.getId(), buyRequest.getBuyer().getFromCountryCode());
        //Optional<SubAccount> sellerSubAccountOpt = subAccountRepository.findByUserIdAndCountryCode(seller.getId(), buyRequest.getSeller().getFromCountryCode());
        List<SubAccount> subAccountList =
                subAccountRepository.getSubAccountsByUserIdsAndCountryCodes(Arrays.asList(buyer.getId(), seller.getId()),
                        Arrays.asList(buyRequest.getBuyer().getFromCountryCode(), buyRequest.getSeller().getFromCountryCode()));
            for(SubAccount subAccount: subAccountList){
                if(subAccount.getCountryCode().equals(buyRequest.getSeller().getFromCountryCode()) && subAccount.getUserId()==seller.getId()){
                    sellerFromSubAccountId = subAccount.getSubAccountId();
                    sellerCurrency = subAccount.getCurrencyCode();
                    log.info("Seller from account {}", sellerFromSubAccountId);
                }
                if(subAccount.getCountryCode().equals(buyRequest.getSeller().getToCountryCode()) && subAccount.getUserId()==seller.getId()){
                    sellerToSubAccountId = subAccount.getSubAccountId();
                    log.info("Seller to account {}", sellerToSubAccountId);
                }
                if(subAccount.getCountryCode().equals(buyRequest.getBuyer().getFromCountryCode()) && subAccount.getUserId()==buyer.getId()){
                    buyerFromSubAccountId = subAccount.getSubAccountId();
                    buyerCurrency = subAccount.getCurrencyCode();
                    log.info("Buyer from account {}", buyerFromSubAccountId);
                }
                if(subAccount.getCountryCode().equals(buyRequest.getBuyer().getToCountryCode()) && subAccount.getUserId()==buyer.getId()){
                    buyerToSubAccountId = subAccount.getSubAccountId();
                    log.info("Buyer to account {}", buyerToSubAccountId);
                }
            }

//



        // Check if seller sub account has enough fund to sell the offer
//        BalanceDto sellerBalance = getUserBalance(seller.getId(), buyRequest.getSeller().getFromCountryCode());
//        if (sellerBalance == null || sellerBalance.getAmountInLocalCurrency() < buyRequest.getSeller().getAmount()) {
//            CustomResponse customResponse = CustomResponse.builder()
//                    .status(HttpStatus.BAD_REQUEST.name())
//                    .error("Insufficient balance in seller's sub account")
//                    .build();
//            return new ResponseEntity<>(customResponse, HttpStatus.BAD_REQUEST);
//        }

        // Check if buyer sub account has enough fund to sell the offer
//        BalanceDto buyerBalance = getUserBalance(buyer.getId(), buyRequest.getBuyer().getFromCountryCode());
//        if (buyerBalance == null || buyerBalance.getAmountInLocalCurrency() < buyRequest.getBuyer().getAmount()) {
//            CustomResponse customResponse = CustomResponse.builder()
//                    .status(HttpStatus.BAD_REQUEST.name())
//                    .error("Insufficient balance in buyer's sub account")
//                    .build();
//            return new ResponseEntity<>(customResponse, HttpStatus.BAD_REQUEST);
//
//        }

       double convertedSellerAmount =  converterService.convertToUSD(Currency.getInstance(sellerCurrency), buyRequest.getSeller().getAmount());
        double convertedBuyerAmount =  converterService.convertToUSD(Currency.getInstance(buyerCurrency), buyRequest.getBuyer().getAmount());

        // mapping buyer request for fund transfer
        buyerReq.put("receiver", sellerToSubAccountId);
        buyerReq.put("subAccount", buyerFromSubAccountId);
        buyerReq.put("valueInUSD", String.valueOf(convertedSellerAmount));
        buyerReq.put("wallet", "chi");

        String url = baseUrl + "wallets/transfer";
        buyerResponse = restTemplateService.post(url, buyerReq, this.headers());

        // mapping seller request for fund transfer
        sellerReq.put("receiver", buyerToSubAccountId);
        sellerReq.put("subAccount", sellerFromSubAccountId);
        sellerReq.put("valueInUSD", String.valueOf(convertedBuyerAmount));
        sellerReq.put("wallet", "chi");
        sellerResponse = restTemplateService.post(url, sellerReq, this.headers());

        if (buyerResponse.getStatusCodeValue() == HttpStatus.OK.value() && buyerResponse.getBody().getStatus().equalsIgnoreCase("success")
                && sellerResponse.getStatusCodeValue() == HttpStatus.OK.value() && sellerResponse.getBody().getStatus().equalsIgnoreCase("success")) {
            offer.setOfferStatus(String.valueOf(OfferStatus.FULFILLED));
            offerRepository.save(offer);

            //asyncRunner.removeBalanceFromRedis(Arrays.asList(buyerFromSubAccountId, sellerFromSubAccountId, buyerToSubAccountId, sellerToSubAccountId));

            return ResponseEntity.ok().body(CustomResponse.builder()
                    .status(HttpStatus.OK.name())
                    .data(Arrays.asList(buyerResponse.getBody(), sellerResponse.getBody()))
                    .build());
        } else {
            String error = "";
            if (buyerResponse.getStatusCodeValue() != HttpStatus.OK.value() || !buyerResponse.getBody().getStatus().equalsIgnoreCase("success")) {
                error += "Fund transfer failed for buyer. ";
            }
            if (sellerResponse.getStatusCodeValue() != HttpStatus.OK.value() || !sellerResponse.getBody().getStatus().equalsIgnoreCase("success")) {
                error += "Fund transfer failed for seller.";
            }
            HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.badRequest().body(CustomResponse.builder()
                    .status(HttpStatus.BAD_REQUEST.name())
                    .error(error)
                    .build());
        }
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
    private BalanceDto getUserBalance(Long userId, String countryCode) throws JsonProcessingException {
        String balanceData = redisTemplate.opsForValue().get(userId + USER_BALANCE);
        if (Objects.nonNull(balanceData)) {
            List<BalanceDto> balanceDtos = objectMapper.readValue(balanceData, new TypeReference<List<BalanceDto>>() {});
            Optional<SubAccount> subAccountOpt = subAccountRepository.findByUserIdAndCountryCode(userId, countryCode);
            if (subAccountOpt.isPresent()) {
                SubAccount subAccount = subAccountOpt.get();
                return balanceDtos.stream()
                        .filter(balanceDto -> balanceDto.getSubAccountId().equals(subAccount.getSubAccountId()))
                        .findFirst()
                        .orElse(null);
            }
        }
        return null;
    }
    private HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-KEY", apiKey);

        return headers;
    }
}
