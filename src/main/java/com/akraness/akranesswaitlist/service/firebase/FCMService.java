package com.akraness.akranesswaitlist.service.firebase;

import com.akraness.akranesswaitlist.dto.PushNotificationRequest;
import com.google.firebase.messaging.FirebaseMessagingException;

import java.util.Map;
import java.util.concurrent.ExecutionException;

public interface FCMService {
    void sendMessage(Map<String, String> data, PushNotificationRequest request, Long userId) throws InterruptedException, ExecutionException, FirebaseMessagingException;
    void sendMessageWithoutData(PushNotificationRequest request, Long userId) throws InterruptedException, ExecutionException, FirebaseMessagingException;
    void sendPushNotificationMessage(PushNotificationRequest request, Long userId) throws InterruptedException, ExecutionException, FirebaseMessagingException;
}
