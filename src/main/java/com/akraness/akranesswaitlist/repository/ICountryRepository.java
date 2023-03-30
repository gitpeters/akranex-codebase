package com.akraness.akranesswaitlist.repository;

import com.akraness.akranesswaitlist.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ICountryRepository extends JpaRepository<Country, Long> {
    Optional<Country> findByCode(String countryCode);

    @Query( "select c from Country c where c.name in :names" )
    List<Country> getAirtimeCountries(@Param("names") List<String> countryList);
}
