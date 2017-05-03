package edu.sjsu.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.sjsu.dataaccess.FlightDAO;
import edu.sjsu.dataaccess.PassengerDAO;
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
		for(Flight flight : flights){
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
