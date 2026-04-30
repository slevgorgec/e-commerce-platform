package com.n11bootcamp.ecommerce.notification.unit;

import com.n11bootcamp.ecommerce.common.event.user.UserRegisteredEvent;
import com.n11bootcamp.ecommerce.notification.application.port.in.NotificationPort;
import com.n11bootcamp.ecommerce.notification.infrastructure.messaging.consumer.UserEventConsumer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserEventConsumerTest {

    @Mock
    private NotificationPort notificationLogger;

    @InjectMocks
    private UserEventConsumer userEventConsumer;

    @Test
    void handleUserRegistered_givenValidEvent_delegatesToNotificationLogger() {
        var userId = UUID.randomUUID();
        var event = new UserRegisteredEvent(
                UUID.randomUUID().toString(), userId,
                "test@example.com", "Ahmet", "Yılmaz", Instant.now()
        );

        userEventConsumer.handleUserRegistered(event);

        verify(notificationLogger).notifyUserRegistered(userId, "test@example.com", "Ahmet");
    }
}
