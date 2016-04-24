package sv.entities;

public class GraphData {

	private String period;
	private String hexNum;
	private double decNum;
	
	public GraphData(String period, String hexNum, double decNum){
		this.period = period;
		this.hexNum = hexNum;
		this.decNum = decNum;
	}
	
	public String getPeriod(){
		return period;
	}
	
	public String getHexNum(){
		return hexNum;
	}
	
	public double getDecNum(){
		return decNum;
	}
}
