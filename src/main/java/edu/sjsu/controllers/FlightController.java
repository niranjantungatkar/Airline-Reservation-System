package edu.sjsu.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.sjsu.models.Flight;
import edu.sjsu.services.FlightService;

@RestController
public class FlightController {

	@Autowired
	private FlightService flightservice;
	
	@RequestMapping(value="/flight/{number}")
	public Flight getFlight(@PathVariable("number") String number) {
		return flightservice.getFlight(number);
	}
}
