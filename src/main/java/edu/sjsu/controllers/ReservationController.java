package edu.sjsu.controllers;

import java.util.HashMap;
import java.util.Map;

import javax.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

			// Check for the over lap between the flights of current reservation
			if (flightService.checkOverlap(flightLists)) {
				System.out.println("Overlap between the timing!! Can not make the reservation");
				throw new Exception("Overlap between flights occurred. Can not make reservation");
			}

			// Check for availability of flight using seats left
			if (!flightService.checkFlightsAvailability(flightLists)) {
				System.out.println("Not enough seats avaiable in the some flights");
				throw new Exception("Not enough seats avaiable for making reservation");
			}

			// Check duplicate reservation
			if (reservationservice.checkDuplicate(pid, flightLists)) {
				System.out.println("Duplicate reservation");
				throw new Exception(
						"Duplicate reservation. Reservation with same passenger and flights already exists. Please check is the history");
			}

			// Check existing reservations for overlap
			if (reservationservice.checkOverlapExisting(pid, flightLists)) {
				System.out.println("Overlap with existing reservation");
				throw new Exception("Overlap between the existing reservtions occurs");
			}

			reservationservice.createReservation(pid, flightLists);
			return null;
		} catch (Exception e) {
			e.printStackTrace(System.out);
			return new ResponseEntity(getErrorResponse("400", e.getMessage()), HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/reservation/{number}", method = RequestMethod.POST)
	public ResponseEntity updateReservation(@PathVariable("number") String number,
			@RequestParam("flightsAdded") String[] flightsAdded,
			@RequestParam("flightsRemoved") String[] flightsRemoved) {

		try {
			if (flightsAdded.length == 0) {
				throw new Exception("FlightsAdded can not be empty. Please check the request again");
			}
			if (flightsRemoved.length == 0) {
				throw new Exception("Flightsremoved can not be empty. Please check the request again");
			}

			reservationservice.updateReservation(number, flightsAdded, flightsRemoved);
		} catch (Exception e) {
			return new ResponseEntity(getErrorResponse("400", e.getMessage()), HttpStatus.BAD_REQUEST);
		}

		return null;
	}

	// passengerId=XX&from=YY&to=ZZ&flightNumber=GH2Z1
	@RequestMapping(value = "/reservation")
	public ResponseEntity searchReservation(@RequestParam(value = "passengerId", required = false) String passengerId,
			@RequestParam(value = "from", required = false) String from,
			@RequestParam(value = "to", required = false) String to,
			@RequestParam(value = "flightNumber", required = false) String flightNumber) {
		HashMap<String, String> parameters = new HashMap<>();
		if (passengerId != null)
			parameters.put("passengerId", passengerId);
		if (from != null)
			parameters.put("from", from);
		if (to != null)
			parameters.put("to", to);
		if (flightNumber != null)
			parameters.put("flightNumber", flightNumber);
		try{
			reservationservice.searchReservations(parameters);
		}catch(Exception e){
			return new ResponseEntity(getErrorResponse("400", e.getMessage()), HttpStatus.BAD_REQUEST);
		}
		
		// TODO: Return success message in xml here
		return null;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/reservation/{number}", method = RequestMethod.DELETE)
	public ResponseEntity cancelReservation(@PathVariable(value = "number") String number) {
		try {
			reservationservice.cancelReservation(number);
			// TODO : Return Success message in xml
			return null;
		} catch (Exception e) {
			return new ResponseEntity(getErrorResponse("400", e.getMessage()), HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/reservation/{orderNumber}", method = RequestMethod.GET)
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
