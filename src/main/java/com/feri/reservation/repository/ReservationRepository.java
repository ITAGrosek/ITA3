package com.feri.reservation.repository;

import com.feri.reservation.model.Reservation;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;

/*
omogoča osnovne operacije z bazo podatkov za rezervacije,
kot so iskanje, dodajanje in brisanje, z uporabo reaktivnega pristopa za neblokirajoče klice.
 */
@ApplicationScoped
public class ReservationRepository implements ReactivePanacheMongoRepository<Reservation> {
    // Metode za poizvedbe po potrebi
}
