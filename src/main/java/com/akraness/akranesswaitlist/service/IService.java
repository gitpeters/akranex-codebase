package com.akraness.akranesswaitlist.service;

import com.akraness.akranesswaitlist.dto.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;

import java.security.Principal;

public interface IService {
ResponseEntity<Response> joinWaitList(WaitListRequestDto request);
ResponseEntity<Response> getAllWaitingUsers();
ResponseEntity<Response> getAllCountries();
ResponseEntity<Response> preSignUp(EmailVerificationRequestDto requestDto) throws JsonProcessingException;
ResponseEntity<Response> verifyEmail(EmailVerificationDto requestDto) throws JsonProcessingException;
ResponseEntity<Response> signup(SignupRequestDto requestDto) throws JsonProcessingException;
ResponseEntity<Response> verifyPhone(PhoneVerificationDto requestDto) throws JsonProcessingException;
ResponseEntity<Response> createMagicPin(MagicPinRequestDto requestDto);
ResponseEntity<Response> createTransactionPin(TransactionPinRequestDto requestDto, Principal principal);
ResponseEntity<Response> verifyPin(VerifyPinRequestDto requestDto);
ResponseEntity<Response> resendPhoneOtpCode(ResendPhoneOtpRequest requestDto) throws JsonProcessingException;
}
