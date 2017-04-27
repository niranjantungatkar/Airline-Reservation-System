package edu.sjsu.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.sjsu.models.Flight;
import edu.sjsu.models.Passenger;
import edu.sjsu.models.Reservation;
import edu.sjsu.services.FlightService;
import edu.sjsu.services.PassengerService;
import edu.sjsu.services.ReservationService;

@RestController
public class ReservationController {

	@Autowired
	private ReservationService reservationservice;

	@Autowired
	private PassengerService passengerService;

	@Autowired
	private FlightService flightService;

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/reservation", method = RequestMethod.POST)
	public ResponseEntity makeReservation(@RequestParam("passengerId") String pid,
			@RequestParam("flightLists") String[] flightLists) {

		try {

			Passenger passenger = passengerService.getPassenger(pid);
			if (passenger == null) {
				System.out.println("Passenger does not exists");
				throw new Exception("Passenger" + pid + " Does not exists");
			}

			// Check if overlap occurs between the flights
			if (flightService.checkOverlap(flightLists)) {
				System.out.println("Overlap between the timing!! Can not make the reservation");
				throw new Exception("Overlap between flights occurred. Can not make reservation");
			}

			if (!flightService.checkFlightsAvailability(flightLists)) {
				System.out.println("Not enough seats avaiable in the some flights");
				throw new Exception("Not enough seats avaiable for making reservation");
			}

			reservationservice.createReservation(pid, flightLists);
			return null;
		} catch (Exception e) {
			// TODO: handle exception
			return new ResponseEntity(getErrorResponse("400", e.getMessage()), HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/reservation/{orderNumber}")
	public Reservation getReservation(@PathVariable("orderNumber") String orderNumber) {
		return reservationservice.getReservation(orderNumber);
	}

	@SuppressWarnings("rawtypes")
	public HashMap<String, Map> getErrorResponse(String errorcode, String error) {
		HashMap<String, String> errorMap = new HashMap<String, String>();
		errorMap.put("code", errorcode);
		errorMap.put("msg", error);
		HashMap<String, Map> errorResponse = new HashMap<String, Map>();
		errorResponse.put("Badrequest", errorMap);
		return errorResponse;
	}

}
