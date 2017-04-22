package edu.sjsu.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.sjsu.models.Flight;
import edu.sjsu.models.Reservation;
import edu.sjsu.services.FlightService;
import edu.sjsu.services.ReservationService;

@RestController
public class ReservationController {
	
	@Autowired
	private ReservationService reservationservice;
	
	@RequestMapping(value="/reservation", method = RequestMethod.POST)
	public void makeReservation(@RequestParam("passengerId") String pid, @RequestParam("flightLists") String flightLists) {
		reservationservice.createReservation(pid, flightLists);
	}
	
	@RequestMapping(value="/reservation/{orderNumber}")
	public Reservation getReservation(@PathVariable("orderNumber") String orderNumber) {
		return reservationservice.getReservation(orderNumber);
	}

}
