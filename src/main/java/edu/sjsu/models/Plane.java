package edu.sjsu.models;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Plane {
	
	private int capacity;
    
	@Id
	private String model; 
    
	private String manufacturer;
    
	private int yearOfManufacture;

	public Plane() {
		super();
	}
	
	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	public int getYearOfManufacture() {
		return yearOfManufacture;
	}

	public void setYearOfManufacture(int yearOfManufacture) {
		this.yearOfManufacture = yearOfManufacture;
	}
}
