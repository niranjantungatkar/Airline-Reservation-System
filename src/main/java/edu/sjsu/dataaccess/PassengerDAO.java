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
	  
	  /**
	   * Creates the Passenger 
	   * @param newPassenger
	   */
	  public void createPassenger(Passenger newPassenger) {
		  entityManager.persist(newPassenger);
	  }
	  
	  /**
	   * Retrieve the Passsenger
	   * @param id
	   * @return pasenger object
	   */
	  public Passenger getPassenger(String id) {
		  return entityManager.find(Passenger.class, id);
	  }
	  
	  public Passenger updatePassenger(Passenger updPassenger) {
		  return entityManager.merge(updPassenger);
	  }
	  
	  public void deletePassenger(Passenger delPassenger) {
		  entityManager.remove(delPassenger);
	  }
	  
}
