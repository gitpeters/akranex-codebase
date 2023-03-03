package com.akraness.akranesswaitlist.service;

import com.akraness.akranesswaitlist.dto.NotificationDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.scheduling.annotation.Async;

public interface INotificationService {
    @Async
    void sendNotification(NotificationDto notificationDto) throws JsonProcessingException;
}
