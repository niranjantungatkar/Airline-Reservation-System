package edu.sjsu.services;

import java.util.ArrayList;
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
	
	public void createReservation(String passengerid, String flightLists) {
		Passenger p = new Passenger();
		p.setId(passengerid);
		
		String [] flightNames = flightLists.split(",");
		
		List<Flight> flights = new ArrayList<Flight>();
		for(String flight : flightNames) {
			Flight f = new Flight();
			f.setNumber(flight);
			flights.add(f);
		}
		
		Reservation rs = new Reservation();
		rs.setOrderNumber("111");
		rs.setPrice(300);
		rs.setPassenger(p);
		rs.setFlights(flights);
	
		reservationDAO.createReservation(rs);
	}
	
	public Reservation getReservation(String orderNumber) {
		return reservationDAO.getReservation(orderNumber);
	}
}
