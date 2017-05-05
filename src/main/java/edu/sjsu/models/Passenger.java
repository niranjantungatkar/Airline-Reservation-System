package edu.sjsu.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "Passenger", uniqueConstraints = { @UniqueConstraint(columnNames = { "phone" }) })
public class Passenger {

	@Id
	@GeneratedValue(generator = "passengerNoGenerator")
	@GenericGenerator(name = "passengerNoGenerator", strategy = "edu.sjsu.utils.PassengerIdGenerator")
	private String id;

	private String firstname;
	private String lastname;
	private int age;
	private String gender;
	private String phone;

	public Passenger() {
		super();
	}

	public Passenger(String id, String firstname, String lastname, int age, String gender, String phone) {
		this.id = id;
		this.firstname = firstname;
		this.lastname = lastname;
		this.age = age;
		this.gender = gender;
		this.phone = phone;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	/*
	 * public List<Reservation> getReservations() { return reservations; }
	 * 
	 * public void setReservations(List<Reservation> reservations) {
	 * this.reservations = reservations; }
	 */
}
