package com.example.transport.service;

import com.example.transport.model.CustomerTrip;
import com.example.transport.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendVerificationEmail(String to, String code) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Verification Code");
        message.setText("Your verification code is: " + code);

        mailSender.send(message);
    }

    @Override
    public void sendCreatedBooking(CustomerTrip booking) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(booking.getCustomer().getEmail());
        mailMessage.setSubject("Booking Confirmed");
        mailMessage.setText("Your Booking Order Has Been Confirmed with the The following \n" +
                "\n" +
                "Dear " + booking.getCustomer().getFirstName() + " " + booking.getCustomer().getLastName() + " \n" +
                "Your Trip is from: " + booking.getTrip().getDepartureLocation() + " \n" +
                "To: " + booking.getTrip().getDestinationLocation() + " \n" +
                "The Departure time for this trip is: " + booking.getTrip().getDepartureDateTime() + " \n" +
                "Number of Seats Booked: " + booking.getNumberOfSeats() + " \n" +
                "The Price per Seat is: " + booking.getTrip().getPrice() + "\n" +
                "Total Price: " + booking.getTotalPrice() );

        mailSender.send(mailMessage);
    }

    @Override
    public void customerSignupMail(User user) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getUsername());
        message.setSubject("Welcome to Gideon Transport Company!");
        message.setText("Hurray! \n" +
                "Dear " + user.getFirstName() + " " + user.getLastName() + "\n" +
                "Welcome to Gideon Transport Company \n" +
                "\n" +
                "We Hope to Serve you well as you Journey Around the Country and Beyond");

        mailSender.send(message);
    }

}