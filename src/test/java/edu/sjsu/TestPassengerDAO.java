package edu.sjsu;

import static org.junit.Assert.assertEquals;

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
import edu.sjsu.dataaccess.PassengerDAO;
import edu.sjsu.models.Flight;
import edu.sjsu.models.Passenger;
import edu.sjsu.models.Plane;
import junit.framework.Assert;

@RunWith(SpringRunner.class)
@DataJpaTest
public class TestPassengerDAO {

	@Autowired
	private PassengerDAO passengerdao;
	
	@Test
	public void PassengerSave(){
		Passenger passenger = new Passenger();
		passenger.setFirstname("Mark");
		passenger.setLastname("Walhburg");
		passenger.setGender("Male");
		passenger.setAge(35);
		passenger.setPhone("1234567891");
		passenger.setId("PNT100AS");
		
		Passenger result = passengerdao.getPassenger("PNT100AS");
		assertEquals("Mark", result.getFirstname());	
	} 

	
}
