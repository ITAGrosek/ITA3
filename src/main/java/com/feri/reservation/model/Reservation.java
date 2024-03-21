package com.feri.reservation.model;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Reservation extends PanacheMongoEntity {
    public String userId;
    public String bookId;
    public LocalDateTime reservationDate;
    public LocalDateTime expectedReturnDate;
    public ReservationStatus status;

    // Enum za status rezervacije
    public enum ReservationStatus {
        ACTIVE, COMPLETED, CANCELLED
    }

    // Konstruktor brez argumentov za Panache in prazen objekt
    public Reservation() {
        // Prazen konstruktor je potreben za Panache in Jackson
    }

    // Konstruktor z argumenti za lažje ustvarjanje instanc
    public Reservation(String userId, String bookId, LocalDateTime reservationDate) {
        this.userId = userId;
        this.bookId = bookId;
        this.reservationDate = reservationDate;
        this.expectedReturnDate = reservationDate.plus(2, ChronoUnit.WEEKS); // Samodejno nastavi expectedReturnDate na dva tedna po datumu rezervacije
        this.status = ReservationStatus.ACTIVE; // Privzeti status ob ustvarjanju rezervacije
    }

    // Getterji in setterji
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

    public void setReservationDate(LocalDateTime reservationDate) {
        this.reservationDate = reservationDate;
    }

    public LocalDateTime getExpectedReturnDate() {
        return expectedReturnDate;
    }

    // To setter metoda ni nujno potrebna, če expectedReturnDate vedno izračunamo avtomatsko
    public void setExpectedReturnDate(LocalDateTime expectedReturnDate) {
        this.expectedReturnDate = expectedReturnDate;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }
}
