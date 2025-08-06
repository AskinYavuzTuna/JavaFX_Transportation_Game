package application;


public class Passenger {
	public int numberOfPassenger;
	public int startingCityId;
	public int destinationCityId;
	public String startingCityName;
	public String destinationCityName;
	public Passenger(int numberOfPassenger,int startingCityId,int destinationCityId,String startingCityName,String destinationCityName) {
		this.numberOfPassenger=numberOfPassenger;
		this.startingCityId=startingCityId;
		this.destinationCityId=destinationCityId;
		this.startingCityName=startingCityName;
		this.destinationCityName=destinationCityName;
	}
	public void removePassenger(int a){
		for(;a>0;a--)
		this.numberOfPassenger--;
	}
	public String getstartingCityName(){
		return this.startingCityName;
	}

}
