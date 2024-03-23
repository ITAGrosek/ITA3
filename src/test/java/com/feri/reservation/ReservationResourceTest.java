package com.feri.reservation;

import com.feri.reservation.model.Reservation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.time.LocalDateTime;

public class ReservationResourceTest {

    @Test
    public void testReservationSettersAndGetters() {
        String expectedUserId = "user123";
        String expectedBookId = "book123";
        LocalDateTime expectedReservationDate = LocalDateTime.now();
        LocalDateTime expectedReturnDate = expectedReservationDate.plusWeeks(2);

        Reservation reservation = new Reservation();
        reservation.setUserId(expectedUserId);
        reservation.setBookId(expectedBookId);
        reservation.setReservationDate(expectedReservationDate);
        reservation.setExpectedReturnDate(expectedReturnDate);

        Assertions.assertEquals(expectedUserId, reservation.getUserId(), "The user ID should match");
        Assertions.assertEquals(expectedBookId, reservation.getBookId(), "The book ID should match");
        Assertions.assertEquals(expectedReservationDate, reservation.getReservationDate(), "The reservation date should match");
        Assertions.assertEquals(expectedReturnDate, reservation.getExpectedReturnDate(), "The expected return date should match");
    }
}
