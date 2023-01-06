package com.akraness.akranesswaitlist.repository;

import com.akraness.akranesswaitlist.entity.WaitList;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface IWaitList extends CrudRepository<WaitList, Long> {
    Optional<WaitList> findByEmail(String email);
    boolean existsByEmail(String email);
}
