package tsp;

import java.awt.Color;
import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

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
		
		curr_tour.nearestNeighbor(8);
		curr_tour.calDistance();
		System.out.println("Erste Loesung (nach NN): "+curr_tour.total_distance);
		
//		for(City c : tour.tour){
//			System.out.println(c);
//		}
		
		//----- SIMULATED ANNEALING ALGORITHM -----
		
		// temperature
		double temp = 100000;
		
		// cooling rate
		double coolingRate = 0.000003;
		
		
		Tour best = new Tour(curr_tour.tour);
		
		while(temp > 1){
			Tour newSolution = new Tour(curr_tour.tour);

            // Zufällige Position
            int tourPos1 = (int) (newSolution.tour.size() * Math.random());
            int tourPos2 = (int) (newSolution.tour.size() * Math.random());

            // Auswählen der Städte
            City citySwap1 = newSolution.tour.get(tourPos1);
            City citySwap2 = newSolution.tour.get(tourPos2);

            // Tauschen
            newSolution.insertCity(tourPos2, citySwap1);
            newSolution.insertCity(tourPos1, citySwap2);
            
            // Kühlung ermitteln
            double currentEnergy = curr_tour.calDistance();
            double neighbourEnergy = newSolution.calDistance();

            // Entscheiden, ob beibehalten
            if (annealing(currentEnergy, neighbourEnergy, temp) > Math.random()) {
            	curr_tour = new Tour(newSolution.tour);
            }

            // Beste Lösung speichern
            if (curr_tour.calDistance() < best.calDistance()) {
                best = new Tour(curr_tour.tour);
            }
            
            // Abkühlung
            temp *= 1-coolingRate;
		}
		System.out.println("Beste Loesung (Distanz): "+ best.calDistance());
		System.out.println("Tour: "+best);
		
		//Graphische Ausgabe
		JFrame fa = new JFrame("Graph");
		fa.setVisible(true);
		fa.setSize(600, 600);
		fa.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel pa = new Panel(best.tour);
		fa.add(pa);
	}
}
