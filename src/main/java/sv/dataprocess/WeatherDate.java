package sv.dataprocess;

/**
*  Features Class
*/
public class WeatherDate {
	String temp_cMinusDewPoint = "";
	String wind_speed_kt = "";
	String sky_cover = "";


	public WeatherDate( String wind_speed_kt,String temp_cMinusDewPoint, String sky_cover) {
		super();
		this.temp_cMinusDewPoint = temp_cMinusDewPoint;
		this.wind_speed_kt = wind_speed_kt;
		this.sky_cover = sky_cover;
	}

	public WeatherDate() {

	}

	/**
	*  Getters and Setters
	*/
	public String getTemp_cMinusDewPoint() {
		return temp_cMinusDewPoint;
	}

	public void setTemp_cMinusDewPoint(String temp_cMinusDewPoint) {
		this.temp_cMinusDewPoint = temp_cMinusDewPoint;
	}

	public String getWind_speed_kt() {
		return wind_speed_kt;
	}

	public void setWind_speed_kt(String wind_speed_kt) {
		this.wind_speed_kt = wind_speed_kt;
	}

	public String getSky_cover() {
		return sky_cover;
	}

	public void setSky_cover(String sky_cover) {
		this.sky_cover = sky_cover;
	}

}
