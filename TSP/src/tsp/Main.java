package tsp;

import java.awt.Color;
import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.JPanel;


class Panel extends JPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ArrayList<City> drawCities;
	
	Panel(ArrayList<City> c){
		this.drawCities = c;			
	}
	
	void setArrayList(ArrayList<City> c){
		this.drawCities = c;			
	}

	protected void paintComponent(Graphics g){
		super.paintComponent(g);
		this.setBackground(Color.white);
		
		g.setColor(Color.black);
		City c1 = drawCities.get(0);
//		g.drawRoundRect((int)c1.x, (int)c1.y, 5, 5, 2, 2);
		City cf,ct;
		cf = c1;
		int f = 3;		//factor
		int x1,x2,y1,y2;
		x2 = (int)cf.x/f;
		y2 = (int)cf.y/f;
		g.drawRoundRect(x2, y2, 5, 5, 2, 2);
		g.drawString(c1.nr+"", x2, y2);
		for(int ii=1; ii<drawCities.size();ii++){
			ct = drawCities.get(ii);
			x1 = (int)ct.x/f;
			y1 = (int)ct.y/f;			
			g.drawRoundRect(x1, y1, 5, 5, 2, 2);
			g.drawString(ct.nr+"", x1, y1);
			g.drawLine(x1, y1, x2, y2);
			cf = ct;
			x2 = (int)cf.x/f;
			y2 = (int)cf.y/f;
			if(ii==drawCities.size()-1){
//				System.out.println((c1.x/f)+" "+(c1.y/f)+" "+x2+" "+y2+" "+ii+" "+drawCities.size());
				g.drawLine(x2, y2, (int)c1.x/f, (int)c1.y/f);
				ii++;
			}
		}
	}
}

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
		Tour curr_tour = new Tour();
		try {
			curr_tour = readFile("src/tsp/berlin52.tsp");
// 			tour = readFile("src/tsp/ch130.tsp");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		curr_tour.calDistance();
		System.out.println("Erste Loesung (vor NN): "+curr_tour.total_distance);
		long startTime = System.currentTimeMillis();
		
		int s = curr_tour.tour.size();
		
		double[] nndistance = new double[s];
		double[] annealdistance = new double[s];
		Tour[] alltours = new Tour[s];
		
		for(int i = 0; i < s; ++i){
		System.out.print("Now entering round "+i+" from "+s);	
		long runStartTime = System.currentTimeMillis();
		curr_tour.nearestNeighbor(i);
		nndistance[i] = curr_tour.calDistance();
		
//		for(City c : tour.tour){
//			System.out.println(c);
//		}
		
		//----- SIMULATED ANNEALING ALGORITHM -----
		
		// temperature
		double temp = 100000;
		
		// cooling rate
		double coolingRate = 0.00001;
		
		
		Tour best = new Tour(curr_tour.tour);
		
		while(temp > 1){

			Tour newSolution = new Tour(curr_tour.tour);

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
            double currentEnergy = curr_tour.calDistance();
            double neighbourEnergy = newSolution.calDistance();

            // Entscheiden, ob beibehalten
            if (annealing(currentEnergy, neighbourEnergy, temp) > Math.random()) {
            	curr_tour = new Tour(newSolution.tour);
            }

            // Beste Loesung speichern
            if (curr_tour.calDistance() < best.calDistance()) {
                best = new Tour(curr_tour.tour);
            }
            
            // Abkuehlung
            temp *= 1-coolingRate;
		}
		
		alltours[i] = best;
		annealdistance[i] = best.calDistance();
		long runEndTime   = System.currentTimeMillis();
		System.out.print(" ...finished after "+(runEndTime - runStartTime)+"ms with a final distance of "+annealdistance[i]+"\n");
		}
		
		long endTime   = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		
		int bestanneal = 0;
		int bestnn = 0;
		
		for(int i = 0; i < s; ++i) {
			if(nndistance[i] < nndistance[bestnn]) bestnn = i;
			if(annealdistance[i] < annealdistance[bestanneal]) bestanneal = i;
		}
				
		
		System.out.println("Beste Loesung (Distanz): "+ annealdistance[bestanneal] + " in "+totalTime+" ms"); //7542 is optimal
		System.out.println("Tour: "+alltours[bestanneal]);
		curr_tour.nearestNeighbor(bestnn);
		System.out.println(bestnn+" Debugtest: "+curr_tour.calDistance());
		Arrays.sort(nndistance);
		Arrays.sort(annealdistance);
		System.out.println("All NN-Distances: "+Arrays.toString(nndistance));
		System.out.println("All Annealing-Distances: "+Arrays.toString(annealdistance));
		//Graphische Ausgabe
		JFrame fa = new JFrame("Graph");
		fa.setVisible(true);
		fa.setSize(600, 600);
		fa.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel pa = new Panel(curr_tour.tour);
		fa.add(pa);
	}
}
