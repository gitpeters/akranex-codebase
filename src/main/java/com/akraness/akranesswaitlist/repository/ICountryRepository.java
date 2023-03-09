package com.akraness.akranesswaitlist.repository;

import com.akraness.akranesswaitlist.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ICountryRepository extends JpaRepository<Country, Long> {
}
