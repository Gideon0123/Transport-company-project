package com.example.transport.dto;

import com.example.transport.enums.BookingStatus;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
public class BookingResponseDTO {

    private Long bookingId;
    private String customerEmail;
    private Long tripId;
    private String departureLocation;
    private String destinationLocation;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime departureDateTime;
    private Integer numberOfSeats;
    private BigDecimal pricePerSeat;
    private BigDecimal totalPrice;
    private BookingStatus status;

    public BookingResponseDTO(
            Long bookingId,
            String customerEmail,
            Long tripId,
            String departureLocation,
            String destinationLocation,
            LocalDateTime departureDateTime,
            Integer numberOfSeats,
            BigDecimal pricePerSeat,
            BigDecimal totalPrice,
            BookingStatus status
    ) {
        this.bookingId = bookingId;
        this.customerEmail = customerEmail;
        this.tripId = tripId;
        this.departureLocation = departureLocation;
        this.destinationLocation = destinationLocation;
        this.departureDateTime = departureDateTime;
        this.numberOfSeats = numberOfSeats;
        this.pricePerSeat = pricePerSeat;
        this.totalPrice = totalPrice;
        this.status = status;
    }

}