package edu.sjsu.dataaccess;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import edu.sjsu.models.Flight;

@Repository
@Transactional
public class FlightDAO {

	@PersistenceContext
	private EntityManager entityManager;
	  
	/*
	 * Get a Flight 
	 * 
	 */
	@SuppressWarnings("unchecked")
	public Flight getFlight(String number) {
		return entityManager.find(Flight.class, number);
	}
	
	public Flight createFlight(Flight flight) {
		return entityManager.merge(flight);
	}
}
