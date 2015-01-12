package tsp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Main {

	public static double distanceBtw(City city1, City city2){
		double xDist = Math.abs(city1.x - city2.x);
		double yDist = Math.abs(city1.y - city2.y);
		double Distance = Math.sqrt((xDist*xDist)+(yDist*yDist));
		return Distance;
	}
	
	public static Tour readFile(String path) throws IOException{
		Tour newtour = new Tour();
		BufferedReader br = new BufferedReader(new FileReader(path));
		try{
			String line = br.readLine();
			int ii=0;
			while(line != null){
				if(ii == 0 ||ii == 1 || ii == 2 || ii == 3 || ii == 4 || ii == 5 || ii == 58){
					;
				}
				else{
					String[] infos = line.split(" ");
					City c = new City(Integer.parseInt(infos[0]), Double.parseDouble(infos[1]), Double.parseDouble(infos[2]));
					newtour.addCity(c);
				}
				line = br.readLine();
				ii++;
			}
		}
		finally{
			br.close();
		}
		
		return newtour;
	}
	
	public static double annealing(double energy, double newEnergy, double temperature){
		if(newEnergy < energy)
			return 1.0;
		else
			return Math.exp((energy-newEnergy)/temperature);
	}
	
	public static void main(String[] args){
		Tour tour = new Tour();
		try {
			tour = readFile("src/tsp/berlin52.tsp");
//			tour = readFile("ch130.tsp");
		} catch (IOException e) {
			e.printStackTrace();
		}
		tour.calDistance();
		
//		for(City c : tour.tour){
//			System.out.println(c);
//		}
		
		//----- SIMULATED ANNEALING ALGORITHM -----
		
		// temperature
		double temp = 10000;
		
		// cooling rate
		double coolingRate = 0.003;
		
		System.out.println("Erste Lösung: "+tour.total_distance);
		
		Tour best = new Tour(tour.tour);
		
		while(temp > 1){
			Tour newSolution = new Tour(tour.tour);

            // Get a random positions in the tour
            int tourPos1 = (int) (newSolution.tour.size() * Math.random());
            int tourPos2 = (int) (newSolution.tour.size() * Math.random());

            // Get the cities at selected positions in the tour
            City citySwap1 = newSolution.tour.get(tourPos1);
            City citySwap2 = newSolution.tour.get(tourPos2);

            // Swap them
            newSolution.insertCity(tourPos2, citySwap1);
            newSolution.insertCity(tourPos1, citySwap2);
            
            // Get energy of solutions
            double currentEnergy = tour.calDistance();
            double neighbourEnergy = tour.calDistance();

            // Decide if we should accept the neighbour
            if (annealing(currentEnergy, neighbourEnergy, temp) > Math.random()) {
                tour = new Tour(newSolution.tour);
            }

            // Keep track of the best solution found
            if (tour.calDistance() < best.calDistance()) {
                best = new Tour(tour.tour);
            }
            
            // Cool system
            temp *= 1-coolingRate;
		}
		System.out.println("Beste Lösung (Distanz): "+ best.calDistance());
		System.out.println("Tour: "+best);
	}
}
