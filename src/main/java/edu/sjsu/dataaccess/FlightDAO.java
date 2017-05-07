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
	
	/**Creates the flight
	 * 
	 * @param flight
	 * @return
	 */
	public Flight createFlight(Flight flight) {
		return entityManager.merge(flight);
	}
	
	/**
	 * Deletes the flight
	 * @param flight
	 */
	public void deleteFlight(Flight flight) {
		entityManager.remove(flight);
	}
}
