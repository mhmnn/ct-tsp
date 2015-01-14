package tsp;

import java.awt.Color;
import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
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
	int display_factor;
	
	Panel(ArrayList<City> c, int f){
		this.drawCities = c;
		this.display_factor = f;
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
		int f = this.display_factor;		//factor berlin 3 ch 2
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
				if(ii == 0 ||ii == 1 || ii == 2 || ii == 3 || ii == 4 || ii == 5 || line.equals("EOF")){
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
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException{
		Tour curr_tour = new Tour();
		int disp_fac=2;
		try {
//			curr_tour = readFile("src/tsp/berlin52.tsp");
//			disp_fac=3;
 			curr_tour = readFile("src/tsp/ch130.tsp");
 			disp_fac=2;		// berlin 3 ch 2
		} catch (IOException e) {
			e.printStackTrace();
		}		
		
		curr_tour.calDistance();
		System.out.println("Erste Loesung (vor NN): "+curr_tour.total_distance);
		// kNN		
		curr_tour.shuffle();
		curr_tour.nearestNeighbor(1);
		System.out.println("Erste Lösung (nach NN): "+curr_tour.calDistance());
		// Protokoll Variablen
		int i=0;
		int best_index=0;
		double best_value=99999999;
		Tour best_tour = null;
		long best_time=0;
		double best_temp=0, best_cooling=0;
		ArrayList<Double> solution_value = new ArrayList<Double>();
		ArrayList<Double> solution_temp = new ArrayList<Double>();
		ArrayList<Double> solution_cooling = new ArrayList<Double>();
		ArrayList<Long> solution_time = new ArrayList<Long>();
		String csv ="";
		//Änderung Temperatur
		for(double temp = 10000; temp>900;temp-=2000){
			//Änderung Cooling Rate
			for(double cR=0.0006; cR>0.000003; cR=cR*0.1){
				long startTime = System.currentTimeMillis();
				curr_tour.simulatedAnnealing(temp, cR);
				solution_value.add(curr_tour.calDistance());
				solution_temp.add(temp);
				solution_cooling.add(cR);
				long endTime   = System.currentTimeMillis();
				long totalTime = endTime - startTime;
				solution_time.add(totalTime);
				if(curr_tour.total_distance < best_value){
					best_value = curr_tour.total_distance;
					best_temp = temp;
					best_time = totalTime;
					best_cooling = cR;
					best_index = i;
					best_tour = new Tour((ArrayList<City>) curr_tour.tour.clone());
				}
				System.out.println("Lösung "+(i+1)+": temp - "+temp+" cooling Rate - "+new DecimalFormat("#.#######").format(cR)+" value - "+curr_tour.total_distance+ " time - "+totalTime+" ms");
				csv += temp+";"+new DecimalFormat("#.#######").format(cR)+";"+new DecimalFormat("#.#######").format(curr_tour.total_distance)+";"+totalTime;
				csv+= "\n";
				i++;
			}
		}
		System.out.println("-----------------");
		//beste: 7542 (berlin) 6110 (ch130)
//		System.out.println("Beste Lösung ("+(best_index+1)+"): "+ solution_value.get(best_index)+ " in "+solution_time.get(best_index)+" ms mit temp: "+solution_temp.get(best_index)+" - cooling Rate: "+new DecimalFormat("#.#####").format(solution_cooling.get(best_index)));
		System.out.println("Beste Lösung: "+ best_value+ " in "+best_time+" ms mit temp: "+best_temp+" - cooling Rate: "+new DecimalFormat("#.#######").format(best_cooling));
		System.out.println("Tour: "+best_tour);
		
		//write File
		PrintWriter writer = new PrintWriter("src/tsp/output.csv", "UTF-8");
		writer.println(csv);
		writer.close();
		
		//Graphische Ausgabe
		JFrame fa = new JFrame("Graph");
		fa.setVisible(true);
		fa.setSize(600, 600);
		fa.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel pa = new Panel(curr_tour.tour, disp_fac);
		fa.add(pa);
	}
}
