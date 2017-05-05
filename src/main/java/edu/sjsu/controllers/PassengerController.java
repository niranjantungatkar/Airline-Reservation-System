package edu.sjsu.controllers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

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
	 * CREATE Passenger - /passenger - METHOD - POST
	 * @author tungatkarniranjan
	 * @param firstname
	 * @param lastname
	 * @param age
	 * @param gender
	 * @param phone
	 * @return Passenger
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value="/passenger", method = RequestMethod.POST)
	public ResponseEntity createPassenger(@RequestParam("firstname") String firstname,
									 @RequestParam("lastname") String lastname,
									 @RequestParam("age") int age,
									 @RequestParam("gender") String gender,
									 @RequestParam("phone") String phone) {
		try {
			//Create a new Passenger
			Passenger newPassenger = new Passenger();
			newPassenger.setFirstname(firstname);
			newPassenger.setLastname(lastname);
			newPassenger.setAge(age);
			newPassenger.setGender(gender);
			newPassenger.setPhone(phone);
			
			passengerService.createPassenger(newPassenger);
			return new ResponseEntity(newPassenger, HttpStatus.OK);
		
		} catch(Exception ex) {
			return new ResponseEntity(getErrorResponse("400",ex.getMessage()), HttpStatus.BAD_REQUEST);	
		}	
	}
	
	
	/**
	 * Builds a hashmap for bad requests.
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
	
	public Map<String, Object> buildPassengerResponse(Passenger passenger, List<Reservation> reservations) {
		
		Map<String, Object> response = new LinkedHashMap<>();
		response.put("id", passenger.getId());
		response.put("firstname", passenger.getFirstname());
		response.put("lastname", passenger.getLastname());
		response.put("age", passenger.getAge());
		response.put("gender", passenger.getGender());
		response.put("phone", passenger.getPhone());
		
		List<Map<String, Object>> reservation = new ArrayList<>();
		for(Reservation r : reservations) {
			Map<String, Object> indReservation = new LinkedHashMap<>();
			indReservation.put("orderNumber", r.getOrderNumber());
			indReservation.put("price", r.getPrice());
			
			List<Map<String, Object>>flight = new ArrayList<>();
			for(Flight f : r.getFlights()) {
				Map<String, Object> indFlight = new LinkedHashMap<>();
				indFlight.put("number", f.getNumber());
				indFlight.put("price", f.getPrice());
				indFlight.put("from", f.getFrom());
				indFlight.put("to", f.getTo());
				indFlight.put("departureTime", f.getDepartureTime());
				indFlight.put("arrivalTime", f.getArrivalTime());
				indFlight.put("description", f.getDescription());
				indFlight.put("plane", f.getPlane());
				//Map<String, Object> indFlMap = new HashMap<>();
				//indFlMap.put("flight", indFlight);
				//flight.add(indFlMap);
				flight.add(indFlight);
			}
			
			HashMap<String, Object> flMap = new LinkedHashMap<>();
			flMap.put("flight", flight);
			
			indReservation.put("flights", flMap);
			//Map<String, Object> indRvMap = new HashMap<>();
			//indRvMap.put("reservation", indReservation);
			reservation.add(indReservation);
		}
		HashMap<String, Object> rMap = new LinkedHashMap<>();
		rMap.put("reservation", reservation);
		
		response.put("reservations",rMap);
		Map<String, Object> testResponse = new LinkedHashMap<>();
		testResponse.put("passenger", response);
		return testResponse;
	}
	
	
	/**
	 * GET Passenger - /passenger/{id}?xml=true - METHOD -GET
	 * Gets ID as pathvariable and xml as request param. Returns JSON
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
			if(passenger != null) {
				//Get all his reservations
				List<Reservation> reservations = reservationService.getReservations(passenger);
				
				JSONObject json = new JSONObject(buildPassengerResponse(passenger, reservations));
			
				if(xml != null)
					return new ResponseEntity(XML.toString(json), HttpStatus.OK);
				else
					return new ResponseEntity(json.toString(), HttpStatus.OK);
				
			}
			else {
				String error = "Sorry, the requested passenger with id "+id+" does not exist";
				return new ResponseEntity(getErrorResponse("404",error), HttpStatus.NOT_FOUND);
			}
		} catch (Exception ex) {
			return new ResponseEntity(getErrorResponse("400", ex.getMessage()), HttpStatus.BAD_REQUEST);
		}
	}
	
	/**
	 * UPDATE Passenger - /passenger/{id} - METHOD - PUT
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
	public ResponseEntity updatePassenger(@PathVariable("id") String id,
									 @RequestParam("firstname") String firstname,
									 @RequestParam("lastname") String lastname,
									 @RequestParam("age") int age,
									 @RequestParam("gender") String gender,
									 @RequestParam("phone") String phone) {
		try{
			System.out.println("PUT");
			if(passengerService.getPassenger(id) != null) {
				Passenger updPassenger = new Passenger();
				updPassenger.setId(id);
				updPassenger.setFirstname(firstname);
				updPassenger.setLastname(lastname);
				updPassenger.setAge(age);
				updPassenger.setGender(gender);
				updPassenger.setPhone(phone);
				return new ResponseEntity(passengerService.updatePassenger(updPassenger), HttpStatus.OK);
			} else {
				String error = "Passenger with id "+id+" does not exist";
				return new ResponseEntity(getErrorResponse("404", error), HttpStatus.NOT_FOUND);
			}
		} catch (Exception ex) {
			return new ResponseEntity(getErrorResponse("400", ex.getMessage()), HttpStatus.BAD_REQUEST);
		}	
	}
	
	/**
	 * DELETE Passenger - /passenger/{id} - METHOD - DELETE
	 * @param id
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/passenger/{id}", method = RequestMethod.DELETE)
	public ResponseEntity deletePassenger(@PathVariable("id") String id) {
		try {
			System.out.println("Delete");
			if(passengerService.deletePassenger(id))
				return new ResponseEntity(getErrorResponse("200", "Passenger with "+id+" deleted successfully."), HttpStatus.OK);
			else
				return new ResponseEntity(getErrorResponse("404", "Passenger with "+id+" not found."), HttpStatus.NOT_FOUND);
			
		} catch(Exception ex) {
			return new ResponseEntity(getErrorResponse("400", ex.getMessage()), HttpStatus.BAD_REQUEST);
		}
	}
	
	@RequestMapping(value="/passengers")
	public List<Passenger> getAll() {
	      return passengerService.getAll();
	}
}
