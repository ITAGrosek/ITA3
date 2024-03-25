package com.feri.reservation.model;

import io.quarkus.mongodb.panache.PanacheMongoEntity;

import java.time.LocalDateTime;

public class Reservation extends PanacheMongoEntity {
    public String userId;
    public String bookId;
    public LocalDateTime reservationDate;
    public LocalDateTime expectedReturnDate;

    public Reservation() {
        // Nastavimo reservationDate na trenutni čas in expectedReturnDate na dva tedna od tega
        this.reservationDate = LocalDateTime.now();
        this.expectedReturnDate = this.reservationDate.plusWeeks(2);
    }

    public Reservation(String userId, String bookId) {
        this.userId = userId;
        this.bookId = bookId;
        this.reservationDate = LocalDateTime.now();
        this.expectedReturnDate = this.reservationDate.plusWeeks(2);
    }

    // Getterji in setterji, izpustili smo setterje za datume, saj se nastavijo samodejno
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public LocalDateTime getReservationDate() {
        return reservationDate;
    }

    public LocalDateTime getExpectedReturnDate() {
        return expectedReturnDate;
    }
    public void setReservationDate(LocalDateTime reservationDate) {
        this.reservationDate = reservationDate;
    }

    public void setExpectedReturnDate(LocalDateTime expectedReturnDate) {
        this.expectedReturnDate = expectedReturnDate;
    }

}
