package com.akraness.akranesswaitlist.chimoney.service;

import com.akraness.akranesswaitlist.chimoney.dto.FundAccountRequestDto;
import org.springframework.http.ResponseEntity;

public interface WalletService {
    ResponseEntity<?> fundAccount(FundAccountRequestDto fundAccountRequestDto);
}
