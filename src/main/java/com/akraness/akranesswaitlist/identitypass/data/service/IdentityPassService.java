package com.akraness.akranesswaitlist.identitypass.data.service;

import com.akraness.akranesswaitlist.config.CustomResponse;
import com.akraness.akranesswaitlist.identitypass.data.dto.IdentityPassRequestNumber;
import com.akraness.akranesswaitlist.identitypass.data.dto.IdentityPassRequestPayload;
import org.springframework.http.ResponseEntity;

public interface IdentityPassService {
    ResponseEntity<CustomResponse> validateBvn(IdentityPassRequestNumber request);

    ResponseEntity<CustomResponse> validateNin(IdentityPassRequestNumber request);
    ResponseEntity<CustomResponse> validateVotersCard(IdentityPassRequestPayload request);

    ResponseEntity<CustomResponse> validateIntPassport(IdentityPassRequestPayload request);

    ResponseEntity<CustomResponse> validateDriverLicense(IdentityPassRequestPayload request);
}
