package com.example.transport.service;

import com.example.transport.model.CustomerTrip;
import com.example.transport.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    @Async
    @Retryable(
            retryFor = { MailException.class, RuntimeException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    public void sendVerificationEmail(String to, String code) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Verification Code");
        message.setText("Your verification code is: " + code);

        mailSender.send(message);
    }
    @Recover
    public void recover(Exception e, String to, String code) {
        System.err.println("GIVING UP! Failed to send email to " + to + " after 3 retries.");
        System.err.println("The verification code is " + code);
    }

    @Override
    @Async
    @Retryable(
            retryFor = { MailException.class, RuntimeException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000, multiplier = 2)
    )
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
    @Recover
    public void recover(Exception e, CustomerTrip booking) {
        System.err.println("GIVING UP! Failed to send email to " + booking.getCustomer().getEmail() + " after 3 retries.");
        System.err.println("""
                The message is for the customer who just booked a trip
                """);
    }

    @Override
    @Async
    @Retryable(
            retryFor = { MailException.class, RuntimeException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000, multiplier = 2)
    )
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
    @Recover
    public void recover(Exception e, User user) {
        System.err.println("GIVING UP! Failed to send email to " + user.getEmail() + " after 3 retries.");
        System.err.println("""
                The message is for the customer who just signed up on the app
                """);
    }

}