package com.n11bootcamp.ecommerce.payment.infrastructure.config;

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

    public static final String QUEUE_PAYMENT_REQUESTED = "payment.payment-requested";

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
    public Queue paymentRequestedQueue() {
        return QueueBuilder.durable(QUEUE_PAYMENT_REQUESTED)
                .withArgument("x-dead-letter-exchange", DLX)
                .withArgument("x-message-ttl", MESSAGE_TTL_MS)
                .build();
    }

    @Bean
    public Binding paymentRequestedBinding() {
        return BindingBuilder.bind(paymentRequestedQueue()).to(ecommerceExchange()).with("payment.requested");
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
