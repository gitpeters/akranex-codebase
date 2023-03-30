package com.akraness.akranesswaitlist.chimoney.service;

import com.akraness.akranesswaitlist.chimoney.dto.BankWrapperDto;
import com.akraness.akranesswaitlist.config.CustomResponse;
import com.akraness.akranesswaitlist.entity.Country;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface InfoService {
    ResponseEntity<CustomResponse> getCountryBanks(String countryCode);

    ResponseEntity<?> verifyBankAccountNumber(BankWrapperDto wrapperDto);
    List<Country> airtimeCountries() throws JsonProcessingException;
}
