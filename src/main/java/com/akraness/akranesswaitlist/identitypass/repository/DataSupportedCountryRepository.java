package com.akraness.akranesswaitlist.identitypass.repository;

import com.akraness.akranesswaitlist.identitypass.entity.DataSupportedCountry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DataSupportedCountryRepository  extends JpaRepository<DataSupportedCountry, Long> {
    Optional<DataSupportedCountry> findByCountryCode(String countryCode);
}
