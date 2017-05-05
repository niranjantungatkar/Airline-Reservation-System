package edu.sjsu.models;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
public class Flight {
    
	
	@Id
	private String number; 
    
    private int price;
    
    @Column(name = "arrFrom")
    private String from;
    
    @Column(name = "destination")
    private String to;  
    
    @DateTimeFormat(pattern = "yyyy-MM-dd-HH")
    private Date departureTime;     
    
    @DateTimeFormat(pattern = "yy-MM-dd-HH")
    private Date arrivalTime;
    
    private int seatsLeft; 
    
    private String description;
    
    @OneToOne(cascade={CascadeType.MERGE},fetch=FetchType.EAGER)
    private Plane plane;

    @ManyToMany
    private List<Passenger> passengers;
	
	public Flight() {
		super();
	}
    
    public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public Date getDepartureTime() {
		return departureTime;
	}

	public void setDepartureTime(Date departureTime) {
		this.departureTime = departureTime;
	}

	public Date getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(Date arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	public int getSeatsLeft() {
		return seatsLeft;
	}

	public void setSeatsLeft(int seatsLeft) {
		this.seatsLeft = seatsLeft;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Plane getPlane() {
		return plane;
	}

	public void setPlane(Plane plane) {
		this.plane = plane;
	} 
	
	public void setPassenger(List<Passenger> passengers) {
		this.passengers = passengers;
	}
	
	public List<Passenger> getPassengers() {
		return passengers;
	}
}
