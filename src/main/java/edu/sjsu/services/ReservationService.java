package edu.sjsu.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.sjsu.dataaccess.ReservationDAO;
import edu.sjsu.models.Flight;
import edu.sjsu.models.Passenger;
import edu.sjsu.models.Reservation;

@Service
public class ReservationService {

	@Autowired
	private ReservationDAO reservationDAO;

	@Autowired
	private PassengerService passengerService;

	@Autowired
	private FlightService flightService;

	/*
	 * Returns true if there is any overlap between existing flights
	 */
	public boolean checkOverlapExisting(String passengerid, String[] flightLists) {
		Passenger passenger = passengerService.getPassenger(passengerid);
		List<Reservation> reservations = reservationDAO.getReservations(passenger);
		List<String> allFlights = new ArrayList<>();
		Collections.addAll(allFlights, flightLists);
		for (Reservation reservation : reservations) {
			List<Flight> flights = reservation.getFlights();
			for (Flight flight : flights) {
				allFlights.add(flight.getNumber());
			}
		}
		return flightService.checkOverlap(allFlights.toArray(new String[allFlights.size()]));
	}

	public boolean checkDuplicate(String passengerid, String[] flightLists) {
		int match = 0;
		Passenger pssenger = passengerService.getPassenger(passengerid);
		List<Reservation> reservations = reservationDAO.getReservations(pssenger);
		Arrays.sort(flightLists);
		if (reservations.size() == 0)
			return false;
		for (Reservation reservation : reservations) {
			List<Flight> flights = reservation.getFlights();
			List<String> flightNumbers = new ArrayList<>();
			for (Flight flight : flights) {
				flightNumbers.add(flight.getNumber());
			}
			String[] temp = flightNumbers.toArray(new String[flightNumbers.size()]);
			Arrays.sort(temp);
			match = 0;
			if (flights.size() == flightLists.length) {
				for (int i = 0; i < flightLists.length; i++) {
					if (temp[i].equals(flightLists[i])) {
						match++;
					}
				}
				if (match == flightLists.length)
					return true;
			}
		}
		return false;
	}

	public void createReservation(String passengerid, String[] flightLists) {

		Reservation reservation = new Reservation();

		Passenger passenger = passengerService.getPassenger(passengerid);
		List<Flight> flights = flightService.getFlights(flightLists);

		int total_price = 0;
		for (Flight flight : flights) {
			total_price += flight.getPrice();
		}

		reservation.setPassenger(passenger);
		reservation.setFlights(flights);
		reservation.setPrice(total_price);
		reservation.setOrderNumber("AJAY007");

		reservationDAO.createReservation(reservation);

		// Update flights
		for (Flight flight : flights) {
			flight.getPassengers().add(passenger);
			flight.setSeatsLeft(flight.getSeatsLeft() - 1);
			flightService.createFlight(flight);
		}
	}

	public Reservation getReservation(String orderNumber) {
		return reservationDAO.getReservation(orderNumber);
	}
	
	public List<Reservation> getReservations(Passenger passenger) {
		return reservationDAO.getReservations(passenger);
	}
}
