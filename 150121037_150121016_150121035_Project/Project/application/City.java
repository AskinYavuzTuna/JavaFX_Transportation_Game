package application;

import java.util.ArrayList;
public class City {
	public String cityName;
	public int coordinate;
	public int cityNumber;
	public ArrayList<Passenger> passengers=new ArrayList<Passenger>();
	public ArrayList<Passenger> arrivals=new ArrayList<Passenger>();
	public City(String cityName,int coordinate,int cityNumber) {
		this.cityName=cityName;
		this.coordinate=coordinate;
		this.cityNumber=cityNumber;
		
	}
	
}
