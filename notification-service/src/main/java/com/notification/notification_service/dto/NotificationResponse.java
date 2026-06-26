package com.notification.notification_service.dto;

import com.notification.notification_service.model.Notification;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResponse {

    private List<Notification> notifications;
}