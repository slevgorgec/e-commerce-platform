package com.n11bootcamp.ecommerce.order.infrastructure.config;

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

    public static final String QUEUE_STOCK_RESERVED = "order.stock-reserved";
    public static final String QUEUE_STOCK_RESERVATION_FAILED = "order.stock-reservation-failed";
    public static final String QUEUE_PAYMENT_COMPLETED = "order.payment-completed";
    public static final String QUEUE_PAYMENT_FAILED = "order.payment-failed";

    private static final int MESSAGE_TTL_MS = 86400000;

    @Bean
    public TopicExchange ecommerceExchange() {
        return ExchangeBuilder.topicExchange(EXCHANGE).durable(true).build();
    }

    @Bean
    public TopicExchange deadLetterExchange() {
        return ExchangeBuilder.topicExchange(DLX).durable(true).build();
    }

    @Bean
    public Queue stockReservedQueue() {
        return QueueBuilder.durable(QUEUE_STOCK_RESERVED)
                .withArgument("x-dead-letter-exchange", DLX)
                .withArgument("x-message-ttl", MESSAGE_TTL_MS)
                .build();
    }

    @Bean
    public Queue stockReservationFailedQueue() {
        return QueueBuilder.durable(QUEUE_STOCK_RESERVATION_FAILED)
                .withArgument("x-dead-letter-exchange", DLX)
                .withArgument("x-message-ttl", MESSAGE_TTL_MS)
                .build();
    }

    @Bean
    public Queue paymentCompletedQueue() {
        return QueueBuilder.durable(QUEUE_PAYMENT_COMPLETED)
                .withArgument("x-dead-letter-exchange", DLX)
                .withArgument("x-message-ttl", MESSAGE_TTL_MS)
                .build();
    }

    @Bean
    public Queue paymentFailedQueue() {
        return QueueBuilder.durable(QUEUE_PAYMENT_FAILED)
                .withArgument("x-dead-letter-exchange", DLX)
                .withArgument("x-message-ttl", MESSAGE_TTL_MS)
                .build();
    }

    @Bean
    public Binding stockReservedBinding(Queue stockReservedQueue, TopicExchange ecommerceExchange) {
        return BindingBuilder.bind(stockReservedQueue).to(ecommerceExchange).with("stock.reserved");
    }

    @Bean
    public Binding stockReservationFailedBinding(Queue stockReservationFailedQueue, TopicExchange ecommerceExchange) {
        return BindingBuilder.bind(stockReservationFailedQueue).to(ecommerceExchange).with("stock.reservation.failed");
    }

    @Bean
    public Binding paymentCompletedBinding(Queue paymentCompletedQueue, TopicExchange ecommerceExchange) {
        return BindingBuilder.bind(paymentCompletedQueue).to(ecommerceExchange).with("payment.completed");
    }

    @Bean
    public Binding paymentFailedBinding(Queue paymentFailedQueue, TopicExchange ecommerceExchange) {
        return BindingBuilder.bind(paymentFailedQueue).to(ecommerceExchange).with("payment.failed");
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