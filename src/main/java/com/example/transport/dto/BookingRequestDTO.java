package com.example.transport.dto;

import lombok.Data;

@Data
public class BookingRequestDTO {

    private Long tripId;
    private Integer numberOfSeats;
}
