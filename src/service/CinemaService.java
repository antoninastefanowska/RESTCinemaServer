package service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;

import exceptions.AuthorizationException;
import exceptions.InvalidIdException;
import exceptions.NoSuchUserException;
import exceptions.SeatAlreadyTakenException;
import exceptions.UserAlreadyExistsException;
import exceptions.WrongPasswordException;
import model.Authorization;
import model.Database;
import model.EntityList;
import model.Film;
import model.Reservation;
import model.Seat;
import model.Showing;
import model.User;
import model.Link;

@Path("/")
public class CinemaService implements ICinemaService {
	private static Database database;
	
	public CinemaService() {
		database = Database.getInstance();
	}
	
	private void createShowingLinks(UriInfo uriInfo, Showing showing) {
		List<Link> links = new ArrayList<>();
		String url1 = uriInfo.getBaseUriBuilder()
				.path(CinemaService.class)
				.path("showings")
				.path(String.valueOf(showing.getIdentifier()))
				.build()
				.toString();
		Link link1 = new Link(url1, "self");
		links.add(link1);
		
		String url2 = uriInfo.getBaseUriBuilder()
				.path(CinemaService.class)
				.path("showings")
				.path(String.valueOf(showing.getIdentifier()))
				.path("seats")
				.build()
				.toString();
		Link link2 = new Link(url2, "seats");
		links.add(link2);
		
		String url3 = uriInfo.getBaseUriBuilder()
				.path(CinemaService.class)
				.path("films")
				.path(String.valueOf(showing.getFilmId()))
				.build()
				.toString();
		Link link3 = new Link(url3, "film");
		links.add(link3);
		
		showing.setLinks(links);
	}
	
	private void createFilmLinks(UriInfo uriInfo, Film film) {
		List<Link> links = new ArrayList<>();
		String url1 = uriInfo.getBaseUriBuilder()
				.path(CinemaService.class)
				.path("films")
				.path(String.valueOf(film.getIdentifier()))
				.build()
				.toString();
		links.add(new Link(url1, "self"));
		
		String url2 = uriInfo.getBaseUriBuilder()
				.path(CinemaService.class)
				.path("films")
				.path(String.valueOf(film.getIdentifier()))
				.path("cover")
				.build()
				.toString();
		links.add(new Link(url2, "cover"));
		film.setLinks(links);
	}
	
	private void createReservationLinks(UriInfo uriInfo, Reservation reservation) {
		List<Link> links = new ArrayList<>();
		String url = uriInfo.getBaseUriBuilder()
				.path(CinemaService.class)
				.path("reservations")
				.path(String.valueOf(reservation.getIdentifier()))
				.build()
				.toString();
		links.add(new Link(url, "self"));
	}

	@Override
	public EntityList<Showing> getShowings(UriInfo uriInfo) {
		List<Showing> showings = database.getShowings();
		for (Showing showing : showings)
			createShowingLinks(uriInfo, showing);		
		return new EntityList<>(showings);
	}

	@Override
	public Showing getShowing(int id, UriInfo uriInfo) {
		try {
			Showing showing = database.getShowing(id);
			createShowingLinks(uriInfo, showing);
			return showing;
		} catch (InvalidIdException e) {
			Response response = Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
			throw new WebApplicationException(e, response);
		}
	}
	
	@Override
	public EntityList<Film> getFilms(UriInfo uriInfo) {
		List<Film> films = database.getFilms();
		for (Film film : films)
			createFilmLinks(uriInfo, film);
		return new EntityList<>(films);
	}

	@Override
	public Film getFilm(int id, UriInfo uriInfo) {
		try {
			Film film = database.getFilm(id);
			createFilmLinks(uriInfo, film);
			return film;
		} catch (InvalidIdException e) {
			Response response = Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
			throw new WebApplicationException(e, response);
		}
	}

	@Override
	public Response getFilmCover(int id) {
		try {
			Film film = database.getFilm(id);
			File file = film.getCoverFile();
			ResponseBuilder responseBuilder = Response.ok(file);
			responseBuilder.header("Content-Disposition", "attachment;filename=" + film.getCoverFilename());
			return responseBuilder.build();
		} catch (InvalidIdException e) {
			Response response = Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
			throw new WebApplicationException(e, response);
		}
	}

	@Override
	public EntityList<Seat> getTakenSeats(int showingId) {
		try {
			Showing showing = database.getShowing(showingId);
			return new EntityList<>(showing.getTakenSeats());
		} catch (InvalidIdException e) {
			Response response = Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
			throw new WebApplicationException(e, response);
		}
	}

	@Override
	public void createUser(String authorizationHeader) {
		try {
			Authorization authorization = new Authorization(authorizationHeader);
			if (database.userExists(authorization.getUsername()))
				throw new UserAlreadyExistsException();
			database.addUser(new User(authorization));
			System.out.println("Utworzono u¿ytkownika: " + authorization.getUsername());
		} catch (UserAlreadyExistsException e) {
			Response response = Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
			throw new WebApplicationException(e, response);
		} catch (IOException e) {
			Response response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
			throw new WebApplicationException(e, response);
		}
	}

	@Override
	public void login(String authorizationHeader) {
		try {
			Authorization authorization = new Authorization(authorizationHeader);
			Authorization existingAuthorization = database.getUserAuthorization(authorization.getUsername());
			if (existingAuthorization == null)
				throw new NoSuchUserException();
			if (!existingAuthorization.getPassword().equals(authorization.getPassword()))
				throw new WrongPasswordException();
			System.out.println("Zalogowano u¿ytkownika: " + authorization.getUsername());
		} catch (NoSuchUserException | WrongPasswordException e) {
			Response response = Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build();
			throw new WebApplicationException(e, response);
		} catch (IOException e) {
			Response response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
			throw new WebApplicationException(e, response);
		}
	}

	@Override
	public EntityList<Reservation> getReservations(String authorizationHeader, UriInfo uriInfo) {
		try {
			Authorization authorization = new Authorization(authorizationHeader);
			User user = database.getUser(authorization.getUsername());
			if (user == null || !user.getAuthorization().checkAuthorization(authorization))
				throw new AuthorizationException();
			
			List<Reservation> reservations = user.getReservations();
			for (Reservation reservation : reservations)
				createReservationLinks(uriInfo, reservation);
			return new EntityList<>(reservations);
		} catch (AuthorizationException | NoSuchUserException e) {
			Response response = Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build();
			throw new WebApplicationException(e, response);
		} catch (IOException e) {
			Response response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
			throw new WebApplicationException(e, response);
		}
	}

	@Override
	public Reservation getReservation(String authorizationHeader, int reservationId, UriInfo uriInfo) {
		try {
			Authorization authorization = new Authorization(authorizationHeader);
			Authorization existingAuthorization = database.getUserAuthorization(authorization.getUsername());		
			if (existingAuthorization == null || !existingAuthorization.checkAuthorization(authorization))
				throw new AuthorizationException();		
			Reservation reservation = database.getReservation(authorization.getUsername(), reservationId);
			createReservationLinks(uriInfo, reservation);
			return reservation;
		} catch (InvalidIdException e) {
			Response response = Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
			throw new WebApplicationException(e, response);
		} catch (AuthorizationException | NoSuchUserException e) {
			Response response = Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build();
			throw new WebApplicationException(e, response);
		} catch (IOException e) {
			Response response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
			throw new WebApplicationException(e, response);
		}
	}

	@Override
	public String makeReservation(String authorizationHeader, Reservation reservation) {
		try {
			Authorization authorization = new Authorization(authorizationHeader);
			Authorization existingAuthorization = database.getUserAuthorization(authorization.getUsername());		
			if (existingAuthorization == null || !existingAuthorization.checkAuthorization(authorization))
				throw new AuthorizationException();
			database.makeReservation(authorization.getUsername(), reservation);
			System.out.println("Utworzono rezerwacjê.");
			return reservation.getCode();
		} catch (SeatAlreadyTakenException e) {
			Response response = Response.status(Response.Status.FORBIDDEN).entity(e.getMessage()).build();
			throw new WebApplicationException(e, response);
		} catch (AuthorizationException | NoSuchUserException e) {
			Response response = Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build();
			throw new WebApplicationException(e, response);
		} catch (IOException e) {
			Response response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
			throw new WebApplicationException(e, response);
		}
	}

	@Override
	public void cancelReservation(String authorizationHeader, int reservationId) {
		try {
			Authorization authorization = new Authorization(authorizationHeader);
			Authorization existingAuthorization = database.getUserAuthorization(authorization.getUsername());		
			if (existingAuthorization == null || !existingAuthorization.checkAuthorization(authorization))
				throw new AuthorizationException();
			
			database.cancelReservation(authorization.getUsername(), reservationId);
			System.out.println("Anulowano rezerwacjê.");
		} catch (InvalidIdException e) {
			Response response = Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
			throw new WebApplicationException(e, response);
		} catch (AuthorizationException | NoSuchUserException e) {
			Response response = Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build();
			throw new WebApplicationException(e, response);
		} catch (IOException e) {
			Response response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
			throw new WebApplicationException(e, response);
		}
	}

	@Override
	public void updateReservation(String authorizationHeader, int reservationId, Reservation reservation) {
		try {
			Authorization authorization = new Authorization(authorizationHeader);
			Authorization existingAuthorization = database.getUserAuthorization(authorization.getUsername());		
			if (existingAuthorization == null || !existingAuthorization.checkAuthorization(authorization))
				throw new AuthorizationException();		
			database.updateReservation(authorization.getUsername(), reservationId, reservation);
			System.out.println("Zaktualizowano rezerwacjê.");
		} catch (SeatAlreadyTakenException e) {
			Response response = Response.status(Response.Status.FORBIDDEN).entity(e.getMessage()).build();
			throw new WebApplicationException(e, response);
		} catch (InvalidIdException e) {
			Response response = Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
			throw new WebApplicationException(e, response);
		} catch (AuthorizationException | NoSuchUserException e) {
			Response response = Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build();
			throw new WebApplicationException(e, response);
		} catch (IOException e) {
			Response response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
			throw new WebApplicationException(e, response);
		}
	}
}