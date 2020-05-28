package service;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import model.EntityList;
import model.Film;
import model.Reservation;
import model.Seat;
import model.Showing;

public interface ICinemaService {
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/showings")
	EntityList<Showing> getShowings(@Context UriInfo uriInfo);
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/showings/{id}")
	Showing getShowing(@PathParam("id") int id, @Context UriInfo uriInfo);
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/films")
	EntityList<Film> getFilms(@Context UriInfo uriInfo);

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/films/{id}")
	Film getFilm(@PathParam("id") int id, @Context UriInfo uriInfo);
	
	@GET
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@Path("/films/{id}/cover")
	Response getFilmCover(@PathParam("id") int id);
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/showings/{id}/seats")
	EntityList<Seat> getTakenSeats(@PathParam("id") int showingId);
	
	@POST
	@Path("/register")
	void createUser(@HeaderParam("Authorization") String authorizationHeader);
	
	@POST
	@Path("/login")
	void login(@HeaderParam("Authorization") String authorizationHeader);

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/reservations")
	EntityList<Reservation> getReservations(
			@HeaderParam("Authorization") String authorizationHeader, 
			@Context UriInfo uriInfo);

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/reservations/{id}")
	Reservation getReservation(
			@HeaderParam("Authorization") String authorizationHeader, 
			@PathParam("id") int reservationId,
			@Context UriInfo uriInfo);

	@POST
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/reservations")
	String makeReservation(
			@HeaderParam("Authorization") String authorizationHeader,
			Reservation reservation);
	
	@DELETE
	@Path("/reservations/{id}")
	void cancelReservation(
			@HeaderParam("Authorization") String authorizationHeader,
			@PathParam("id") int reservationId);

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/reservations/{id}")
	void updateReservation(
			@HeaderParam("Authorization") String authorizationHeader,
			@PathParam("id") int reservationId, 
			Reservation reservation);
}
