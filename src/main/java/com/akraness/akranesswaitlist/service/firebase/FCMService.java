package com.akraness.akranesswaitlist.service.firebase;


import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import com.akraness.akranesswaitlist.dto.PushNotificationRequest;
import com.akraness.akranesswaitlist.entity.UserFCMToken;
import com.akraness.akranesswaitlist.repository.UserFCMTokenRepository;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class FCMService {
    private final Logger logger;
    private final FirebaseMessaging firebaseMessaging;
    private final UserFCMTokenRepository fcmTokenRepository;

    public void sendMessage(Map<String, String> data, PushNotificationRequest request, Long userId)
            throws InterruptedException, ExecutionException {
        List<String> tokens = getFCMTokens(userId);
        for (List<String> batch : splitIntoBatches(tokens, 500)) {
            List<CompletableFuture<String>> futures = new ArrayList<>();
            for (String token : batch) {
                Message message = getPreconfiguredMessageWithData(data, request);
                futures.add(sendAsync(message, token));
            }
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get();
        }
    }

    public void sendMessageWithoutData(PushNotificationRequest request, Long userId)
            throws InterruptedException, ExecutionException {
        List<String> tokens = getFCMTokens(userId);
        for (List<String> batch : splitIntoBatches(tokens, 500)) {
            List<CompletableFuture<String>> futures = new ArrayList<>();
            for (String token : batch) {
                Message message = getPreconfiguredMessageWithoutData(request);
                futures.add(sendAsync(message, token));
            }
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get();
        }
    }
    public void sendPushNotificationMessage(PushNotificationRequest request, Long userId)
            throws InterruptedException, ExecutionException {
        List<String> tokens = getFCMTokens(userId);
        for (List<String> batch : splitIntoBatches(tokens, 500)) {
            List<CompletableFuture<String>> futures = new ArrayList<>();
            for (String token : batch) {
                Message message = getPreconfiguredMessageToToken(request);
                futures.add(sendAsync(message, token));
            }
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get();
        }
    }

    private CompletableFuture<String> sendAsync(Message message, String target) {
        CompletableFuture<String> future = new CompletableFuture<>();
        firebaseMessaging.sendAsync(message, true).addListener(() -> {
            try {
                String result = firebaseMessaging.toString();
                logger.info("Sent message. Target: " + target + ", Response: " + result);
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

    private Message getPreconfiguredMessageToToken(PushNotificationRequest request) {
        return getPreconfiguredMessageBuilder(request)
                .setToken(request.getToken())
                .build();
    }

    private Message getPreconfiguredMessageWithoutData(PushNotificationRequest request) {
        return getPreconfiguredMessageBuilder(request)
                .setTopic(request.getTopic())
                .build();
    }

    private Message getPreconfiguredMessageWithData(Map<String, String> data, PushNotificationRequest request) {
        return getPreconfiguredMessageBuilder(request)
                .putAllData(data)
                .setToken(request.getToken())
                .build();
    }

    private Message.Builder getPreconfiguredMessageBuilder(PushNotificationRequest request) {
        AndroidConfig androidConfig = getAndroidConfig(request.getTopic());
        ApnsConfig apnsConfig = getApnsConfig(request.getTopic());
        return Message.builder()
                .setApnsConfig(apnsConfig)
                .setAndroidConfig(androidConfig)
                .setNotification(new Notification(request.getTitle(), request.getMessage()));
    }

    private List<String> getFCMTokens(Long userId) {
        List<UserFCMToken> userTokens = fcmTokenRepository.findByUserId(userId);
        return userTokens.stream().map(UserFCMToken::getFcmToken).collect(Collectors.toList());
    }

    private List<List<String>> splitIntoBatches(List<String> list, int batchSize) {
        List<List<String>> batches = new ArrayList<>();
        for (int i = 0; i < list.size(); i += batchSize) {
            batches.add(list.subList(i, Math.min(i + batchSize, list.size())));
        }
        return batches;
    }

}


