package edu.sjsu.controllers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import edu.sjsu.services.PassengerService;
import edu.sjsu.models.*;

@RestController
public class PassengerController {

	@Autowired
	private PassengerService passengerService;
	
	/** 
	 * @author tungatkarniranjan
	 * @param firstname
	 * @param lastname
	 * @param age
	 * @param gender
	 * @param phone
	 * @return Passenger
	 * 
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
	 * @author tungatkarniranjan
	 * @param id
	 * @param xml
	 * @param json
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/passenger/{id}")
	public ResponseEntity getPassenger(@PathVariable("id") String id,
								  @RequestParam(value = "xml", required = false) String xml,
								  @RequestParam(value = "json", required = false) String json) {
		try {
			Passenger passenger = passengerService.getPassenger(id);
			if(passenger != null) {
				return new ResponseEntity(passenger, HttpStatus.OK);
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
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/passenger/{id}", method = RequestMethod.DELETE)
	public ResponseEntity deletePassenger(@PathVariable("id") String id) {
		try {
			if(passengerService.getPassenger(id) != null) {
				System.out.println("lalalalalalalla");
				passengerService.deletePassenger(id);
				return new ResponseEntity("OK", HttpStatus.OK);
			} else {
				String error = "Passenger with id "+id+" does not exist";
				return new ResponseEntity(getErrorResponse("404", error), HttpStatus.NOT_FOUND);
			}
		} catch(Exception ex) {
			return new ResponseEntity(getErrorResponse("400", ex.getMessage()), HttpStatus.BAD_REQUEST);
		}
	}
	
	@RequestMapping(value="/passengers")
	public List<Passenger> getAll() {
	      return passengerService.getAll();
	}
}
