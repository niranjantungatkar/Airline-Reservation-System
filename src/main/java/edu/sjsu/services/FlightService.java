package edu.sjsu.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.sjsu.dataaccess.FlightDAO;
import edu.sjsu.models.Flight;

@Service
public class FlightService {

	@Autowired
	private FlightDAO flightdao;

	public Flight getFlight(String number) {
		return flightdao.getFlight(number);
	}

	public Flight createFlight(Flight flight) {
		return flightdao.createFlight(flight);
	}

	/*
	 * Returns the list of flights from list of flight numbers
	 */
	public List<Flight> getFlights(String[] flightList) {
		List<Flight> flights = new ArrayList<>();
		for (String flightNumber : flightList) {
			flights.add(flightdao.getFlight(flightNumber));
		}
		return flights;
	}

	public Boolean checkOverlap(String[] flightLists) throws Exception {
		List<Flight> flights = getFlights(flightLists);

		Boolean case1 = null, case2 = null;
		try {

			for (int i = 0; i < flights.size(); i++) {
				Flight flight = flights.get(i);
				for (int j = 0; j < flights.size(); j++) {
					if (i != j) {
						Flight checkFlight = flights.get(j);
						case1 = (flight.getDepartureTime().after(checkFlight.getDepartureTime())
								|| flight.getDepartureTime().equals(checkFlight.getDepartureTime()))
								&& (flight.getDepartureTime().before(checkFlight.getArrivalTime())
										|| flight.getDepartureTime().equals(checkFlight.getArrivalTime()));
						case2 = (flight.getArrivalTime().after(checkFlight.getDepartureTime())
								|| flight.getArrivalTime().equals(checkFlight.getDepartureTime()))
								&& (flight.getArrivalTime().before(checkFlight.getArrivalTime())
										|| flight.getArrivalTime().equals(checkFlight.getArrivalTime()));
						if (case1 || case2) {
							return true;
						}

					}
				}
			}
		} catch (NullPointerException e) {
			throw new Exception("Flight does not exists. Please check again");
		}
		return false;
	}

	public Boolean checkFlightAvailability(String flightNumber) throws Exception {
		Flight flight = flightdao.getFlight(flightNumber);
		if (flight != null) {
			return flight.getSeatsLeft() > 0;
		} else {
			throw new Exception("Flight does not exists. Please check again");
		}

	}

	public Boolean checkFlightsAvailability(String[] flights) throws Exception {

		Boolean result = true;
		for (String flightNumber : flights) {
			result = result && checkFlightAvailability(flightNumber);
		}
		return result;
	}

	public void deleteFlight(Flight flight) {
		flightdao.deleteFlight(flight);
	}

}
