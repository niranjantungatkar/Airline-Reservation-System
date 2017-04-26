package edu.sjsu.services;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.sjsu.dataaccess.PassengerDAO;
import edu.sjsu.models.Passenger;

@Service
public class PassengerService {

	@Autowired
	private PassengerDAO passengerDAO;
	
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
	
}
