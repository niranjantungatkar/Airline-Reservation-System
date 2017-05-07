package edu.sjsu.controllers;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

import edu.sjsu.models.Flight;
import edu.sjsu.models.Passenger;
import edu.sjsu.models.Plane;
import edu.sjsu.models.Reservation;
import edu.sjsu.services.FlightService;
import edu.sjsu.services.ReservationService;


@RestController
public class FlightController {

	@Autowired
	private FlightService flightservice;
	
	@Autowired
	private ReservationService reservationService;
	
	

	/**
	 * CREATE OR UPDATE FLIGHT
	 * 
	 * @param flightnumber
	 * @param price
	 * @param from
	 * @param to
	 * @param departureTime
	 * @param description
	 * @param capacity
	 * @param model
	 * @param manufacturer
	 * @param arrivalTime
	 * @param yearOfManufacture
	 * @return
	 * @throws JSONException 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/flight/{flightNumber}", method = RequestMethod.POST)
	public ResponseEntity createFlight(@PathVariable("flightNumber") String flightnumber,
							   		   @RequestParam("price") int price,
							   		   @RequestParam("from") String from,
							   		   @RequestParam("to") String to,
							   		   @RequestParam("departureTime") String departureTime,
							   		   @RequestParam("description") String description,
							   		   @RequestParam("capacity") int capacity,
							   		   @RequestParam("model") String model,
							   		   @RequestParam("manufacturer") String manufacturer,
							   		   @RequestParam("arrivalTime") String arrivalTime,
							   		   @RequestParam("yearOfManufacture") int yearOfManufacture) throws JSONException {
		try {
			Flight updFlight = flightservice.getFlight(flightnumber); 
			if( updFlight != null ) {
				List<Passenger> passengers = updFlight.getPassengers();
				if(updFlight.getPlane().getCapacity() - updFlight.getSeatsLeft() > capacity) {
					JSONObject json = new JSONObject(getErrorResponse("400", "Reservation count greater than capacity"));
					return new ResponseEntity(XML.toString(json), getXMLHeaders(), HttpStatus.BAD_REQUEST);
				} else {
					
					DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH");
					Date depT = formatter.parse(departureTime);	
					Date arrT = formatter.parse(arrivalTime);
					
					if( checkOverlap(passengers, flightnumber, depT, arrT)) {
						JSONObject json = new JSONObject(getErrorResponse("400", "Passenger flights overlap! Can't  change the Flight arrival Departure time"));
						return new ResponseEntity(XML.toString(json), getXMLHeaders(), HttpStatus.BAD_REQUEST);
					} else {
						updFlight.setSeatsLeft(capacity - ( updFlight.getPlane().getCapacity() - updFlight.getSeatsLeft() ) );
						
						Plane plane = new Plane();
						plane.setCapacity(capacity);
						plane.setManufacturer(manufacturer);
						plane.setModel(model);
						plane.setYearOfManufacture(yearOfManufacture);
						updFlight.setNumber(flightnumber);
						updFlight.setPrice(price);
						updFlight.setFrom(from);
						updFlight.setTo(to);
						updFlight.setDepartureTime(depT);
						updFlight.setArrivalTime(arrT);
						updFlight.setDescription(description);
						updFlight.setPlane(plane);
						
						JSONObject responseFlight = new JSONObject(buildFlightResponse(flightservice.createFlight(updFlight)));
						return new ResponseEntity(XML.toString(responseFlight), getXMLHeaders(), HttpStatus.OK);
					}
				}
			} else {
				Flight flight = new Flight();
				DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH");
				Date depT = formatter.parse(departureTime);	
				Date arrT = formatter.parse(arrivalTime);
				
				Plane plane = new Plane();
				plane.setCapacity(capacity);
				plane.setManufacturer(manufacturer);
				plane.setModel(model);
				plane.setYearOfManufacture(yearOfManufacture);
				
				flight.setNumber(flightnumber);
				flight.setPrice(price);
				flight.setFrom(from);
				flight.setTo(to);
				flight.setSeatsLeft(capacity);
				flight.setDepartureTime(depT);
				flight.setArrivalTime(arrT);
				flight.setDescription(description);
				flight.setPlane(plane);
				
				JSONObject responseFlight = new JSONObject(buildFlightResponse(flightservice.createFlight(flight)));
				return new ResponseEntity(XML.toString(responseFlight), getXMLHeaders(), HttpStatus.OK);
			}
		} catch(Exception ex) {
			JSONObject json = new JSONObject(getErrorResponse("400", ex.getMessage()));
			return new ResponseEntity(XML.toString(json), getXMLHeaders(), HttpStatus.BAD_REQUEST);
		}
		
	}
	
	/**GET FLIGHT
	 * @author tungatkarniranjan
	 * @param number
	 * @param xml
	 * @param json
	 * @return
	 * @throws JSONException 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value="/flight/{number}")
	public ResponseEntity getFlight(@PathVariable("number") String number, 
							        @RequestParam(value="xml",required = false) String xml) throws JSONException {
		try {
			if(flightservice.getFlight(number) != null) {
				JSONObject flight = new JSONObject(buildFlightResponse(flightservice.getFlight(number)));
				if(xml != null) {
					return new ResponseEntity(XML.toString(flight), getXMLHeaders(), HttpStatus.OK);
				} else {
					return new ResponseEntity(flight.toString(), getJSONHeaders(), HttpStatus.OK);
				}	
			} else {
				JSONObject json = new JSONObject(getErrorResponse("404", "Flight with number "+number+" not found"));
				if(xml != null) {
					return new ResponseEntity(XML.toString(json), getXMLHeaders(), HttpStatus.NOT_FOUND);
				} else {
					return new ResponseEntity(json.toString(), getJSONHeaders(), HttpStatus.NOT_FOUND);
				}
			}
		} catch(Exception ex) {
			JSONObject json = new JSONObject(getErrorResponse("400", ex.getMessage()));
			if(xml != null) {
				return new ResponseEntity(XML.toString(json), getXMLHeaders(), HttpStatus.BAD_REQUEST);
			} else {
				return new ResponseEntity(json.toString(), getJSONHeaders(), HttpStatus.BAD_REQUEST);
			}
		}
	}
	
	
	/**
	 * DELETE Flight
	 * @param number
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value="/flight/{number}", method = RequestMethod.DELETE)
	public ResponseEntity deleteFlight(@PathVariable("number") String number) {
		try {
			Flight flight = flightservice.getFlight(number);
			if(flight != null) {
				List<Passenger> passengers = flight.getPassengers();
				if(passengers == null || passengers.size() == 0) {
					flightservice.deleteFlight(flight);
					JSONObject json = new JSONObject();
					json.put("code", 200);
					json.put("msg", "Flight with number "+number+" deleted successfully");
					return new ResponseEntity(json.toString(),getJSONHeaders(),HttpStatus.OK);
				} else {
					JSONObject json = new JSONObject(getErrorResponse("400", "Flight with number "+number+" cannot be deleted. It has one or more reservations"));
					return new ResponseEntity(json.toString(), getJSONHeaders(), HttpStatus.BAD_REQUEST);
				}		
			} else {
				JSONObject json = new JSONObject(getErrorResponse("404", "Flight with number "+number+" not found"));
				return new ResponseEntity(json.toString(), getJSONHeaders(), HttpStatus.NOT_FOUND);
			}		
		} catch (Exception ex) {
			JSONObject json = new JSONObject(getErrorResponse("400", ex.getMessage()));
			return new ResponseEntity( json.toString(), getJSONHeaders(), HttpStatus.BAD_REQUEST);
		}
	}
	
	
	public Map<String, Object> buildFlightResponse(Flight flight) throws ParseException {
		
		SimpleDateFormat target = new SimpleDateFormat("yyyy-MM-dd-HH");
		Map<String, Object> response = new LinkedHashMap<>();
		
		response.put("flightNumber", flight.getNumber());
		response.put("price", flight.getPrice() );
		response.put("from", flight.getFrom());
		response.put("to", flight.getTo());
		response.put("departureTime", target.format(flight.getDepartureTime()));
		response.put("arrivalTime", target.format(flight.getArrivalTime()));
		response.put("description", flight.getDescription());
		response.put("seatsLeft", flight.getSeatsLeft());
		response.put("plane",flight.getPlane());
		
		if(flight.getPassengers() != null && flight.getPassengers().size() > 0) {
			List<Map<String, Object>> passengers = new ArrayList<>();
			for(Passenger p : flight.getPassengers()) {
				Map<String, Object> indPassenger = new LinkedHashMap<>();
				indPassenger.put("id", p.getId());
				indPassenger.put("firstname", p.getFirstname());
				indPassenger.put("lastname", p.getLastname());
				indPassenger.put("age", p.getAge());
				indPassenger.put("gender", p.getGender());
				indPassenger.put("phone", p.getPhone());
				passengers.add(indPassenger);	
			}
			HashMap<String, Object> pMap = new LinkedHashMap<>();
			pMap.put("passenger", passengers);
			response.put("passengers",pMap);
		}
		Map<String, Object> Response = new LinkedHashMap<>();
		Response.put("flight", response);
		return Response;
	}
	
	/**
	 * Get Error response in Map
	 * @param errorcode
	 * @param error
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Map<String, Map> getErrorResponse(String errorcode, String error) {
		HashMap<String, String> errorMap = new HashMap<String, String>();
		errorMap.put("code", errorcode);
		errorMap.put("msg", error);
		HashMap<String, Map> errorResponse = new HashMap<String, Map>();
		errorResponse.put("Badrequest", errorMap);
		return errorResponse;
	}	
	
	/**
	 * Check overlap in the flights timeings
	 * @param passengers
	 * @param flightnumber
	 * @param depT
	 * @param arrT
	 * @return
	 */
	public boolean checkOverlap(List<Passenger> passengers, String flightnumber, Date depT, Date arrT) {
		
		for(Passenger p : passengers) {
			List<Reservation> reservations = reservationService.getReservations(p);
			for(Reservation r : reservations) {
				List<Flight> flights = r.getFlights();
				for(Flight f : flights) {
					if(f.getNumber() == flightnumber)
						continue;
					else {
						boolean case1 = (depT.after(f.getDepartureTime())
								|| depT.equals(f.getDepartureTime()))
								&& (depT.before(f.getArrivalTime())
										|| depT.equals(f.getArrivalTime()));
						boolean case2 = (arrT.after(f.getDepartureTime())
								|| arrT.equals(f.getDepartureTime()))
								&& (arrT.before(f.getArrivalTime())
										|| arrT.equals(f.getArrivalTime()));
						if (case1 || case2) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	public HttpHeaders getXMLHeaders() {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setContentType(MediaType.APPLICATION_XML);
		return responseHeaders;
	}
	
	public HttpHeaders getJSONHeaders() {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setContentType(MediaType.APPLICATION_JSON);
		return responseHeaders;
	}
	
	public JSONObject createJSONObject(Map<Object, Object> map) {
		return new JSONObject(map);
	}
}
