package com.akraness.akranesswaitlist.service;

import com.akraness.akranesswaitlist.dto.Response;
import com.akraness.akranesswaitlist.dto.WaitListRequestDto;
import org.springframework.http.ResponseEntity;

public interface IService {
ResponseEntity<Response> joinWaitList(WaitListRequestDto request);
}
