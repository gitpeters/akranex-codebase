package com.akraness.akranesswaitlist.chimoney.service.impl;

import com.akraness.akranesswaitlist.chimoney.dto.BankWrapperDto;
import com.akraness.akranesswaitlist.chimoney.service.InfoService;
import com.akraness.akranesswaitlist.config.CustomResponse;
import com.akraness.akranesswaitlist.config.RestTemplateService;
import com.akraness.akranesswaitlist.entity.Country;
import com.akraness.akranesswaitlist.repository.ICountryRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class InfoServiceImpl implements InfoService {
    private final RestTemplateService restTemplateService;
    @Value("${chimoney.base-url}")
    private String baseUrl;

    @Value("${chimoney.api-key}")
    private String apiKey;
    private final ICountryRepository countryRepository;
    private final StringRedisTemplate redisTemplate;
    private static final String AIRTIME_COUNTRIES = "airtime_countries";
    @Override
    public ResponseEntity<CustomResponse> getCountryBanks(String countryCode) {
        String url = baseUrl + "info/country-banks?countryCode="+countryCode;
        ResponseEntity<CustomResponse> response = restTemplateService.get(url, this.headers());

        return ResponseEntity.ok().body(response.getBody());
    }

    @Override
    public ResponseEntity<?> verifyBankAccountNumber(BankWrapperDto wrapperDto) {
        String url = baseUrl + "info/verify-bank-account-number";
        ResponseEntity<CustomResponse> response = restTemplateService.post(url, wrapperDto, this.headers());

        return ResponseEntity.ok().body(response.getBody());
    }

    @Override
    public List<Country> airtimeCountries() throws JsonProcessingException {
        String url = baseUrl + "info/airtime-countries";
        List<Country> countries = new ArrayList<>();
        ObjectMapper om = new ObjectMapper();

        String countryData = redisTemplate.opsForValue().get(AIRTIME_COUNTRIES);
        if(Objects.nonNull(countryData)) {
            return om.readValue(countryData, new TypeReference<List<Country>>(){});
        }

        ResponseEntity<CustomResponse> response = restTemplateService.get(url, this.headers());
        if(response.getStatusCodeValue() == HttpStatus.OK.value()){
            countries = countryRepository.getAirtimeCountries((List<String>) response.getBody().getData());
            redisTemplate.opsForValue().set(AIRTIME_COUNTRIES, om.writeValueAsString(countries));
        }

        return countries;
    }

    private HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-KEY", apiKey);

        return headers;
    }
}
