package com.akraness.akranesswaitlist.identitypass.repository;

import com.akraness.akranesswaitlist.identitypass.dto.IdentityPassRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IdentityPassRepo extends JpaRepository<IdentityPassRequest, String> {
    IdentityPassRequest findByCountryCode(String countryCode);
}
