package edu.sjsu.models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;

import org.hibernate.annotations.GenericGenerator;

@Entity
public class Reservation {

	@Id
	@GeneratedValue(generator = "reservationNoGenerator")
	@GenericGenerator(name = "reservationNoGenerator", strategy = "edu.sjsu.utils.ReservationIdGenerator")
	private String orderNumber;

	@OneToOne
	@JoinColumn(name = "passenger")
	private Passenger passenger;

	private int price; // sum of each flight’s price.

	@ManyToMany
	private List<Flight> flights;

	public Reservation() {
		super();
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public Passenger getPassenger() {
		return passenger;
	}

	public void setPassenger(Passenger passenger) {
		this.passenger = passenger;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public List<Flight> getFlights() {
		return flights;
	}

	public void setFlights(List<Flight> flights) {
		this.flights = flights;
	}

}
