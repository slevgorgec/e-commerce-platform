package com.n11bootcamp.ecommerce.product.infrastructure.config;

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

    // Kuyruk isimleri
    public static final String QUEUE_ORDER_CREATED    = "product.order-created";
    public static final String QUEUE_ORDER_CANCELLED  = "product.order-cancelled";
    public static final String QUEUE_PAYMENT_COMPLETED = "product.payment-completed";
    public static final String QUEUE_PAYMENT_FAILED   = "product.payment-failed";

    @Bean
    public TopicExchange ecommerceExchange() {
        return ExchangeBuilder.topicExchange(EXCHANGE).durable(true).build();
    }

    @Bean
    public TopicExchange deadLetterExchange() {
        return ExchangeBuilder.topicExchange(DLX).durable(true).build();
    }

    @Bean
    public Queue orderCreatedQueue() {
        return QueueBuilder.durable(QUEUE_ORDER_CREATED)
                .withArgument("x-dead-letter-exchange", DLX)
                .withArgument("x-message-ttl", 86400000)
                .build();
    }

    @Bean
    public Queue orderCancelledQueue() {
        return QueueBuilder.durable(QUEUE_ORDER_CANCELLED)
                .withArgument("x-dead-letter-exchange", DLX)
                .withArgument("x-message-ttl", 86400000)
                .build();
    }

    @Bean
    public Queue paymentCompletedQueue() {
        return QueueBuilder.durable(QUEUE_PAYMENT_COMPLETED)
                .withArgument("x-dead-letter-exchange", DLX)
                .withArgument("x-message-ttl", 86400000)
                .build();
    }

    @Bean
    public Queue paymentFailedQueue() {
        return QueueBuilder.durable(QUEUE_PAYMENT_FAILED)
                .withArgument("x-dead-letter-exchange", DLX)
                .withArgument("x-message-ttl", 86400000)
                .build();
    }

    @Bean
    public Binding orderCreatedBinding() {
        return BindingBuilder.bind(orderCreatedQueue()).to(ecommerceExchange()).with("order.created");
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
}
