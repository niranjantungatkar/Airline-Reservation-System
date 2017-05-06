package edu.sjsu.dataaccess;

import java.util.ArrayList;

import java.util.LinkedHashMap;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;
import edu.sjsu.models.Passenger;
import edu.sjsu.models.Reservation;
import edu.sjsu.utils.ReservationNotFoundException;

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
	 * Returns list of reservations from list of reservation number
	 */
	public List<Reservation> getReservations(List<String> ids) {
		List<Reservation> reservations = new ArrayList<>();
		for (String id : ids) {
			Reservation reservation = getReservation(id);
			reservations.add(reservation);
		}
		return reservations;
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
	public List<Reservation> searchReservations(LinkedHashMap<String, String> parameters) throws Exception {

		StringBuilder query = new StringBuilder();
		query.append("select distinct r.orderNumber from Reservation r JOIN r.flights f JOIN r.passenger p");
		boolean firstCame = false;

		if (parameters.containsKey("passengerId")) {
			if (!firstCame) {
				query.append(" where p.id = '" + parameters.get("passengerId") + "'");
				firstCame = true;
			} else {
				query.append(" AND p.id = '" + parameters.get("passengerId") + "'");
			}
		}
		if (parameters.containsKey("from")) {
			if (!firstCame) {
				query.append(" where f.from = '" + parameters.get("from") + "'");
				firstCame = true;
			} else {
				query.append(" AND f.from = '" + parameters.get("from") + "'");
			}
		}
		if (parameters.containsKey("to")) {
			if (!firstCame) {
				query.append(" where f.to = '" + parameters.get("to") + "'");
				firstCame = true;
			} else {
				query.append(" AND f.to = '" + parameters.get("to") + "'");
			}
		}
		if (parameters.containsKey("flightNumber")) {
			if (!firstCame) {
				query.append(" where f.number = '" + parameters.get("flightNumber") + "'");
				firstCame = true;
			} else {
				query.append(" AND f.number = '" + parameters.get("flightNumber") + "'");
			}
		}

		String str = query.toString();
		List<String> reservationIds = entityManager.createQuery(str).getResultList();
		List<Reservation> reservations = getReservations(reservationIds);
		return reservations;
		
	}
}
