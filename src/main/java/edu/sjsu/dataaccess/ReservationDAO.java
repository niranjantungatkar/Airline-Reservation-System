package edu.sjsu.dataaccess;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import edu.sjsu.models.Flight;
import edu.sjsu.models.Reservation;

@Repository
@Transactional
public class ReservationDAO {
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@SuppressWarnings("unchecked")
	public boolean createReservation(Reservation reservation) {
		entityManager.persist(reservation);
		return true;
	}

	public Reservation getReservation(String orderNumber) {
		return entityManager.find(Reservation.class, orderNumber);
	}
	
	/*
	 * Note : this method is used by passengerService while deleting a passenger
	 */
	public void deleteReservation(Reservation reservation) {
		entityManager.remove(reservation);
	}
}
