package tsp;

import java.util.ArrayList;

public class Tour {
	public ArrayList<City> tour;
	public double total_distance = 0;
	
	public Tour(){
		tour = new ArrayList<City>();
	}
	
	@SuppressWarnings("unchecked")
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
	
	private int[][] getDistanceMatrix(){
		int s = tour.size();
		int[][] dm = new int[s][s];
		for(int i = 0; i<s; ++i){
			for(int j = 0; j<s; ++j){
				dm[i][j] = (int) tour.get(i).distanceTo(tour.get(j));
			}
		}
		return dm;
	}
	
	private int findMin(int[] row, ArrayList<City> old){
        int nextCity = -1;
        int min = Integer.MAX_VALUE;
        
        for(int i = 0; i < row.length; ++i) {
        	if(row[i] < min) {
        		if( !tour.contains(old.get(i)) ){
        			min = row[i];
        			nextCity = i;
                }
            }
        }
        return nextCity;
	}
	
	@SuppressWarnings("unchecked")
	public void nearestNeighbor(int startCity){
		ArrayList<City> old = (ArrayList<City>) tour.clone();
		int[][] distanceMatrix = getDistanceMatrix();
		int currentCity = startCity;
		int nextCity = -1;
		
		tour.clear();
		tour.add(old.get(startCity));
				
		for(int i = 1; i<old.size(); ){
			nextCity = findMin(distanceMatrix[currentCity], old);
			if(nextCity != -1) {
                tour.add(old.get(nextCity));
                currentCity = nextCity;
                i++;
			}
		}
	}
	
}
