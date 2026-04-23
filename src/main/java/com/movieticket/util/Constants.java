package com.movieticket.util;

public final class Constants {

    private Constants() {}

    // Roles
    public static final String ROLE_USER = "ROLE_USER";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";

    public static final long SEAT_LOCK_TIMEOUT_SECONDS = 300L;
    public static final String DEFAULT_CURRENCY = "INR";

    // ================= USER =================
    public static final String USER_REGISTERED = "User registered successfully";
    public static final String USER_LOGIN_SUCCESS = "User Login Success";
    public static final String USER_FETCHED = "User fetched successfully";
    public static final String USER_UPDATED = "User updated successfully";
    public static final String USER_DELETED = "User deleted successfully";

    // ================= MOVIE =================
    public static final String MOVIE_CREATED = "Movie created successfully";
    public static final String MOVIE_FETCHED = "Movie fetched successfully";
    public static final String MOVIE_UPDATED = "Movie updated successfully";
    public static final String MOVIE_DELETED = "Movie deleted successfully";

    // ================= THEATER =================
    public static final String THEATER_CREATED = "Theater created successfully";
    public static final String THEATER_FETCHED = "Theater fetched successfully";
    public static final String THEATER_UPDATED = "Theater updated successfully";
    public static final String THEATER_DELETED = "Theater deleted successfully";

    // ================= SHOW =================
    public static final String SHOW_CREATED = "Show created successfully";
    public static final String SHOW_FETCHED = "Show fetched successfully";
    public static final String SHOW_UPDATED = "Show updated successfully";
    public static final String SHOW_DELETED = "Show deleted successfully";

    // ================= SEAT =================
    public static final String SEAT_RESERVED = "Seats reserved successfully";
    public static final String SEAT_CONFIRMED = "Seats confirmed successfully";
    public static final String SEAT_RELEASED = "Seats released successfully";

    // ================= BOOKING =================
    public static final String BOOKING_CREATED = "Booking created successfully";
    public static final String BOOKING_FETCHED = "Booking fetched successfully";
    public static final String BOOKINGS_FETCHED = "Bookings fetched successfully";
    public static final String MY_BOOKINGS_FETCHED = "My bookings fetched successfully";
    public static final String BOOKING_CANCELLED = "Booking cancelled successfully";
    public static final String BOOKING_PAYMENT_CONFIRMED = "Payment confirmed successfully";

    // ================= PAYMENT =================
    public static final String PAYMENT_INITIATED = "Payment initiated successfully";
    public static final String PAYMENT_VERIFIED = "Payment verified successfully";
    public static final String PAYMENT_FETCHED = "Payment fetched successfully";

    // ================= NOTIFICATION =================
    public static final String NOTIFICATION_SENT = "Notification sent successfully";
    public static final String NOTIFICATIONS_FETCHED = "Notifications fetched successfully";
    public static final String NOTIFICATION_MARKED_READ = "Notification marked as read";
	public static final String SEATS_RELEASED = "Seats Released";
	//public static final String SEATS_CONFIRMED = null;
	public static final String SEAT_FETCHED = "Seat Fetched";
	public static final String ALL_BOOKINGS_FETCHED = "Bookings fetched successfully";
}

