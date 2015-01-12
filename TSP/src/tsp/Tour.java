package tsp;

import java.util.ArrayList;

public class Tour {
	public ArrayList<City> tour;
	public double total_distance = 0;
	
	public Tour(){
		tour = new ArrayList<City>();
	}
	
	public Tour(ArrayList<City> tour){
        this.tour = (ArrayList<City>) tour.clone();
    }
	
	public void addCity(City city){
		tour.add(city);
		total_distance = 0;
	}
	
	public void insertCity(int i, City city){
		tour.set(i, city);
		total_distance = 0;
	}
	
	public double calDistance()
	{
		double distance=0;
		for(int i=0; i<tour.size();i++){
			City city1 = tour.get(i);
			City city2;
			
			if(i+1 < tour.size())
				city2 = tour.get(i+1);
			else
				city2 = tour.get(0);
			distance += city1.distanceTo(city2);
		}
		total_distance=distance;
		return distance;
	}
	
	public String toString(){
		String output="";
		for(int i=0; i< tour.size();i++){
			output += tour.get(i)+" ";
		}
		return output;
	}
}
