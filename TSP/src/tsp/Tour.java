package tsp;

import java.util.ArrayList;
import java.util.Collections;

public class Tour {
	
	public static double annealing(double energy, double newEnergy, double temperature){
		if(newEnergy < energy)
			return 1.0;
		else
			return Math.exp((energy-newEnergy)/temperature);
	}
	
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
	
	public void shuffle(){
		Collections.shuffle(tour);
	}
	
	@SuppressWarnings("unchecked")
	public void simulatedAnnealing(double t, double cR){
		// temperature
		double temp = t;
		
		// cooling rate
		double coolingRate = cR;
		
		
		Tour best = new Tour(this.tour);
		
		while(temp > 1){

			Tour newSolution = new Tour(this.tour);

            // Zufaellige Position
            int tourPos1 = (int) (newSolution.tour.size() * Math.random());
            int tourPos2 = (int) (newSolution.tour.size() * Math.random());

            // Auswaehlen der Staedte
            City citySwap1 = newSolution.tour.get(tourPos1);
            City citySwap2 = newSolution.tour.get(tourPos2);

            // Tauschen
            newSolution.insertCity(tourPos2, citySwap1);
            newSolution.insertCity(tourPos1, citySwap2);
            
            // Kuehlung ermitteln
            double currentEnergy = this.calDistance();
            double neighbourEnergy = newSolution.calDistance();

            // Entscheiden, ob beibehalten
            if (annealing(currentEnergy, neighbourEnergy, temp) > Math.random()) {
            	this.tour = (ArrayList<City>)((newSolution.tour)).clone();
            }

            // Beste Loesung speichern
            if (this.calDistance() < best.calDistance()) {
                best = new Tour(this.tour);
            }
            
            // Abkuehlung
            temp *= 1-coolingRate;
		}
	}
	
	public long runAll(int StartCity, double temperature, double coolingRate, int runs){
		Tour cache = new Tour(this.tour);
		long startTime = System.currentTimeMillis();
		for(int i = 0; i<runs; ++i){
			this.shuffle();
			this.nearestNeighbor(StartCity);
			this.simulatedAnnealing(temperature, coolingRate);
			if(this.calDistance() < cache.calDistance()){
				cache.tour = this.tour;
			}
		}
		this.tour = cache.tour;
		calDistance();
		long endTime = System.currentTimeMillis() - startTime;
		return endTime;
	}
}
