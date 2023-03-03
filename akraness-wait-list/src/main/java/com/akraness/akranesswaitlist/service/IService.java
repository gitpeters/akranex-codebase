package com.akraness.akranesswaitlist.service;

import com.akraness.akranesswaitlist.dto.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;

public interface IService {
ResponseEntity<Response> joinWaitList(WaitListRequestDto request);
ResponseEntity<Response> getAllWaitingUsers();
ResponseEntity<Response> getAllCountries();
ResponseEntity<Response> preSignUp(EmailVerificationRequestDto requestDto) throws JsonProcessingException;
ResponseEntity<Response> verifyEmail(EmailVerificationDto requestDto) throws JsonProcessingException;
ResponseEntity<Response> signup(SignupRequestDto requestDto) throws JsonProcessingException;
}
