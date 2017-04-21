package edu.sjsu.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.sjsu.dataaccess.FlightDAO;
import edu.sjsu.models.Flight;

@Service
public class FlightService {

	@Autowired
	private FlightDAO flightdao;
	
	public Flight getFlight(String number) {
		return flightdao.getFlight(number);
	}
	
}
