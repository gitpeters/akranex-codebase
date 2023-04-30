package com.akraness.akranesswaitlist.chimoney.repository;

import com.akraness.akranesswaitlist.chimoney.entity.SubAccount;
import com.akraness.akranesswaitlist.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ISubAccountRepository extends JpaRepository<SubAccount, Long> {
    List<SubAccount> findByUserId(Long userId);
    Optional<SubAccount> findByUserIdAndCountryCode(Long userId, String countryCode);
    Optional<SubAccount> findByUserIdAndCurrencyCode(Long userId, String countryCode);
    Optional<SubAccount> findBySubAccountId(String subAccount);
    @Query( "select s from SubAccount s where s.subAccountId in :ids" )
    List<SubAccount> getSubAccountUsers(@Param("ids") List<String> ids);

    @Query( "select s from SubAccount s where s.userId in :userId and s.countryCode in :countryCode" )
    List<SubAccount> getSubAccountsByUserIdsAndCountryCodes(@Param("userId") List<Long> ids, @Param("countryCode") List<String> countryCodes);
}
