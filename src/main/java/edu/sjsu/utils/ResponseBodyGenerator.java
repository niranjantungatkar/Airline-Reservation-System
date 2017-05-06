package edu.sjsu.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.stereotype.Service;

import edu.sjsu.models.Flight;
import edu.sjsu.models.Passenger;
import edu.sjsu.models.Reservation;

@Service
public class ResponseBodyGenerator {

	public LinkedHashMap<String, Object> buildMakeReservationResponse(Reservation reservation) {
		LinkedHashMap<String, Object> response = new LinkedHashMap<>();
		SimpleDateFormat target = new SimpleDateFormat("yyyy-MM-dd-HH");

		response.put("orderNumber", reservation.getOrderNumber());
		response.put("price", reservation.getPrice());

		LinkedHashMap<String, Object> passengerMap = new LinkedHashMap<>();
		Passenger passenger = reservation.getPassenger();
		passengerMap.put("id", passenger.getId());
		passengerMap.put("firstname", passenger.getFirstname());
		passengerMap.put("lastname", passenger.getLastname());
		passengerMap.put("age", passenger.getAge());
		passengerMap.put("gender", passenger.getGender());
		passengerMap.put("phone", passenger.getPhone());
		response.put("passenger", passengerMap);

		List<LinkedHashMap<String, Object>> flights = new ArrayList<>();
		for (Flight flight : reservation.getFlights()) {
			LinkedHashMap<String, Object> flightMap = new LinkedHashMap<>();
			flightMap.put("number", flight.getNumber());
			flightMap.put("price", flight.getPrice());
			flightMap.put("from", flight.getFrom());
			flightMap.put("to", flight.getTo());
			flightMap.put("depatureTime", target.format(flight.getDepartureTime()));
			flightMap.put("arrivalTime", target.format(flight.getArrivalTime()));
			flightMap.put("seatsLeft", flight.getSeatsLeft());
			flightMap.put("description", flight.getDescription());
			flightMap.put("plane", flight.getPlane());
			flights.add(flightMap);
		}

		LinkedHashMap<String, Object> flightsMap = new LinkedHashMap<>();
		flightsMap.put("flight", flights);
		response.put("flights", flightsMap);

		LinkedHashMap<String, Object> output = new LinkedHashMap<>();
		output.put("reservation", response);
		return output;
	}

	public LinkedHashMap<String, Object> buildSearchReservationResponse(List<Reservation> reservations) {

		List<LinkedHashMap<String, Object>> output = new ArrayList<>();
		for (Reservation reservation : reservations) {
			output.add(buildMakeReservationResponse(reservation));
		}
		LinkedHashMap<String, Object> outputMap = new LinkedHashMap<>();
		outputMap.put("reservations", output);
		return outputMap;
	}

}
