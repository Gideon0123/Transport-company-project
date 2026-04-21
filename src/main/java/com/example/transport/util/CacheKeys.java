package com.example.transport.util;

public class CacheKeys {

    public static final String VEHICLE = "vehicles";
    public static final String VEHICLE_ALL = "'all'";

    public static final String TRIP = "trips";
    public static final String TRIP_ALL = "'all'";

    public static final String BOOKING = "bookings";
    public static final String BOOKING_ALL = "'all'";

    public static final String USER = "users";
    public static final String USER_ALL = "'all'";

    public static final String STAFF = "staffs";
    public static final String STAFF_ALL = "'all'";

    public static String vehicleById(Long id) {
        return String.valueOf(id);
    }

    public static String tripById(Long id) {
        return String.valueOf(id);
    }

    public static String bookingById(Long id) {
        return String.valueOf(id);
    }

    public static String userById(Long id) {
        return String.valueOf(id);
    }

    public static String staffById(Long id) {
        return String.valueOf(id);
    }
}