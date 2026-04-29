package com.n11bootcamp.ecommerce.user.infrastructure.messaging.publisher;

import com.n11bootcamp.ecommerce.common.event.EventRoutingKeys;
import com.n11bootcamp.ecommerce.common.event.user.UserRegisteredEvent;
import com.n11bootcamp.ecommerce.user.application.port.out.EventPublisherPort;
import com.n11bootcamp.ecommerce.user.domain.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventPublisher implements EventPublisherPort {

    private static final String EXCHANGE = "ecommerce.events";

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publishUserRegistered(User user) {
        var event = UserRegisteredEvent.of(user.id(), user.email(), user.firstName(), user.lastName());
        rabbitTemplate.convertAndSend(EXCHANGE, EventRoutingKeys.USER_REGISTERED, event);
        log.info("UserRegisteredEvent yayınlandı: userId={}", user.id());
    }
}
