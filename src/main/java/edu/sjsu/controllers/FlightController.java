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
import edu.sjsu.models.Plane;
import edu.sjsu.services.FlightService;


@RestController
public class FlightController {

	@Autowired
	private FlightService flightservice;
	
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
		return response;
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
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/flight/{flightNumber}", method = RequestMethod.POST )
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
							   		   @RequestParam("yearOfManufacture") int yearOfManufacture) {
		try {
			
			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH");
			Date depT = formatter.parse(departureTime);	
			Date arrT = formatter.parse(arrivalTime);
			
			Plane plane = new Plane();
			plane.setCapacity(capacity);
			plane.setManufacturer(manufacturer);
			plane.setModel(model);
			plane.setYearOfManufacture(yearOfManufacture);

			Flight flight = new Flight();
			flight.setNumber(flightnumber);
			flight.setPrice(price);
			flight.setFrom(from);
			flight.setTo(to);
			flight.setSeatsLeft(capacity);
			flight.setDepartureTime(depT);
			flight.setArrivalTime(arrT);
			flight.setDescription(description);
			flight.setPlane(plane);
			
			return new ResponseEntity(buildFlightResponse(flightservice.createFlight(flight)), HttpStatus.OK);
			
		} catch(Exception ex) {
			ex.printStackTrace();
			return new ResponseEntity(getErrorResponse("400", ex.getMessage()),HttpStatus.BAD_REQUEST);
		}
		
	}
	
	/**GET Flight
	 * @author tungatkarniranjan
	 * @param number
	 * @param xml
	 * @param json
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value="/flight/{number}")
	public ResponseEntity getFlight(@PathVariable("number") String number, 
							@RequestParam(value="xml",required = false) String xml, 
							@RequestParam(value="json", required = false) String json) {
		try {
			if(flightservice.getFlight(number) != null) {
				return new ResponseEntity(buildFlightResponse(flightservice.getFlight(number)), HttpStatus.OK);
			} else {
				return new ResponseEntity(getErrorResponse("404", "Flight with number "+number+" not found"), HttpStatus.NOT_FOUND);
			}
			
		} catch(Exception ex) {
			return new ResponseEntity(getErrorResponse("400", ex.getMessage()), HttpStatus.BAD_REQUEST);
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
					return new ResponseEntity(getErrorResponse("200", "Flight with number "+number+" deleted successfully."), HttpStatus.OK);
				} else {
					return new ResponseEntity(getErrorResponse("400", "Flight with number "+number+" cannot be deleted. It has one or more reservations"), HttpStatus.BAD_REQUEST);
				}		
			} else {
				return new ResponseEntity(getErrorResponse("404", "Flight with number "+number+" not found"), HttpStatus.NOT_FOUND);
			}		
		} catch (Exception ex) {
			return new ResponseEntity(getErrorResponse("400", ex.getMessage()), HttpStatus.BAD_REQUEST);
		}
	}	
}
