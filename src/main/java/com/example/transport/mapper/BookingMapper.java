package com.example.transport.mapper;

import com.example.transport.dto.BookingResponseDTO;
import com.example.transport.dto.TripSimpleDTO;
import com.example.transport.dto.UserSimpleDTO;
import com.example.transport.model.CustomerTrip;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookingMapper {

    public static BookingResponseDTO toDTO(CustomerTrip booking) {
        if (booking == null) return null;

        BookingResponseDTO dto = new BookingResponseDTO();

        dto.setBookingId(booking.getBookingId());
        dto.setNumberOfSeats(booking.getNumberOfSeats());
        dto.setTotalPrice(booking.getTotalPrice());
        dto.setStatus(booking.getStatus());

        if (booking.getCustomer() != null){
            UserSimpleDTO userDTO = new UserSimpleDTO();
            userDTO.setFirstName(booking.getCustomer().getFirstName());
            userDTO.setLastName(booking.getCustomer().getLastName());
            userDTO.setEmail(booking.getCustomer().getEmail());
            userDTO.setPhoneNo(booking.getCustomer().getPhoneNo());

            dto.setCustomer(userDTO);
        }

        if (booking.getTrip() != null){
            TripSimpleDTO tripDTO = new TripSimpleDTO();
            tripDTO.setBookingDate(booking.getTrip().getBookingDate());
            tripDTO.setDepartureDateTime(booking.getTrip().getDepartureDateTime());
            tripDTO.setDepartureLocation(booking.getTrip().getDepartureLocation());
            tripDTO.setDestinationLocation(booking.getTrip().getDestinationLocation());
            tripDTO.setPrice(booking.getTrip().getPrice());

            dto.setTrip(tripDTO);
        }

        return dto;
    }
}
