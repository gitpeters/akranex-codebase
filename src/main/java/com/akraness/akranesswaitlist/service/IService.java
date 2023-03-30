package com.akraness.akranesswaitlist.service;

import com.akraness.akranesswaitlist.dto.*;
import com.akraness.akranesswaitlist.entity.Country;
import com.akraness.akranesswaitlist.entity.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

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

List<Country> getCountries();

ResponseEntity<?> passwordResetRequest(PasswordResetRequestDto passwordResetRequestDto) throws JsonProcessingException;

ResponseEntity<Response> resetPassword(ResetPasswordDto requestDto) throws JsonProcessingException;
ResponseEntity<Response> createAkranexTag(AkranexTagCreationRequestDto requestDto) throws JsonProcessingException;
ResponseEntity<Response> checkAkranexTag(String akranexTag) throws JsonProcessingException;
ResponseEntity<Response> uploadUserProfilePic(MultipartFile file, Long userId) throws Exception;
ResponseEntity<Response> getUser(long userId) throws JsonProcessingException;

String getCurrencyCode(String countryCode);
}
