package com.akraness.akranesswaitlist.repository;

import com.akraness.akranesswaitlist.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IUserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    boolean existsByMobileNumber(String mobileNumber);
    Optional<User> findByUsername(String username);
    Optional<User> findByMobileNumber(String mobileNumber);
    Optional<User> findByEmail(String email);
    Optional<User> findByAkranexTag(String akranexTag);
    Optional<User> findById(Long userdId);
    boolean existsByMagicPin(String magicPin);
    boolean existsByTransactionPin(String transactionPin);
}
