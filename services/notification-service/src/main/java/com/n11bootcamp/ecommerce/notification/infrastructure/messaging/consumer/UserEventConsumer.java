package com.n11bootcamp.ecommerce.notification.infrastructure.messaging.consumer;

import com.n11bootcamp.ecommerce.common.event.user.UserRegisteredEvent;
import com.n11bootcamp.ecommerce.notification.application.port.in.NotificationPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventConsumer {

    private final NotificationPort notificationLogger;

    @RabbitListener(queues = "notification.user-registered")
    public void handleUserRegistered(UserRegisteredEvent event) {
        log.info("UserRegisteredEvent alındı: userId={}, eventId={}", event.userId(), event.eventId());
        notificationLogger.notifyUserRegistered(event.userId(), event.email(), event.firstName());
    }
}
