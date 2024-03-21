package com.feri.reservation.repository;

import com.feri.reservation.model.Reservation;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;


@ApplicationScoped
public class ReservationRepository implements ReactivePanacheMongoRepository<Reservation> {
    // Tu lahko dodaš dodatne metode za poizvedbe, če jih boš potreboval
}
