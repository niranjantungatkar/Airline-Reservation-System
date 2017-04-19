package edu.sjsu.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import edu.sjsu.services.PassengerService;
import edu.sjsu.models.*;

@RestController
public class PassengerController {

	@Autowired
	private PassengerService passengerService;
	
	@RequestMapping(value="/passengers")
	public List<Passenger> getAll() {
	      return passengerService.getAll();
	  }
}
