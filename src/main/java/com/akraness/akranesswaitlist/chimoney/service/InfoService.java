package com.akraness.akranesswaitlist.chimoney.service;

import com.akraness.akranesswaitlist.config.CustomResponse;
import org.springframework.http.ResponseEntity;

public interface InfoService {
    ResponseEntity<CustomResponse> getCountryBanks(String countryCode);
}
