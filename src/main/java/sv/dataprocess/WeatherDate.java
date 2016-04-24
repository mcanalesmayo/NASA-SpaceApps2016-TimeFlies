package sv.dataprocess;

public class WeatherDate {
	String temp_c = "";
	String dewpoint_c = "";
	String wind_dir_degrees = "";
	String wind_speed_kt = "";
//	String visibility_statute_mi = "";
	String sky_cover = "";

	public WeatherDate(String temp_c, String dewpoint_c, String wind_dir_degrees, String wind_speed_kt,
			/*String visibility_statute_mi,*/ String sky_cover) {
		super();
		this.temp_c = temp_c;
		this.dewpoint_c = dewpoint_c;
		this.wind_dir_degrees = wind_dir_degrees;
		this.wind_speed_kt = wind_speed_kt;
	/*	this.visibility_statute_mi = visibility_statute_mi;*/
		this.sky_cover = sky_cover;
	}

	public WeatherDate() {

	}

	public String getTemp_c() {
		return temp_c;
	}

	public void setTemp_c(String temp_c) {
		this.temp_c = temp_c;
	}

	public String getDewpoint_c() {
		return dewpoint_c;
	}

	public void setDewpoint_c(String dewpoint_c) {
		this.dewpoint_c = dewpoint_c;
	}

	public String getWind_dir_degrees() {
		return wind_dir_degrees;
	}

	public void setWind_dir_degrees(String wind_dir_degrees) {
		this.wind_dir_degrees = wind_dir_degrees;
	}

	public String getWind_speed_kt() {
		return wind_speed_kt;
	}

	public void setWind_speed_kt(String wind_speed_kt) {
		this.wind_speed_kt = wind_speed_kt;
	}
/*
	public String getVisibility_statute_mi() {
		return visibility_statute_mi;
	}

	public void setVisibility_statute_mi(String visibility_statute_mi) {
		this.visibility_statute_mi = visibility_statute_mi;
	}
*/

	public String getSky_cover() {
		return sky_cover;
	}

	public void setSky_cover(String sky_cover) {
		this.sky_cover = sky_cover;
	}


}
