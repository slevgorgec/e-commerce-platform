package com.n11bootcamp.ecommerce.notification.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "ecommerce.events";
    public static final String DLX = "ecommerce.events.dlx";

    public static final String QUEUE_USER_REGISTERED    = "notification.user-registered";
    public static final String QUEUE_ORDER_CREATED      = "notification.order-created";
    public static final String QUEUE_ORDER_CONFIRMED    = "notification.order-confirmed";
    public static final String QUEUE_ORDER_CANCELLED    = "notification.order-cancelled";
    public static final String QUEUE_PAYMENT_COMPLETED  = "notification.payment-completed";
    public static final String QUEUE_PAYMENT_FAILED     = "notification.payment-failed";

    private static final int MESSAGE_TTL_MS = 86_400_000;

    @Bean
    public TopicExchange ecommerceExchange() {
        return ExchangeBuilder.topicExchange(EXCHANGE).durable(true).build();
    }

    @Bean
    public TopicExchange deadLetterExchange() {
        return ExchangeBuilder.topicExchange(DLX).durable(true).build();
    }

    @Bean
    public Queue userRegisteredQueue() {
        return buildQueue(QUEUE_USER_REGISTERED);
    }

    @Bean
    public Queue orderCreatedQueue() {
        return buildQueue(QUEUE_ORDER_CREATED);
    }

    @Bean
    public Queue orderConfirmedQueue() {
        return buildQueue(QUEUE_ORDER_CONFIRMED);
    }

    @Bean
    public Queue orderCancelledQueue() {
        return buildQueue(QUEUE_ORDER_CANCELLED);
    }

    @Bean
    public Queue paymentCompletedQueue() {
        return buildQueue(QUEUE_PAYMENT_COMPLETED);
    }

    @Bean
    public Queue paymentFailedQueue() {
        return buildQueue(QUEUE_PAYMENT_FAILED);
    }

    @Bean
    public Binding userRegisteredBinding() {
        return BindingBuilder.bind(userRegisteredQueue()).to(ecommerceExchange()).with("user.registered");
    }

    @Bean
    public Binding orderCreatedBinding() {
        return BindingBuilder.bind(orderCreatedQueue()).to(ecommerceExchange()).with("order.created");
    }

    @Bean
    public Binding orderConfirmedBinding() {
        return BindingBuilder.bind(orderConfirmedQueue()).to(ecommerceExchange()).with("order.confirmed");
    }

    @Bean
    public Binding orderCancelledBinding() {
        return BindingBuilder.bind(orderCancelledQueue()).to(ecommerceExchange()).with("order.cancelled");
    }

    @Bean
    public Binding paymentCompletedBinding() {
        return BindingBuilder.bind(paymentCompletedQueue()).to(ecommerceExchange()).with("payment.completed");
    }

    @Bean
    public Binding paymentFailedBinding() {
        return BindingBuilder.bind(paymentFailedQueue()).to(ecommerceExchange()).with("payment.failed");
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        var mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return new Jackson2JsonMessageConverter(mapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         Jackson2JsonMessageConverter messageConverter) {
        var template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }

    private Queue buildQueue(String name) {
        return QueueBuilder.durable(name)
                .withArgument("x-dead-letter-exchange", DLX)
                .withArgument("x-message-ttl", MESSAGE_TTL_MS)
                .build();
    }
}
