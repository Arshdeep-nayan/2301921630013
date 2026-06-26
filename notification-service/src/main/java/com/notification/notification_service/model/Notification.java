package com.notification.notification_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Notification {

    private Long id;
    private String type;
    private String message;
    private boolean read;
    private Long timestamp;
}