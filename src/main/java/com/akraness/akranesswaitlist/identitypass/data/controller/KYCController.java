package com.akraness.akranesswaitlist.identitypass.data.controller;

import com.akraness.akranesswaitlist.identitypass.data.dto.IdentityPassRequestNumber;
import com.akraness.akranesswaitlist.identitypass.data.dto.IdentityPassRequestPayload;
import com.akraness.akranesswaitlist.identitypass.data.service.IdentityPassService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/identitypass")
@RequiredArgsConstructor
public class KYCController {
    private final IdentityPassService identityPassService;

    @PostMapping("/validate/ng/bvn")
    public ResponseEntity<?> validateBvn(@RequestBody IdentityPassRequestNumber request) {
        return identityPassService.validateBvn(request);
    }
    @PostMapping("/validate/ng/nin")
    public ResponseEntity<?> validateNin(@RequestBody IdentityPassRequestNumber request) {
        return identityPassService.validateNin(request);
    }
    @PostMapping("/validate/ng/voters_card")
    public ResponseEntity<?> validateVotersCard(@RequestBody IdentityPassRequestPayload request) {
        return identityPassService.validateVotersCard(request);
    }

    @PostMapping("/validate/ng/national_passport")
    public ResponseEntity<?> validateVotersIntPassport(@RequestBody IdentityPassRequestPayload request) {
        return identityPassService.validateIntPassport(request);
    }

    @PostMapping("/validate/ng/drivers_license")
    public ResponseEntity<?> validateVotersDriversLicense(@RequestBody IdentityPassRequestPayload request) {
        return identityPassService.validateDriverLicense(request);
    }
}
