package tsp;

public class City {
	public int nr;
	public double x;
	public double y;
	
	public City(int nr, double x, double y){
		this.nr=nr;
		this.x=x;
		this.y=y;
	}
	
	public String toString(){
		return this.x+","+this.y;
	}
	
	public double distanceTo(City city){
		double xDist = Math.abs(this.x - city.x);
		double yDist = Math.abs(this.y - city.y);
		double Distance = Math.sqrt((xDist*xDist)+(yDist*yDist));
		return Distance;
	}
}
