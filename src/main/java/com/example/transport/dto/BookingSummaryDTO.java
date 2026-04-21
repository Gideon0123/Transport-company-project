package com.example.transport.dto;

import com.example.transport.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingSummaryDTO {

    private Long bookingId;
    private String customerEmail;
    private Long tripId;
    private String departureLocation;
    private String destinationLocation;
    private LocalDateTime departureDateTime;
    private Integer numberOfSeats;
    private BigDecimal pricePerSeat;
    private BigDecimal totalPrice;
    private BookingStatus status;
}
