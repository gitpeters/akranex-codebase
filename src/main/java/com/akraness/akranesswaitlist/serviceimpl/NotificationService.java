package com.akraness.akranesswaitlist.serviceimpl;

import com.akraness.akranesswaitlist.dto.NotificationDto;
import com.akraness.akranesswaitlist.service.INotificationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class NotificationService implements INotificationService {
    @Value("${topic.name.producer}")
    private String topic;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void sendNotification(NotificationDto notificationDto) throws JsonProcessingException {
        log.info("Payload received for recipient {}  and type is {} ",notificationDto.getRecipient(), notificationDto.getType());
        kafkaTemplate.send(topic, new ObjectMapper().writeValueAsString(notificationDto));
        log.info("payload sent to queue for recipient {} ",notificationDto.getRecipient());
    }
}
