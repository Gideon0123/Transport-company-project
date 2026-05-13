package com.example.transport.rabbitmq;

import com.example.transport.config.RabbitMQConfig;
import com.example.transport.dto.events.CustomerSignupEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerSignupProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendSignupEvent(CustomerSignupEvent event) {

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.SIGNUP_EXCHANGE,
                RabbitMQConfig.SIGNUP_ROUTING_KEY,
                event
        );

        System.out.println("CUSTOMER SIGNUP EVENT SENT");
    }
}