package com.movieticket.entity;

public enum BookingStatus {
    PENDING,    // created, awaiting payment
    CONFIRMED,  // payment successful
    CANCELLED,  // cancelled by user/admin
    EXPIRED , // reservation expired (not paid)
    COMPLETED
}