package com.example.transport.rabbitmq;

import com.example.transport.config.RabbitMQConfig;
import com.example.transport.dto.events.VerificationEmailEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VerificationEmailProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendVerificationEmail(VerificationEmailEvent event) {

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.VERIFICATION_EXCHANGE,
                RabbitMQConfig.VERIFICATION_ROUTING_KEY,
                event
        );
        System.out.println("EMAIL WITH VERIFICATION CODE SENT");
    }
}
