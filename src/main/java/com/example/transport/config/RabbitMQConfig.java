package com.example.transport.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String BOOKING_QUEUE = "booking.queue";
    public static final String BOOKING_EXCHANGE = "booking.exchange";
    public static final String BOOKING_ROUTING_KEY = "booking.routingKey";

    public static final String VERIFICATION_QUEUE = "verification.queue";
    public static final String VERIFICATION_EXCHANGE = "verification.exchange";
    public static final String VERIFICATION_ROUTING_KEY = "verification.routingKey";

    public static final String SIGNUP_QUEUE = "signup.queue";
    public static final String SIGNUP_EXCHANGE = "signup.exchange";
    public static final String SIGNUP_ROUTING_KEY = "signup.routingKey";

    // =========================
    // BOOKING EMAIL
    // =========================

    @Bean
    public Queue bookingQueue() {
        return new Queue(BOOKING_QUEUE);
    }

    @Bean
    public DirectExchange bookingExchange() {
        return new DirectExchange(BOOKING_EXCHANGE);
    }

    @Bean
    public Binding bookingBinding() {
        return BindingBuilder
                .bind(bookingQueue())
                .to(bookingExchange())
                .with(BOOKING_ROUTING_KEY);
    }

    // =========================
    // VERIFICATION EMAIL
    // =========================

    @Bean
    public Queue verificationQueue() {
        return new Queue(VERIFICATION_QUEUE);
    }

    @Bean
    public TopicExchange verificationExchange() {
        return new TopicExchange(VERIFICATION_EXCHANGE);
    }

    @Bean
    public Binding verificationBinding() {
        return BindingBuilder
                .bind(verificationQueue())
                .to(verificationExchange())
                .with(VERIFICATION_ROUTING_KEY);
    }

    // =========================
    // SIGNUP EMAIL
    // =========================


    @Bean
    public Queue signupQueue() {
        return new Queue(SIGNUP_QUEUE);
    }

    @Bean
    public TopicExchange signupExchange() {
        return new TopicExchange(SIGNUP_EXCHANGE);
    }

    @Bean
    public Binding signupBinding() {
        return BindingBuilder
                .bind(signupQueue())
                .to(signupExchange())
                .with(SIGNUP_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {

        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.registerModule(new JavaTimeModule());

        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return new Jackson2JsonMessageConverter(objectMapper);
    }
}