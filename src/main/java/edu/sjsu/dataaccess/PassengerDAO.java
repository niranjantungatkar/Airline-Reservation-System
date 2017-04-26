package edu.sjsu.dataaccess;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import edu.sjsu.models.Passenger;

@Repository
@Transactional
public class PassengerDAO {

	  @PersistenceContext
	  private EntityManager entityManager;
	  
	  @SuppressWarnings("unchecked")
	  public List<Passenger> getAll() {
	    return entityManager.createQuery("from Passenger").getResultList();
	  }
	  
	  public void createPassenger(Passenger newPassenger) {
		  entityManager.persist(newPassenger);
	  }
	  
	  public Passenger getPassenger(String id) {
		  return entityManager.find(Passenger.class, id);
	  }
	  
	  public Passenger updatePassenger(Passenger updPassenger) {
		  return entityManager.merge(updPassenger);
	  }
	  
}
