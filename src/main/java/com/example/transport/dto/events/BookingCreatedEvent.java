package com.example.transport.dto.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingCreatedEvent {

    private String email;

    private String customerName;

    private String departureLocation;

    private String destinationLocation;

    private LocalDateTime departureTime;

    private Integer seats;

    private BigDecimal price;

    private BigDecimal totalPrice;
}