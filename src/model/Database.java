package model;

import java.util.Arrays;
import java.util.List;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import exceptions.InvalidIdException;
import exceptions.NoSuchUserException;
import exceptions.SeatAlreadyTakenException;
import utils.Converter;
import utils.DataInitializer;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Projections.*;
import static com.mongodb.client.model.Updates.*;
import static com.mongodb.client.model.Sorts.*;
import static com.mongodb.client.model.Aggregates.*;

public class Database {
	private static Database instance;
	
	private MongoDatabase mongoDB;
	private MongoCollection<Document> films;
	private MongoCollection<Document> users;
	private MongoCollection<Document> showings;
	private MongoCollection<Document> blockedIpAddresses;
	
	public Database() {	
		openConnection();
	}
	
	public static Database getInstance() {
		if (instance == null)
			instance = new Database();
		return instance;
	}
	
	public static void initialize() {
		instance = new Database();
	}
	
	@SuppressWarnings("resource")
	private void openConnection() {
		MongoClient mongoClient = new MongoClient();
		mongoDB = mongoClient.getDatabase("CinemaDB");
		films = mongoDB.getCollection("Films");
		users = mongoDB.getCollection("Users");
		showings = mongoDB.getCollection("Showings");
		blockedIpAddresses = mongoDB.getCollection("BlockedIPs");
	}

	public List<Film> getFilms() {
		FindIterable<Document> documents = films.find().projection(fields(excludeId()));
		return Converter.toEntityList(documents, Film.class);
	}

	public List<Showing> getShowings() {
		FindIterable<Document> documents = showings.find().projection(fields(excludeId())).sort(descending("date"));
		return Converter.toEntityList(documents, Showing.class);
	}

	public List<User> getUsers() {
		FindIterable<Document> documents = users.find().projection(fields(excludeId()));
		return Converter.toEntityList(documents, User.class);
	}
	
	public int addFilm(Film film) {
		int id = film.generateIdentifier();
		Document document = Converter.toDocument(film);
		films.insertOne(document);
		return id;
	}
	
	public int addShowing(Showing showing) {
		int id = showing.generateIdentifier();
		Document document = Converter.toDocument(showing);
		showings.insertOne(document);
		return id;
	}
	
	public void addUser(User user) {
		Document document = Converter.toDocument(user);
		users.insertOne(document);
	}
	
	public Film getFilm(int id) throws InvalidIdException {
		Document document = films.find(eq("identifier", id)).projection(fields(excludeId())).first();
		if (document != null)
			return Converter.toEntity(document, Film.class);
		else
			throw new InvalidIdException();
	}
	
	public Showing getShowing(int id) throws InvalidIdException {
		Document document = showings.find(eq("identifier", id)).projection(fields(excludeId())).first();
		if (document != null)
			return Converter.toEntity(document, Showing.class);
		else
			throw new InvalidIdException();
	}
	
	public User getUser(String username) throws NoSuchUserException {
		Document document = users.find(eq("authorization.username", username)).projection(fields(excludeId())).first();
		if (document != null)
			return Converter.toEntity(document, User.class);
		else
			throw new NoSuchUserException();
	}
	
	public boolean userExists(String username) {
		Document document = users.find(eq("authorization.username", username)).first();
		return document != null;
	}
	
	public void blockIpAddress(String ipAddress) {
		Document document = new Document("ip", ipAddress);
		blockedIpAddresses.insertOne(document);
		System.out.println("Adres IP zosta³ zablokowany.");
	}
	
	public void unblockIpAddress(String ipAddress) {
		blockedIpAddresses.findOneAndDelete(eq("ip", ipAddress));
		System.out.println("Adres IP zosta³ odblokowany.");
	}
	
	public boolean isIpAddressBlocked(String ipAddress) {
		Document document = blockedIpAddresses.find(eq("ip", ipAddress)).first();
		return document != null;
	}
	
	public Authorization getUserAuthorization(String username) throws NoSuchUserException {	
		Document authenticationDocument = users
				.aggregate(Arrays.asList(
						match(eq("authorization.username", username)),
						replaceRoot("$authorization")))
				.first();
		
		if (authenticationDocument != null)
			return Converter.toEntity(authenticationDocument, Authorization.class);
		else
			throw new NoSuchUserException();
	}
	
	public List<Reservation> getReservations(String username) {	
		AggregateIterable<Document> reservationDocuments = users
				.aggregate(Arrays.asList(
					match(eq("authorization.username", username)),
					unwind("$reservations"),
					replaceRoot("$reservations")));	
		return Converter.toEntityList(reservationDocuments, Reservation.class);
	}
	
	public Reservation getReservation(String username, int reservationId) throws InvalidIdException {
		Document reservationDocument = users
				.aggregate(Arrays.asList(
						match(eq("authorization.username", username)),
						unwind("$reservations"),
						replaceRoot("$reservations"),
						match(eq("identifier", reservationId))))
				.first();
		
		if (reservationDocument != null)
			return Converter.toEntity(reservationDocument, Reservation.class);
		else
			throw new InvalidIdException();
	}
	
	private boolean checkTakenSeats(Reservation reservation) {
		AggregateIterable<Document> seatDocuments = showings
				.aggregate(Arrays.asList(
						match(eq("identifier", reservation.getShowingId())),
						unwind("$takenSeats"),
						replaceRoot("$takenSeats")));
		
		List<Seat> takenSeats = Converter.toEntityList(seatDocuments, Seat.class);
		for (Seat seat : reservation.getSeats())
			for (Seat takenSeat : takenSeats)
				if (takenSeat.compare(seat))
					return false;
		return true;
	}
	
	public void makeReservation(String username, Reservation reservation)
			throws SeatAlreadyTakenException {
		
		if (!checkTakenSeats(reservation))
			throw new SeatAlreadyTakenException();
		
		reservation.generateIdentifier();
		reservation.generateCode();
		Document reservationDocument = Converter.toDocument(reservation);	
		users.updateOne(eq("authorization.username", username), push("reservations", reservationDocument));
		
		List<Document> newSeatDocuments = Converter.toDocumentList(reservation.getSeats());
		showings.updateOne(eq("identifier", reservation.getShowingId()), pushEach("takenSeats", newSeatDocuments));
	}
	
	public void updateReservation(String username, int reservationId, Reservation reservation) 
			throws SeatAlreadyTakenException, InvalidIdException {
		
		Reservation oldReservation = getReservation(username, reservationId);
		List<Document> oldSeatDocuments = Converter.toDocumentList(oldReservation.getSeats());
		showings.updateOne(eq("identifier", oldReservation.getShowingId()), pullAll("takenSeats", oldSeatDocuments));
		
		if (!checkTakenSeats(reservation)) {
			showings.updateOne(eq("identifier", oldReservation.getShowingId()), pushEach("takenSeats", oldSeatDocuments));
			throw new SeatAlreadyTakenException();
		}
		
		reservation.setId(reservationId);
		Document oldReservationDocument = Converter.toDocument(oldReservation);
		Document newReservationDocument = Converter.toDocument(reservation);
		List<Document> newSeatDocuments = Converter.toDocumentList(reservation.getSeats());
		users.updateOne(eq("authorization.username", username), pull("reservations", oldReservationDocument));
		users.updateOne(eq("authorization.username", username), push("reservations", newReservationDocument));
		showings.updateOne(eq("identifier", reservation.getShowingId()), pushEach("takenSeats", newSeatDocuments));
	}
	
	public void cancelReservation(String username, int reservationId) 
			throws InvalidIdException {
		
		Reservation oldReservation = getReservation(username, reservationId);
		Document oldReservationDocument = Converter.toDocument(oldReservation);
		List<Document> oldSeatDocuments = Converter.toDocumentList(oldReservation.getSeats());
		
		users.updateOne(eq("authorization.username", username), pull("reservations", oldReservationDocument));
		showings.updateOne(eq("identifier", oldReservation.getShowingId()), pullAll("takenSeats", oldSeatDocuments));
	}
	
	private void clearDatabase() {
		mongoDB.drop();
		System.out.println("Usuniêto bazê danych.");
	}
	
	public void processCommand(String command) {
		String[] words = command.split(" ");
		switch (words[0]) {
		case "block":
			blockIpAddress(words[1]);
			break;
		case "unblock":
			unblockIpAddress(words[1]);
			break;
		case "populate-db":
			DataInitializer.populateDatabase(this);
			break;
		case "drop-db":
			clearDatabase();
			break;
		}
	}
}
