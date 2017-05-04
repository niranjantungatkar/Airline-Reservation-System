package edu.sjsu.dataaccess;

import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;
import edu.sjsu.models.Passenger;
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

	@SuppressWarnings("unchecked")
	public List<Reservation> getReservations(Passenger delPassenger) {
		List<Reservation> reservations = entityManager
				.createQuery("Select r from Reservation r where r.passenger = :pid").setParameter("pid", delPassenger)
				.getResultList();
		return reservations;
	}

	@SuppressWarnings("unchecked")
	public List<Reservation> getReservationPassengerNotReservationId(Passenger passenger, String number) {
		List<Reservation> reservations = entityManager
				.createQuery("Select r from Reservation r where r.passenger = :pid AND r.orderNumber != :number")
				.setParameter("pid", passenger).setParameter("number", number).getResultList();
		return reservations;
	}

	@SuppressWarnings("unchecked")
	public void searchReservations(HashMap<String, String> parameters) throws Exception {

		List<Reservation> reservations = entityManager
				.createQuery("select r from Reservation r JOIN r.flights f JOIN r.passenger p").getResultList();
		if(reservations.size() == 0){
			throw new Exception("No reservations found for given criteria");
		}
		System.out.println(reservations.size());

	}
}
