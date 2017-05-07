package edu.sjsu.controllers;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import edu.sjsu.models.Passenger;
import edu.sjsu.models.Reservation;
import edu.sjsu.services.FlightService;
import edu.sjsu.services.PassengerService;
import edu.sjsu.services.ReservationService;
import edu.sjsu.utils.ReservationNotFoundException;
import edu.sjsu.utils.ResponseBodyGenerator;

@RestController
public class ReservationController {

	@Autowired
	private ReservationService reservationservice;

	@Autowired
	private PassengerService passengerService;

	@Autowired
	private FlightService flightService;

	@Autowired
	private ResponseBodyGenerator responseBodyGenerator;

	/**
	 * Make the reservation for passenger
	 * 
	 * @param pid
	 * @param flightLists
	 * @return
	 * @throws JSONException
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/reservation", method = RequestMethod.POST)
	public ResponseEntity makeReservation(@RequestParam("passengerId") String pid,
			@RequestParam("flightLists") String[] flightLists) throws JSONException {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setContentType(MediaType.APPLICATION_XML);
		try {

			Passenger passenger = passengerService.getPassenger(pid);
			if (passenger == null) {
				System.out.println("Passenger does not exists");
				throw new ReservationNotFoundException("Passenger" + pid + " Does not exists");
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

			// reservationservice.createReservation(pid, flightLists);
			Reservation reservation = reservationservice.createReservation(pid, flightLists);
			LinkedHashMap<String, Object> output = responseBodyGenerator.buildMakeReservationResponse(reservation);
			JSONObject json = new JSONObject(output);
			// gson.toJson(output, LinkedHashMap.class);
			Gson gson = new Gson();
			return new ResponseEntity(XML.toString(json), responseHeaders, HttpStatus.OK);

		} catch (ReservationNotFoundException e) {
			return new ResponseEntity(XML.toString(getErrorResponse("404", e.getMessage())), responseHeaders,
					HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			e.printStackTrace(System.out);
			return new ResponseEntity(XML.toString(getErrorResponse("400", e.getMessage())), responseHeaders,
					HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Update the exiting reservation
	 * 
	 * @param number
	 * @param flightsAdded
	 * @param flightsRemoved
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/reservation/{number}", method = RequestMethod.POST)
	public ResponseEntity updateReservation(@PathVariable("number") String number,
			@RequestParam(value = "flightsAdded", required = false) String[] flightsAdded,
			@RequestParam(value = "flightsRemoved", required = false) String[] flightsRemoved) {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setContentType(MediaType.APPLICATION_JSON);
		try {
			// if (flightsAdded == null || flightsAdded.length == 0) {
			// throw new Exception("FlightsAdded can not be empty. Please check
			// the request again");
			// }
			// if (flightsRemoved == null || flightsRemoved.length == 0) {
			// throw new Exception("Flightsremoved can not be empty. Please
			// check the request again");
			// }
			// get the updated reservation
			Reservation reservation = reservationservice.updateReservation(number, flightsAdded, flightsRemoved);
			LinkedHashMap<String, Object> output = responseBodyGenerator.buildMakeReservationResponse(reservation);
			Gson gson = new Gson();
			return new ResponseEntity(gson.toJson(output, LinkedHashMap.class), responseHeaders, HttpStatus.OK);
		} catch (ReservationNotFoundException e) {
			return new ResponseEntity(getErrorResponse("404", e.getMessage()).toString(), responseHeaders,
					HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity(getErrorResponse("400", e.getMessage()).toString(), responseHeaders,
					HttpStatus.BAD_REQUEST);
		}
	}

	/**Search the reservation from parameters
	 * 
	 * @param passengerId
	 * @param from
	 * @param to
	 * @param flightNumber
	 * @return
	 * @throws ReservationNotFoundException
	 * @throws JSONException 
	 */
	@RequestMapping(value = "/reservation", method = RequestMethod.GET)
	public ResponseEntity searchReservation(@RequestParam(value = "passengerId", required = false) String passengerId,
			@RequestParam(value = "from", required = false) String from,
			@RequestParam(value = "to", required = false) String to,
			@RequestParam(value = "flightNumber", required = false) String flightNumber)
			throws ReservationNotFoundException, JSONException {
		LinkedHashMap<String, String> parameters = new LinkedHashMap<>();

		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setContentType(MediaType.APPLICATION_XML);

		if (passengerId != null)
			parameters.put("passengerId", passengerId);
		if (from != null)
			parameters.put("from", from);
		if (to != null)
			parameters.put("to", to);
		if (flightNumber != null)
			parameters.put("flightNumber", flightNumber);
		try {
			if (parameters.size() == 0) {
				throw new ReservationNotFoundException("Not enough parameters to query the reservations");
			}
			List<Reservation> reservations = reservationservice.searchReservations(parameters);
			if (reservations.size() == 0) {
				throw new ReservationNotFoundException("No reservations found!!");
			}
			LinkedHashMap<String, Object> output = responseBodyGenerator.buildSearchReservationResponse(reservations);
			JSONObject json = new JSONObject(output);
			return new ResponseEntity(XML.toString(json), responseHeaders, HttpStatus.OK);
		} catch (ReservationNotFoundException e) {
			return new ResponseEntity( XML.toString(getErrorResponse("404", e.getMessage())), responseHeaders,
					HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity(XML.toString(getErrorResponse("400", e.getMessage())), responseHeaders,
					HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Cancels the given reservation
	 * @param number
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/reservation/{number}", method = RequestMethod.DELETE)
	public ResponseEntity cancelReservation(@PathVariable(value = "number") String number) {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setContentType(MediaType.APPLICATION_JSON);
		try {
			reservationservice.cancelReservation(number);
			HashMap<String, String> errorMap = new HashMap<String, String>();
			errorMap.put("code", "200");
			errorMap.put("msg", "Reservation cancelled successfully");
			HashMap<String, Map> errorResponse = new HashMap<String, Map>();
			errorResponse.put("Response", errorMap);
			JSONObject json = new JSONObject(errorResponse);
			return new ResponseEntity(json.toString(), responseHeaders, HttpStatus.OK);
		} catch (ReservationNotFoundException e) {
			return new ResponseEntity(getErrorResponse("404", e.getMessage()).toString(), responseHeaders,
					HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity(getErrorResponse("400", e.getMessage()).toString(), responseHeaders,
					HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Get the reservation details from reservation number
	 * @param orderNumber
	 * @return
	 */
	@RequestMapping(value = "/reservation/{orderNumber}", method = RequestMethod.GET)
	public ResponseEntity getReservation(@PathVariable(value = "orderNumber", required = true) String orderNumber) {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setContentType(MediaType.APPLICATION_JSON);
		try {
			Reservation reservation = reservationservice.getReservation(orderNumber);
			if (reservation == null) {
				throw new ReservationNotFoundException("Reservation does not exists");
			}
			LinkedHashMap<String, Object> output = responseBodyGenerator.buildMakeReservationResponse(reservation);
			Gson gson = new Gson();
			return new ResponseEntity(gson.toJson(output, LinkedHashMap.class), responseHeaders, HttpStatus.OK);
		} catch (ReservationNotFoundException e) {
			return new ResponseEntity(getErrorResponse("404", e.getMessage()).toString(), responseHeaders,
					HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity(getErrorResponse("400", e.getMessage()).toString(), responseHeaders,
					HttpStatus.BAD_REQUEST);
		}

	}

	/**
	 * Create the error response in Map
	 * @param errorcode
	 * @param error
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public JSONObject getErrorResponse(String errorcode, String error) {
		HashMap<String, String> errorMap = new HashMap<String, String>();
		errorMap.put("code", errorcode);
		errorMap.put("msg", error);
		HashMap<String, Map> errorResponse = new HashMap<String, Map>();
		errorResponse.put("Badrequest", errorMap);
		JSONObject json = new JSONObject(errorResponse);
		return json;
	}

}
