package com.notification.notification_service.controller;

import com.notification.notification_service.dto.NotificationResponse;
import com.notification.notification_service.model.Notification;
import com.notification.notification_service.service.NotificationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(
            NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/top")
    public NotificationResponse getTopNotifications(
            @RequestBody List<Notification> notifications) {

        List<Notification> result =
                notificationService
                        .getTopNotifications(
                                notifications
                        );

        return new NotificationResponse(result);
    }
}