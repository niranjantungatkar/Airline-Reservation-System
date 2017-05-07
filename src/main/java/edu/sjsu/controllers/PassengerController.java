package edu.sjsu.controllers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.exception.ConstraintViolationException;
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

import edu.sjsu.services.PassengerService;
import edu.sjsu.services.ReservationService;
import edu.sjsu.models.*;

@RestController
public class PassengerController {

	@Autowired
	private PassengerService passengerService;

	@Autowired
	private ReservationService reservationService;

	/**
	 * CREATE PASSENGER - /passenger - METHOD - POST
	 * 
	 * @author tungatkarniranjan
	 * @param firstname
	 * @param lastname
	 * @param age
	 * @param gender
	 * @param phone
	 * @return Passenger
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/passenger", method = RequestMethod.POST)
	public ResponseEntity createPassenger(@RequestParam("firstname") String firstname,
			@RequestParam("lastname") String lastname, @RequestParam("age") int age,
			@RequestParam("gender") String gender, @RequestParam("phone") String phone) {
		try {
			// Create a new Passenger
			Passenger newPassenger = new Passenger();
			newPassenger.setFirstname(firstname);
			newPassenger.setLastname(lastname);
			newPassenger.setAge(age);
			newPassenger.setGender(gender);
			newPassenger.setPhone(phone);

			passengerService.createPassenger(newPassenger);
			HttpHeaders responseHeaders = new HttpHeaders();
			responseHeaders.setContentType(MediaType.APPLICATION_JSON);

			Map<String, Object> pMap = new HashMap<>();
			pMap.put("id", newPassenger.getId());
			pMap.put("firstname", newPassenger.getFirstname());
			pMap.put("lastname", newPassenger.getLastname());
			pMap.put("age", newPassenger.getAge());
			pMap.put("gender", newPassenger.getGender());
			pMap.put("phone", newPassenger.getPhone());
			Map<String, Object> actPassenger = new HashMap<>();
			actPassenger.put("passenger", pMap);
			JSONObject json = new JSONObject(actPassenger);

			return new ResponseEntity(json.toString(), responseHeaders, HttpStatus.OK);

		}catch (Exception ex) {
			HttpHeaders responseHeaders = new HttpHeaders();
			responseHeaders.setContentType(MediaType.APPLICATION_JSON);
			if(ex.getMessage().contains("ConstraintViolationException")){
				JSONObject json = new JSONObject(getErrorResponse("400", "Phone number already registered for other passenger!. Can not create Passenger."));
				return new ResponseEntity(json.toString(), responseHeaders, HttpStatus.BAD_REQUEST);
			}else{
				JSONObject json = new JSONObject(getErrorResponse("400", ex.getMessage()));
				return new ResponseEntity(json.toString(), responseHeaders, HttpStatus.BAD_REQUEST);
			}
			
		}
	}

	/**
	 * GET PASSENGER - /passenger/{id}?xml=true - METHOD -GET Gets ID as
	 * pathvariable and xml as request param. Returns JSON or XML
	 * 
	 * @param id
	 * @param xml
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/passenger/{id}")
	public ResponseEntity getPassenger(@PathVariable("id") String id,
			@RequestParam(value = "xml", required = false) String xml) {
		try {
			System.out.println("xml get");
			Passenger passenger = passengerService.getPassenger(id);
			if (passenger != null) {
				// Get all his reservations
				List<Reservation> reservations = reservationService.getReservations(passenger);

				JSONObject json = new JSONObject(buildPassengerResponse(passenger, reservations));

				if (xml != null) {
					HttpHeaders responseHeaders = new HttpHeaders();
					responseHeaders.setContentType(MediaType.APPLICATION_XML);
					return new ResponseEntity(XML.toString(json), responseHeaders, HttpStatus.OK);
				} else {
					HttpHeaders responseHeaders = new HttpHeaders();
					responseHeaders.setContentType(MediaType.APPLICATION_JSON);
					return new ResponseEntity(json.toString(), responseHeaders, HttpStatus.OK);
				}
			} else {
				if(xml != null) {
					HttpHeaders responseHeaders = new HttpHeaders();
					responseHeaders.setContentType(MediaType.APPLICATION_XML);
					String error = "Sorry, the requested passenger with id " + id + " does not exist";
					JSONObject json = new JSONObject(getErrorResponse("404", error));
					return new ResponseEntity(XML.toString(json), responseHeaders, HttpStatus.NOT_FOUND);	
				} else {
					HttpHeaders responseHeaders = new HttpHeaders();
					responseHeaders.setContentType(MediaType.APPLICATION_JSON);
					String error = "Sorry, the requested passenger with id " + id + " does not exist";
					JSONObject json = new JSONObject(getErrorResponse("404", error));
					return new ResponseEntity(json.toString(), responseHeaders, HttpStatus.NOT_FOUND);
				}
			}
		} catch (Exception ex) {
			HttpHeaders responseHeaders = new HttpHeaders();
			responseHeaders.setContentType(MediaType.APPLICATION_JSON);
			return new ResponseEntity(getErrorResponse("400", ex.getMessage()), responseHeaders,
					HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * UPDATE PASSENGER - /passenger/{id} - METHOD - PUT
	 * 
	 * @author tungatkarniranjan
	 * @param id
	 * @param firstname
	 * @param lastname
	 * @param age
	 * @param gender
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "passenger/{id}", method = RequestMethod.PUT)
	public ResponseEntity updatePassenger(@PathVariable("id") String id, @RequestParam("firstname") String firstname,
			@RequestParam("lastname") String lastname, @RequestParam("age") int age,
			@RequestParam("gender") String gender, @RequestParam("phone") String phone) {
		try {
			Passenger updPassenger = passengerService.getPassenger(id);
			if (updPassenger != null) {
				List<Reservation> reservations = reservationService.getReservations(updPassenger);
				updPassenger.setId(id);
				updPassenger.setFirstname(firstname);
				updPassenger.setLastname(lastname);
				updPassenger.setAge(age);
				updPassenger.setGender(gender);
				updPassenger.setPhone(phone);
				JSONObject updPassengerJson = new JSONObject(
						buildPassengerResponse(passengerService.updatePassenger(updPassenger), reservations));
				HttpHeaders responseHeaders = new HttpHeaders();
				responseHeaders.setContentType(MediaType.APPLICATION_JSON);
				return new ResponseEntity(updPassengerJson.toString(), responseHeaders, HttpStatus.OK);
			} else {
				HttpHeaders responseHeaders = new HttpHeaders();
				responseHeaders.setContentType(MediaType.APPLICATION_JSON);
				String error = "Passenger with id " + id + " does not exist";
				JSONObject json = new JSONObject(getErrorResponse("404", error));
				return new ResponseEntity(json.toString(), responseHeaders, HttpStatus.NOT_FOUND);
			}
		} catch (Exception ex) {
			HttpHeaders responseHeaders = new HttpHeaders();
			responseHeaders.setContentType(MediaType.APPLICATION_JSON);
			if(ex.getMessage().contains("ConstraintViolationException")){
				JSONObject json = new JSONObject(getErrorResponse("400", "Phone number already registered for other passenger!. Can not update Passenger."));
				return new ResponseEntity(json.toString(), responseHeaders, HttpStatus.BAD_REQUEST);
			}else{
				JSONObject json = new JSONObject(getErrorResponse("400", ex.getMessage()));
				return new ResponseEntity(json.toString(), responseHeaders, HttpStatus.BAD_REQUEST);
			}
		}
	}

	/**
	 * DELETE Passenger - /passenger/{id} - METHOD - DELETE
	 * 
	 * @param id
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/passenger/{id}", method = RequestMethod.DELETE)
	public ResponseEntity deletePassenger(@PathVariable("id") String id) {
		try {
			HttpHeaders responseHeaders = new HttpHeaders();
			responseHeaders.setContentType(MediaType.APPLICATION_JSON);
			if (passengerService.deletePassenger(id)){
				
				HashMap<String, String> succMap = new HashMap<String, String>();
				succMap.put("code", "200");
				succMap.put("msg", "Passenger with " + id + " deleted successfully.");
				HashMap<String, Map> succResponse = new HashMap<String, Map>();
				succResponse.put("Response", succMap);
				JSONObject json = new JSONObject(succResponse);
				return new ResponseEntity(json.toString(), responseHeaders, HttpStatus.OK);
			}
			else {
				JSONObject json = new JSONObject(getErrorResponse("404", "Passenger with " + id + " not found."));
				return new ResponseEntity(json.toString(), responseHeaders, HttpStatus.NOT_FOUND);
			}
		} catch (Exception ex) {
			HttpHeaders responseHeaders = new HttpHeaders();
			responseHeaders.setContentType(MediaType.APPLICATION_JSON);
			JSONObject json = new JSONObject(getErrorResponse("400", ex.getMessage()));
			return new ResponseEntity(json.toString(), responseHeaders, HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Builds a hashmap for bad requests.
	 * 
	 * @param errorcode
	 * @param error
	 * @return HashMap<String, String>
	 */
	@SuppressWarnings("rawtypes")
	public HashMap<String, Map> getErrorResponse(String errorcode, String error) {
		HashMap<String, String> errorMap = new HashMap<String, String>();
		errorMap.put("code", errorcode);
		errorMap.put("msg", error);
		HashMap<String, Map> errorResponse = new HashMap<String, Map>();
		errorResponse.put("Badrequest", errorMap);
		return errorResponse;
	}

	/**
	 * Create JSON reponse from entity
	 * @param passenger
	 * @param reservations
	 * @return
	 */
	// Move to utils
	public Map<String, Object> buildPassengerResponse(Passenger passenger, List<Reservation> reservations) {

		Map<String, Object> response = new LinkedHashMap<>();
		response.put("id", passenger.getId());
		response.put("firstname", passenger.getFirstname());
		response.put("lastname", passenger.getLastname());
		response.put("age", passenger.getAge());
		response.put("gender", passenger.getGender());
		response.put("phone", passenger.getPhone());

		List<Map<String, Object>> reservation = new ArrayList<>();
		SimpleDateFormat target = new SimpleDateFormat("yyyy-MM-dd-HH");
		for (Reservation r : reservations) {
			Map<String, Object> indReservation = new LinkedHashMap<>();
			indReservation.put("orderNumber", r.getOrderNumber());
			indReservation.put("price", r.getPrice());

			List<Map<String, Object>> flight = new ArrayList<>();
			for (Flight f : r.getFlights()) {
				Map<String, Object> indFlight = new LinkedHashMap<>();
				indFlight.put("number", f.getNumber());
				indFlight.put("price", f.getPrice());
				indFlight.put("from", f.getFrom());
				indFlight.put("to", f.getTo());
				indFlight.put("departureTime", target.format(f.getDepartureTime()));
				indFlight.put("arrivalTime", target.format(f.getArrivalTime()));
				indFlight.put("description", f.getDescription());
				indFlight.put("plane", f.getPlane());
				flight.add(indFlight);
			}

			HashMap<String, Object> flMap = new LinkedHashMap<>();
			flMap.put("flight", flight);

			indReservation.put("flights", flMap);
			reservation.add(indReservation);
		}
		HashMap<String, Object> rMap = new LinkedHashMap<>();
		rMap.put("reservation", reservation);
		response.put("reservations", rMap);

		Map<String, Object> Response = new LinkedHashMap<>();
		Response.put("passenger", response);
		return Response;
	}
}
