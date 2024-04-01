package com.feri.reservation.rest;

import com.feri.reservation.model.Reservation;
import com.feri.reservation.repository.ReservationRepository;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jboss.logging.Logger;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Path("/reservations")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReservationResource {

    private static final Logger LOG = Logger.getLogger(ReservationResource.class);

    @Inject
    ReservationRepository reservationRepository;

    //anotacija označuje vmesnik za pošiljanje sporočil v RabbitMQ.
    @Inject
    @Channel("reservations")
    Emitter<String> reservationEmitter;

    /*
    Emitter<String> je objekt, ki omogoča pošiljanje sporočil v določen kanal (v tem primeru imenovan "reservations").
    Ko je ustvarjen Emitter, ga lahko uporabimo za pošiljanje sporočil v RabbitMQ, ki bodo potem na voljo
    za obdelavo drugim komponentam sistema, ki so naročene na ta kanal.
     */
    @POST


    /*
    Reaktivno programiranje omogoča asinhrono obdelavo dogodkov v realnem času, kar izboljša odzivnost, skalabilnost
    in robustnost aplikacij z visoko obremenitvijo. Namesto čakanja na zaključek vsake operacije se omogoča vzporedno
    izvajanje in odziv na dogodke takoj, ko se zgodijo.
     */
    public Uni<Response> createReservation(Reservation reservation) {
        // Formatter za lepši izpis datuma in časa
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedReservationDate = reservation.getReservationDate().format(formatter);
        String formattedExpectedReturnDate = reservation.getExpectedReturnDate().format(formatter);

        LOG.infof("Attempting to create a new reservation for bookId: %s and userId: %s", reservation.getBookId(), reservation.getUserId());
        return reservationRepository.persist(reservation)
                .onItem().transform(inserted -> {
                    // Sestavi sporočilo za RabbitMQ, ki vključuje lepo formatirane datume
                    String message = String.format(
                            "New reservation created for bookId: %s, userId: %s, reservationDate: %s, expectedReturnDate: %s",
                            reservation.getBookId(),
                            reservation.getUserId(),
                            formattedReservationDate,
                            formattedExpectedReturnDate);

                    reservationEmitter.send(message); // Pošlji sporočilo
                    LOG.info("Reservation created successfully and message sent to RabbitMQ with formatted dates.");
                    return Response.status(Response.Status.CREATED).entity(inserted).build();
                })
                .onFailure().invoke(e -> LOG.errorf("Error creating reservation", e))
                .onFailure().recoverWithItem(e -> Response.status(Response.Status.BAD_REQUEST).entity("Error creating reservation: " + e.getMessage()).build());
    }




    @GET
    public Uni<Response> getAllReservations() {
        LOG.info("Retrieving all reservations");
        return reservationRepository.listAll() // Reactive
                .onItem().transform(reservations -> {
                    LOG.info("Reservations retrieved successfully.");
                    return Response.ok(reservations).build();
                })
                .onFailure().invoke(e -> LOG.error("Error retrieving reservations", e))
                .onFailure().recoverWithItem(e -> Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error retrieving reservations: " + e.getMessage()).build());
    }

    @GET
    @Path("/{id}")
    public Uni<Response> getReservationById(@PathParam("id") String id) {
        LOG.infof("Retrieving reservation with id: %s", id);
        return reservationRepository.findById(new ObjectId(id)) // Reactive
                .onItem().ifNotNull().transform(reservation -> {
                    LOG.info("Reservation retrieved successfully.");
                    return Response.ok(reservation).build();
                })
                .onItem().ifNull().continueWith(Response.status(Response.Status.NOT_FOUND)::build)
                .onFailure().invoke(e -> LOG.errorf("Error retrieving reservation with id: %s", id, e))
                .onFailure().recoverWithItem(e -> Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error retrieving reservation: " + e.getMessage()).build());
    }

    @PUT
    @Path("/{id}")
    public Uni<Response> updateReservation(@PathParam("id") String id, Reservation update) {
        LOG.infof("Attempting to update reservation with id: %s", id);
        return reservationRepository.findById(new ObjectId(id)) // Reactive
                .onItem().ifNotNull().transformToUni(existing -> {
                    existing.setUserId(update.getUserId());
                    existing.setBookId(update.getBookId());
                    if (update.getReservationDate() != null) existing.setReservationDate(update.getReservationDate());
                    if (update.getExpectedReturnDate() != null) existing.setExpectedReturnDate(update.getExpectedReturnDate());
                    return reservationRepository.update(existing);
                })
                .onItem().ifNotNull().transform(updated -> {
                    LOG.info("Reservation updated successfully.");
                    return Response.ok(updated).build();
                })
                .onItem().ifNull().continueWith(Response.status(Response.Status.NOT_FOUND)::build)
                .onFailure().invoke(e -> LOG.errorf("Error updating reservation with id: %s", id, e))
                .onFailure().recoverWithItem(e -> Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error updating reservation: " + e.getMessage()).build());
    }

    @DELETE
    @Path("/{id}")
    public Uni<Response> deleteReservation(@PathParam("id") String id) {
        LOG.infof("Attempting to delete reservation with id: %s", id);
        return reservationRepository.deleteById(new ObjectId(id)) // Reactive
                .onItem().transform(deleted -> {
                    if (deleted) {
                        LOG.info("Reservation deleted successfully.");
                        return Response.noContent().entity("Reservation deleted successfully.").build();
                    } else {
                        return Response.status(Response.Status.NOT_FOUND).entity("Reservation not found.").build();
                    }
                })
                .onFailure().invoke(e -> LOG.errorf("Error deleting reservation with id: %s", id, e))
                .onFailure().recoverWithItem(e -> Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error deleting reservation: " + e.getMessage()).build());
    }

    @GET
    @Path("/user/{userId}")
    public Uni<List<Reservation>> getReservationsByUser(@PathParam("userId") String userId) {
        LOG.infof("Retrieving reservations for user: %s", userId);
        return reservationRepository.find("userId", userId).list() // Reactive
                .onItem().transform(reservations -> {
                    LOG.info("Reservations for user retrieved successfully.");
                    return reservations;
                })
                .onFailure().invoke(e -> LOG.errorf("Error retrieving reservations for user: %s", userId, e))
                .onFailure().recoverWithItem(e -> {
                    throw new WebApplicationException("Error retrieving reservations for user: " + e.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
                });
    }

    @GET
    @Path("/book/{bookId}")
    public Uni<List<Reservation>> getReservationsByBook(@PathParam("bookId") String bookId) {
        LOG.infof("Retrieving reservations for book: %s", bookId);
        return reservationRepository.find("bookId", bookId).list() // Reactive
                .onItem().transform(reservations -> {
                    LOG.info("Reservations for book retrieved successfully.");
                    return reservations;
                })
                .onFailure().invoke(e -> LOG.errorf("Error retrieving reservations for book: %s", bookId, e))
                .onFailure().recoverWithItem(e -> {
                    throw new WebApplicationException("Error retrieving reservations for book: " + e.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
                });
    }
}


/*
Uni omogoča asinhrono obdelavo rezultatov ali napak, ko postanejo na voljo, brez blokiranja izvajanja.
 */