package com.n11bootcamp.ecommerce.user.application.port.out;

import com.n11bootcamp.ecommerce.user.domain.model.User;

public interface EventPublisherPort {
    void publishUserRegistered(User user);
}
