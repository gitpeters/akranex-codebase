package com.akraness.akranesswaitlist.dto;

import com.akraness.akranesswaitlist.enums.NotificationType;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.springframework.lang.Nullable;

import java.util.Map;

@Data
@Builder
@ToString
public class NotificationDto {
    private String sender;
    private String subject;
    private String message;
    private String recipient;
    private NotificationType type;
    @Nullable
    private String attachment;
    private String templateId;
    private Map<String, String> substitutions;
}
