package com.akraness.akranesswaitlist.controller;

import com.akraness.akranesswaitlist.dto.PushNotificationRequest;
import com.akraness.akranesswaitlist.service.firebase.FCMService;
import com.google.firebase.messaging.FirebaseMessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/v1/push-notification")
@RequiredArgsConstructor
public class PushNotificationController {
    private final FCMService fcmService;

    @PostMapping("/test")
    public void testPushNotification(@RequestBody PushNotificationRequest request, @RequestParam Long userId) throws ExecutionException, InterruptedException, FirebaseMessagingException {
        fcmService.sendPushNotificationMessage(request, userId);
    }
}
