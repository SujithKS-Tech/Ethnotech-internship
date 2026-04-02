package com.appointment.service;



import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.appointment.entities.Notification;
import com.appointment.entities.User;
import com.appointment.enums.NotificationStatus;
import com.appointment.enums.NotificationType;
import com.appointment.repositories.NotificationRepository;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    public void sendNotification(User user, String message) {

        Notification notification = new Notification();

        notification.setUser(user);
        notification.setMessage(message);
        notification.setType(NotificationType.SYSTEM);
        notification.setStatus(NotificationStatus.SENT);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setSentAt(LocalDateTime.now());

        notificationRepository.save(notification);

        System.out.println("Notification sent to " + user.getEmail());
    }
}
