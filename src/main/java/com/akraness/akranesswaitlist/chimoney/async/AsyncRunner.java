package com.akraness.akranesswaitlist.chimoney.async;

import com.akraness.akranesswaitlist.chimoney.entity.SubAccount;
import com.akraness.akranesswaitlist.chimoney.repository.ISubAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class AsyncRunner {
    private final StringRedisTemplate redisTemplate;
    private final ISubAccountRepository subAccountRepository;
    private static final String USER_BALANCE = "user_balance";

    //@Async
    public void removeBalanceFromRedis(List<String> subAccountIds) {
        List<SubAccount> subAccountList = subAccountRepository.getSubAccountUsers(subAccountIds);
        log.info("SubAccount list: "+ subAccountList.size());
        if(subAccountList.isEmpty()) return;

        //get Users of sub accounts
        Set<Long> userIds = subAccountList
                .stream()
                .map(SubAccount::getUserId)
                .collect(Collectors.toSet());

        for(long userId: userIds) {
            redisTemplate.delete(userId+USER_BALANCE);
            //redisTemplate.opsForValue().getAndDelete(userId+USER_BALANCE);
            log.info("Balance for userID: "+userId +" removed from redis");
        }
    }
}
