package com.movieticket.util;

import java.time.Instant;

public final class SeatLockUtil {

    private SeatLockUtil() {}

    public static Instant computeExpiry(Long seconds) {
        long timeout = (seconds == null || seconds <= 0)
                ? Constants.SEAT_LOCK_TIMEOUT_SECONDS
                : seconds;

        return Instant.now().plusSeconds(timeout);
    }

    public static boolean isExpired(Instant expiry) {
        return expiry == null || Instant.now().isAfter(expiry);
    }
}
