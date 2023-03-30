package com.akraness.akranesswaitlist.chimoney.repository;

import com.akraness.akranesswaitlist.chimoney.entity.SubAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ISubAccountRepository extends JpaRepository<SubAccount, Long> {
    List<SubAccount> findByUserId(Long userId);
    Optional<SubAccount> findByUserIdAndCountryCode(Long userId, String countryCode);

    Optional<SubAccount> findBySubAccountId(String subAccount);
}
