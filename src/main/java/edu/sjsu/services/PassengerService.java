package edu.sjsu.services;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.sjsu.dataaccess.FlightDAO;
import edu.sjsu.dataaccess.PassengerDAO;
import edu.sjsu.dataaccess.ReservationDAO;
import edu.sjsu.models.Flight;
import edu.sjsu.models.Passenger;
import edu.sjsu.models.Reservation;

@Service
public class PassengerService {

	@Autowired
	private PassengerDAO passengerDAO;
	
	@Autowired
	private FlightDAO flightDAO;
	
	@Autowired
	private ReservationDAO reservationDAO;
	
	public List<Passenger> getAll() {
		return passengerDAO.getAll();
	}
	
	public void createPassenger(Passenger newPassenger) {
		passengerDAO.createPassenger(newPassenger);
	}
	
	public Passenger getPassenger(String id) {
		return passengerDAO.getPassenger(id);
	}
	
	public Passenger updatePassenger(Passenger updPassenger) {
		return passengerDAO.updatePassenger(updPassenger);
	}
	
	public boolean deletePassenger(String id) {
		try {
			Passenger delPassenger = passengerDAO.getPassenger(id);
			List<Reservation> reservations = delPassenger.getReservations();
			for(Reservation reservation : reservations) {
				List<Flight> flights = reservation.getFlights();
				for(Flight flight : flights) {
					flight.setSeatsLeft(flight.getSeatsLeft() + 1);
					flightDAO.createFlight(flight);
				}
				reservationDAO.deleteReservation(reservation);
			}
			return true;
		} catch (Exception ex) {
			return false;
		}
	}
}
