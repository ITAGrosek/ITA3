package com.feri.reservation.rest;

import com.feri.reservation.model.Reservation;
import com.feri.reservation.repository.ReservationRepository;
import io.smallrye.mutiny.Uni;
import org.jboss.logging.Logger;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.bson.types.ObjectId;

import java.util.List;

@Path("/reservations")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReservationResource {

    private static final Logger LOG = Logger.getLogger(ReservationResource.class.getName());

    @Inject
    ReservationRepository reservationRepository;

    @POST
    public Uni<Response> createReservation(Reservation reservation) {
        LOG.infov("Creating a new reservation for bookId: {0} and userId: {1}", reservation.getBookId(), reservation.getUserId());
        return reservationRepository.persist(reservation)
                .onItem().transform(inserted -> Response.ok(inserted).status(Response.Status.CREATED).build())
                .onFailure().invoke(e -> LOG.error("Error creating reservation", e))
                .onFailure().recoverWithItem(e -> Response.status(Response.Status.BAD_REQUEST).entity("Error creating reservation: " + e.getMessage()).build());
    }

    @GET
    public Uni<Response> getAllReservations() {
        LOG.info("Retrieving all reservations");
        return reservationRepository.listAll()
                .onItem().transform(reservations -> Response.ok(reservations).build())
                .onFailure().invoke(e -> LOG.error("Error retrieving reservations", e))
                .onFailure().recoverWithItem(e -> Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error retrieving reservations: " + e.getMessage()).build());
    }

    @GET
    @Path("/{id}")
    public Uni<Response> getReservationById(@PathParam("id") String id) {
        LOG.infov("Retrieving reservation with id: {0}", id);
        return reservationRepository.findById(new ObjectId(id))
                .onItem().ifNotNull().transform(reservation -> Response.ok(reservation).build())
                .onItem().ifNull().continueWith(Response.status(Response.Status.NOT_FOUND)::build)
                .onFailure().invoke(e -> LOG.errorv("Error retrieving reservation with id: {0}", id, e))
                .onFailure().recoverWithItem(e -> Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error retrieving reservation: " + e.getMessage()).build());
    }

    @DELETE
    @Path("/{id}")
    public Uni<Response> deleteReservation(@PathParam("id") String id) {
        LOG.infov("Deleting reservation with id: {0}", id);
        return reservationRepository.deleteById(new ObjectId(id))
                .onItem().transform(deleted -> deleted
                        ? Response.ok().status(Response.Status.NO_CONTENT).build()
                        : Response.status(Response.Status.NOT_FOUND).build())
                .onFailure().invoke(e -> LOG.errorv("Error deleting reservation with id: {0}", id, e))
                .onFailure().recoverWithItem(e -> Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error deleting reservation: " + e.getMessage()).build());
    }

    // Dodaj metode za UPDATE, SEARCH, itd., glede na tvoje zahteve, vključno z logiranjem in obdelavo napak

    @GET
    @Path("/user/{userId}")
    public Uni<List<Reservation>> getReservationsByUser(@PathParam("userId") String userId) {
        LOG.infov("Retrieving reservations for user: {0}", userId);
        return reservationRepository.find("userId", userId).list()
                .onFailure().invoke(e -> LOG.errorv("Error retrieving reservations for user: {0}", userId, e))
                .onFailure().recoverWithItem(e -> {
                    throw new WebApplicationException("Error retrieving reservations for user: " + e.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
                });
    }

    @GET
    @Path("/book/{bookId}")
    public Uni<List<Reservation>> getReservationsByBook(@PathParam("bookId") String bookId) {
        LOG.infov("Retrieving reservations for book: {0}", bookId);
        return reservationRepository.find("bookId", bookId).list()
                .onItem().transform(reservations -> reservations)
                .onFailure().invoke(e -> LOG.errorv("Error retrieving reservations for book: {0}", bookId, e))
                .onFailure().recoverWithItem(e -> {
                    throw new WebApplicationException("Error retrieving reservations for book: " + e.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
                });
    }
}

