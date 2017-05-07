package edu.sjsu;

import static org.junit.Assert.*;

import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import edu.sjsu.dataaccess.FlightDAO;
import edu.sjsu.models.Flight;
import edu.sjsu.models.Passenger;
import edu.sjsu.models.Plane;
import junit.framework.Assert;

@RunWith(SpringRunner.class)
@DataJpaTest
public class TestFlightDAO {

	@Autowired
	private FlightDAO flightDAO;

	@Test
	public void flightSave() {

		String departureDate = "2016-02-10-09";
		String arrivalDate = "2016-02-10-16";
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH");
		Date depart = null;
		Date arrival = null;
		try {
			depart = (Date) format.parse(departureDate);
			arrival = (Date) format.parse(arrivalDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		Plane plane = new Plane();
		plane.setCapacity(10);
		plane.setManufacturer("Boeing");
		plane.setModel("747-300ER");
		plane.setYearOfManufacture(1992);

		Passenger passenger = new Passenger();
		passenger.setAge(25);
		passenger.setFirstname("Mark");
		passenger.setLastname("Walhburg");
		passenger.setGender("Male");
		passenger.setPhone("1234567891");
		List<Passenger> passengers = new ArrayList<>();
		passengers.add(passenger);

		Flight flight = new Flight();
		flight.setNumber("A001");
		flight.setPrice(120);
		flight.setFrom("SJC");
		flight.setTo("NYC");
		flight.setDepartureTime(depart);
		flight.setArrivalTime(arrival);
		flight.setSeatsLeft(5);
		flight.setDescription("From San Jose to New York");
		flight.setPlane(plane);
		flight.setPassenger(passengers);

		Flight result = flightDAO.createFlight(flight);
		Assert.assertEquals("A001", result.getNumber());

	}
}
