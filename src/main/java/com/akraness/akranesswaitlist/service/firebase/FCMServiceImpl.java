package com.akraness.akranesswaitlist.service.firebase;


import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import com.akraness.akranesswaitlist.config.CustomResponse;
import com.akraness.akranesswaitlist.dto.PushNotificationRequest;
import com.akraness.akranesswaitlist.entity.UserFCMToken;
import com.akraness.akranesswaitlist.exception.UserNotFoundException;
import com.akraness.akranesswaitlist.repository.UserFCMTokenRepository;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class FCMServiceImpl implements FCMService{
    private final FirebaseMessaging firebaseMessaging;
    private final UserFCMTokenRepository fcmTokenRepository;

    @Override
    public void sendMessage(Map<String, String> data, PushNotificationRequest request, Long userId)
            throws InterruptedException, ExecutionException, FirebaseMessagingException {
        List<String> tokens = getFCMTokens(userId);
        for (List<String> batch : splitIntoBatches(tokens, 500)) {
            List<CompletableFuture<String>> futures = new ArrayList<>();
            for (String token : batch) {
                Message message = getPreconfiguredMessageWithData(data, request, token);
                futures.add(sendAsync(message, token));
            }
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get();
        }
    }
    @Override
    public void sendMessageWithoutData(PushNotificationRequest request, Long userId)
            throws InterruptedException, ExecutionException, FirebaseMessagingException {
        List<String> tokens = getFCMTokens(userId);
        for (List<String> batch : splitIntoBatches(tokens, 500)) {
            List<CompletableFuture<String>> futures = new ArrayList<>();
            for (String token : batch) {
                Message message = getPreconfiguredMessageWithoutData(request,token);
                futures.add(sendAsync(message, token));
            }
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get();
        }
    }
    @Override
    public void sendPushNotificationMessage(PushNotificationRequest request, Long userId)
            throws InterruptedException, ExecutionException, FirebaseMessagingException {
        List<String> tokens = getFCMTokens(userId);
        for (List<String> batch : splitIntoBatches(tokens, 500)) {
            List<CompletableFuture<String>> futures = new ArrayList<>();
            for (String token : batch) {
                Message message = getPreconfiguredMessageToToken(request, token);
                CompletableFuture<String> future = sendAsync(message, token);
                future.exceptionally(ex -> {
                    if (ex.getCause() instanceof FirebaseMessagingException) {
                        FirebaseMessagingException fex = (FirebaseMessagingException) ex.getCause();
                        if (fex.getErrorCode().equals(HttpStatus.NOT_FOUND)) {
                            log.error("Invalid FCM token: {}", token);
                            throw new UserNotFoundException("Invalid FCM token");
                        } else {
                            log.error("Error sending FCM message to token {}: {}", token, fex.getMessage());
                            throw new UserNotFoundException("Error sending FCM message to token::"+ token);
                        }
                    } else {
                        log.error("Error sending FCM message to token {}: {}", token, ex.getMessage());
                        throw new UserNotFoundException("Error sending FCM message to token::"+ token);
                    }
                });
                futures.add(future);
            }
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get();
        }
    }


    private CompletableFuture<String> sendAsync(Message message, String target) throws FirebaseMessagingException {
        CompletableFuture<String> future = new CompletableFuture<>();
        firebaseMessaging.sendAsync(message, true).addListener(() -> {
            try {
                String result = firebaseMessaging.toString();
                log.info("Sent message. Target: " + target + ", Response: " + result);
                future.complete(result);
            } catch (Throwable t) {
                future.completeExceptionally(t);
            }
        }, MoreExecutors.directExecutor());
        return future;
    }





    private AndroidConfig getAndroidConfig(String topic) {
        return AndroidConfig.builder()
                .setTtl(Duration.ofMinutes(2).toMillis())
                .setCollapseKey(topic)
                .setPriority(AndroidConfig.Priority.HIGH)
                .setNotification(AndroidNotification.builder()
                        .setSound(NotificationParameter.SOUND.getValue())
                        .setColor(NotificationParameter.COLOR.getValue())
                        .setTag(topic)
                        .build())
                .build();
    }

    private ApnsConfig getApnsConfig(String topic) {
        return ApnsConfig.builder()
                .setAps(Aps.builder()
                        .setCategory(topic)
                        .setThreadId(topic)
                        .build())
                .build();
    }

    private Message getPreconfiguredMessageToToken(PushNotificationRequest request, String token) {
        return getPreconfiguredMessageBuilder(request, token)
                .setToken(token)
                .build();
    }

    private Message getPreconfiguredMessageWithoutData(PushNotificationRequest request, String token) {
        return getPreconfiguredMessageBuilder(request, token)
                .setTopic(token)
                .build();
    }

    private Message getPreconfiguredMessageWithData(Map<String, String> data, PushNotificationRequest request, String token) {
        return getPreconfiguredMessageBuilder(request, token)
                .putAllData(data)
                .setToken(token)
                .build();
    }

    private Message.Builder getPreconfiguredMessageBuilder(PushNotificationRequest request, String token) {
        AndroidConfig androidConfig = getAndroidConfig(request.getTopic());
        ApnsConfig apnsConfig = getApnsConfig(request.getTopic());
        Notification notification = Notification.builder()
                .setTitle(request.getTitle())
                .setBody(request.getMessage())
                .build();
        return Message.builder()
                .setApnsConfig(apnsConfig)
                .setAndroidConfig(androidConfig)
                .setNotification(notification);
    }

    private List<String> getFCMTokens(Long userId) {
        List<UserFCMToken> users = fcmTokenRepository.findByUserId(userId);
        if(users.isEmpty()) {
            throw new UserNotFoundException("User id does not exist");
        }

        return users.stream().map(UserFCMToken::getFcmToken).collect(Collectors.toList());
    }

    private List<List<String>> splitIntoBatches(List<String> list, int batchSize) {
        List<List<String>> batches = new ArrayList<>();
        for (int i = 0; i < list.size(); i += batchSize) {
            batches.add(list.subList(i, Math.min(i + batchSize, list.size())));
        }
        return batches;
    }

}


