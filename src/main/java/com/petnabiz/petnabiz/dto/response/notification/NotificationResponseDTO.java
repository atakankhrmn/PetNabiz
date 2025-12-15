package com.petnabiz.petnabiz.dto.response.notification;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NotificationResponseDTO {
    private String notificationId;
    private String type;
    private String message;
    private LocalDateTime createdAt;
    private boolean read;
}
