package com.notification.notification_service.service;

import com.notification.notification_service.model.Notification;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    public List<Notification> getTopNotifications(
            List<Notification> notifications) {

        return notifications.stream()
                .filter(notification ->
                        !notification.isRead())
                .sorted(
                        Comparator
                                .comparingInt(
                                        (Notification n) ->
                                                getPriority(
                                                        n.getType()
                                                )
                                )
                                .reversed()
                                .thenComparing(
                                        Notification::getTimestamp,
                                        Comparator.reverseOrder()
                                )
                )
                .limit(10)
                .collect(Collectors.toList());
    }

    private int getPriority(String type) {

        switch (type.toUpperCase()) {

            case "PLACEMENT":
                return 3;

            case "RESULT":
                return 2;

            case "EVENT":
                return 1;

            default:
                return 0;
        }
    }
}