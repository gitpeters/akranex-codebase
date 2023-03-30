package com.akraness.akranesswaitlist.repository;

import com.akraness.akranesswaitlist.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ICountryRepository extends JpaRepository<Country, Long> {
    Optional<Country> findByCode(String countryCode);
}
