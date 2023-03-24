package com.akraness.akranesswaitlist.controller;

import com.akraness.akranesswaitlist.dto.*;
import com.akraness.akranesswaitlist.service.INotificationService;
import com.akraness.akranesswaitlist.service.IService;
import com.akraness.akranesswaitlist.serviceimpl.AuthenticationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api/v1/users/no-auth")
@RequiredArgsConstructor
@Slf4j
public class NonAuthController {
    private final IService service;
    private final INotificationService notificationService;
    private final AuthenticationService authenticationService;



    @PostMapping("/join-waitlist")
    public ResponseEntity<Response> users(@RequestBody @Validated WaitListRequestDto requestDto) {
        return service.joinWaitList(requestDto);
    }

    @GetMapping
    public ResponseEntity<Response> getAllWaitingUsers() {
        return service.getAllWaitingUsers();
    }
    @GetMapping("/countries")
    public ResponseEntity<Response> getAllCountries() {
        return service.getAllCountries();
    }

//    @PostMapping("/kafka")
//    public ResponseEntity<Response> users(@RequestBody NotificationDto requestDto) throws JsonProcessingException {
//        notificationService.sendNotification(requestDto);
//        return ResponseEntity.ok(new Response("90000","Successful",null));
//    }
    @PostMapping("/pre-signup")
    public ResponseEntity<Response> preSignup(@RequestBody @Validated EmailVerificationRequestDto requestDto) throws JsonProcessingException {
        return service.preSignUp(requestDto);
    }

    @PostMapping("/verify-email")
    public ResponseEntity<Response> verifyEmail(@RequestBody @Validated EmailVerificationDto requestDto) throws JsonProcessingException {
        return service.verifyEmail(requestDto);
    }

    @PostMapping("/signup")
    public ResponseEntity<Response> Signup(@RequestBody @Validated SignupRequestDto requestDto) throws JsonProcessingException {
        return service.signup(requestDto);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<Response> authenticate(@RequestBody @Validated LoginRequestDto requestDto) throws Exception {
        return authenticationService.createAuthenticationToken(requestDto);
    }

    @PostMapping("/verify-phone")
    public ResponseEntity<Response> verifyPhone(@RequestBody @Validated PhoneVerificationDto requestDto) throws JsonProcessingException {
        return service.verifyPhone(requestDto);
    }

    @PostMapping("/create-magic-pin")
    public ResponseEntity<Response> createMagicPin(@RequestBody @Validated MagicPinRequestDto requestDto) {
        return service.createMagicPin(requestDto);
    }

    @PostMapping("/resend-phone-otp")
    public ResponseEntity<Response> resendPhoneOtpCode(@RequestBody @Validated ResendPhoneOtpRequest requestDto) throws JsonProcessingException {
        return service.resendPhoneOtpCode(requestDto);
    }

    @GetMapping("/get-countries")
    public ResponseEntity<?> getCountries() {
        return service.getCountries();
    }

    @PostMapping("/reset-password-request")
    public ResponseEntity<?> resetPasswordRequest(@RequestBody @Validated PasswordResetRequestDto passwordResetRequestDto) throws JsonProcessingException {
        return service.passwordResetRequest(passwordResetRequestDto);
    }

    @PostMapping("/verify-reset-password-otp")
    public ResponseEntity<Response> verifyResetPasswordOtp(@RequestBody @Validated EmailVerificationDto requestDto) throws JsonProcessingException {
        return service.verifyEmail(requestDto);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Response> resetPassword(@RequestBody @Validated ResetPasswordDto requestDto) throws JsonProcessingException {
        return service.resetPassword(requestDto);
    }

    @PostMapping("/create-akranex-tag")
    public ResponseEntity<Response> createAkranexTag(@RequestBody @Validated AkranexTagCreationRequestDto requestDto) throws JsonProcessingException {
        return service.createAkranexTag(requestDto);
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadToContainer(@RequestParam(value = "file", required = true) MultipartFile file, @RequestParam(value = "userId", required = true) Long userId)
            throws Exception {
        return service.uploadUserProfilePic(file, userId);

    }




}
