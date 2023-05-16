package com.akraness.akranesswaitlist.service.firebase;


import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.akraness.akranesswaitlist.dto.PushNotificationRequest;
import com.akraness.akranesswaitlist.entity.UserFCMToken;
import com.akraness.akranesswaitlist.repository.UserFCMTokenRepository;
import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class FCMServiceImpl implements PushNotificationService {
    private final FirebaseMessaging firebaseMessaging;
    private final UserFCMTokenRepository userFCMTokenRepository;
    @Override
    public String sendPushNotificationMessage(PushNotificationRequest request, String token) throws InterruptedException, ExecutionException, FirebaseMessagingException {
       Notification notification = Notification
               .builder()
               .setTitle(request.getSubject())
               .setBody(request.getMessage())
               .build();

        Message message = Message.builder()
                .setToken(token)
                .setNotification(notification)
                .setAndroidConfig(AndroidConfig.builder()
                        .setNotification(AndroidNotification.builder()
                                .setSound("default")
                                .setColor("#FFFFFF")
                                .build())
                        .build())
                .setApnsConfig(ApnsConfig.builder()
                        .setAps(Aps.builder()
                                .setSound("default")
                                .build())
                        .build())
                .build();
        try {
            // Send the push notification
            return firebaseMessaging.send(message);
        } catch (FirebaseMessagingException e) {
            // Handle any exceptions
            log.error("Failed to send push notification: {}", e.getMessage());
            return e.getMessage();
        }
    }

    @Override
    public void sendPushNotificationToUser(Long userId, PushNotificationRequest request) throws ExecutionException, InterruptedException, FirebaseMessagingException {
        List<UserFCMToken> userTokens = userFCMTokenRepository.findByUserId(userId);
        if(userTokens.isEmpty()){
            System.out.println("No fcm token found");
        }

        for (UserFCMToken userToken : userTokens) {
            String fcmToken = userToken.getFcmToken();

            // Create and send the push notification using the FCM token
            sendPushNotificationMessage(request, fcmToken);
        }
    }
    @Scheduled(fixedDelay = 3600000) // execute every hour
    public void removeExpiredTokens() {
        List<UserFCMToken> allTokens = userFCMTokenRepository.findAll();

        if(allTokens.isEmpty()){
            log.warn("No token record found");
        }

        LocalDateTime currentDateTime = LocalDateTime.now();
        Duration expirationThreshold = Duration.ofDays(30);
        for (UserFCMToken token : allTokens) {
            LocalDateTime tokenCreationDate = token.getCreatedOn().toLocalDateTime();
            LocalDateTime expirationDate = tokenCreationDate.plus(expirationThreshold);

            if (currentDateTime.isAfter(expirationDate)) {
                userFCMTokenRepository.delete(token);
            }
        }
    }


}


