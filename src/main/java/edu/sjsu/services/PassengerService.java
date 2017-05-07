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
	
	/** Deletes the passenger
	 * 
	 * @param id
	 * @return true, if passenger deleted successfully, else false
	 */
	public boolean deletePassenger(String id) {
		Passenger delPassenger = passengerDAO.getPassenger(id);
		if(delPassenger != null) {
			List<Reservation> reservations = reservationDAO.getReservations(delPassenger);
			if(reservations != null) {
				for(Reservation reservation : reservations) {
					List<Flight> flights = reservation.getFlights();
					for(Flight flight : flights) {
						flight.setSeatsLeft(flight.getSeatsLeft() + 1);
						flight.getPassengers().remove(delPassenger);
						flightDAO.createFlight(flight);
					}
					reservationDAO.deleteReservation(reservation);
				}
			}
			passengerDAO.deletePassenger(delPassenger);
			return true;
		} else {
			return false;
		}
	}
}
