package com.example.transport.rabbitmq;

import com.example.transport.config.RabbitMQConfig;
import com.example.transport.dto.events.BookingCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookingEventProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendBookingCreatedEvent(BookingCreatedEvent event) {

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.BOOKING_EXCHANGE,
                RabbitMQConfig.BOOKING_ROUTING_KEY,
                event
        );

        System.out.println("BOOKING EVENT SENT");
    }
}
