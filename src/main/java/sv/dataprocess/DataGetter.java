package sv.dataprocess;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.dvdme.ForecastIOLib.FIOCurrently;
import com.github.dvdme.ForecastIOLib.FIODataPoint;
import com.github.dvdme.ForecastIOLib.FIOHourly;
import com.github.dvdme.ForecastIOLib.ForecastIO;

public class DataGetter {

	public static void main(String[] args) {

		try {
			// longitu, latitud, offsetdia, offsethora
			//WeatherDate weather = getTimeWheater("41.5", "0.5", 0, 0);

		} catch (Exception a) {
		}
	}

	/*
	 * pasan la hora del lugar
	 */
	public static WeatherDate getTimeWheater(String lon, String lat, int offsetdia, int offsethour) throws Exception {
		// Gets the hour offset

		// offSetDays,int hour,int minutes
		ForecastIO fio = new ForecastIO("3e9f39acff087ff700454b625f1f59d3");
		fio.setUnits(ForecastIO.UNITS_SI);

		fio.setLang(ForecastIO.LANG_ENGLISH);
		// get forecast
		fio.getForecast(lat, lon);

		if (offsetdia >= 1) {
			offsethour += offsetdia * 24;
		}
		String temp_c = "";//
		String dewpoint_c = "";//
		String wind_speed_kt = "";//

		if (offsethour == 0) {
			// tiempo actual
			FIOCurrently currently = new FIOCurrently(fio);
			// Print currently data
			dewpoint_c = String.valueOf(currently.get().dewPoint());
			temp_c = String.valueOf(currently.get().temperature());
			wind_speed_kt = String.valueOf(currently.get().windSpeed());
			String cloud = String.valueOf(currently.get().cloudCover());
			//// rociio-temp
			Double temp_roc=Double.valueOf(temp_c)-Double.valueOf(dewpoint_c);
			return new WeatherDate(wind_speed_kt,String.valueOf(temp_roc), cloud);

		} else {
			// offsethour y la hora 1, seria i=1 (el cero es la acutal)
			FIOHourly hourly = new FIOHourly(fio);
			// In case there is no hourly data available
			if (hourly.hours() < offsethour)
				offsethour = hourly.hours() - 1;
			FIODataPoint currently = hourly.getHour(offsethour);
			dewpoint_c = String.valueOf(currently.dewPoint());
			temp_c = String.valueOf(currently.temperature());
			wind_speed_kt = String.valueOf(currently.windSpeed());
			String cloud = String.valueOf(currently.cloudCover());
			Double temp_roc=Double.valueOf(temp_c)-Double.valueOf(dewpoint_c);

			return new WeatherDate(wind_speed_kt,String.valueOf(temp_roc), cloud);
		}

	}
}
