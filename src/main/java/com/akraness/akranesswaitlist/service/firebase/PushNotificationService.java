package com.akraness.akranesswaitlist.service.firebase;

import com.akraness.akranesswaitlist.dto.PushNotificationRequest;
import com.google.firebase.messaging.FirebaseMessagingException;

import java.util.concurrent.ExecutionException;

public interface PushNotificationService {

//    void sendMessage(Map<String, String> data, PushNotificationRequest request, Long userId) throws InterruptedException, ExecutionException, FirebaseMessagingException;
//    void sendMessageWithoutData(PushNotificationRequest request, Long userId) throws InterruptedException, ExecutionException, FirebaseMessagingException;
    String sendPushNotificationMessage(PushNotificationRequest request, String token) throws InterruptedException, ExecutionException, FirebaseMessagingException;

    void sendPushNotificationToUser(Long userId, PushNotificationRequest request) throws ExecutionException, InterruptedException, FirebaseMessagingException;
}
