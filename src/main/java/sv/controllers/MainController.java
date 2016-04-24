package sv.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import sv.dataprocess.DataGetter;
import sv.dataprocess.WeatherDate;
import sv.entities.GraphData;

@Controller
@PropertySource(value = { "classpath:network.properties" })
public class MainController {
	
	private static final Logger logger = LoggerFactory.getLogger(MainController.class);
	
	@Value("${predictor.host}")
	public String PREDICTOR_HOST;
	@Value("${predictor.port}")
	public int PREDICTOR_PORT;
	
	private static final String PREDICT_COMMAND = "PR";
	private static final String FEED_COMMAND = "FE";
	private static final long FEED_FREQ = 3;
	private static final String PREDICTOR_RESPONSE_OK = "OK";
	private static final String PREDICTOR_RESPONSE_ERR = "ERR";
	
	private static final String[] PREDICTION_COLOR = { "#33cc33", "#ffff00", "#ff9933", "#ff5050"};
	private static final String[] PREDICTION_PERIOD = { "0-15 min", "15-30 min", "30-60 min", ">60 min"};
	
	/**
	 * Predicts the delay of a flight
	 * @param city City where the person is taking the flight
	 * @param date Date of the flight
	 * @throws Exception 
	 */
	@RequestMapping(value = "/predict", method = RequestMethod.GET)
	public ResponseEntity<?> predict(@RequestParam(value = "lat", required = true) double lat,
			@RequestParam(value = "lon", required = true) double lon,
			@RequestParam(value = "offsetH", required = true) int offsetH,
			@RequestParam(value = "offsetD", required = true) int offsetD) throws Exception{
		try {
			Socket predictorSocket = new Socket(PREDICTOR_HOST, PREDICTOR_PORT);
			logger.info("Connected to the predictor");
			PrintWriter output = new PrintWriter(predictorSocket.getOutputStream(), false);
			BufferedReader input = new BufferedReader(new InputStreamReader(predictorSocket.getInputStream()));
			
			WeatherDate weatherDate = DataGetter.getTimeWheater(""+lon, ""+lat, offsetD, offsetH);
			
			logger.info(fromDoubleToString(Double.parseDouble(weatherDate.getWind_speed_kt())));
			logger.info(fromDoubleToString(Double.parseDouble(weatherDate.getTemp_cMinusDewPoint())));
			logger.info(fromDoubleToString(Double.parseDouble(weatherDate.getSky_cover())));
			
			// Command
			output.printf("%s", MainController.PREDICT_COMMAND);
			output.flush();
			// Data
			output.printf("%s", fromDoubleToString(Double.parseDouble(weatherDate.getWind_speed_kt())));
			output.flush();
			output.printf("%s", fromDoubleToString(Double.parseDouble(weatherDate.getTemp_cMinusDewPoint())));
			output.flush();
			output.printf("%s", fromDoubleToString(Double.parseDouble(weatherDate.getSky_cover())));
			output.flush();
			String response = input.readLine();
			logger.info(response);
			GraphData[] prediction = processPredictorResponse(response);
			
			// Close the connection
			predictorSocket.close();
			
			return new ResponseEntity<>(prediction, HttpStatus.OK);
		} catch (IOException e) {
			logger.debug(e.getMessage());
			return new ResponseEntity<>(null, HttpStatus.SERVICE_UNAVAILABLE);
		}
	}
	
	/**
	 * Converts a double to a 8-char string in order to send it to the python server:
	 * - If the length is more than 8 then it gets the first 8 characters, hence it deletes
	 * the least significative decimal numbers.
	 * - Otherwise it prepends zeroes till the string has 8 characters.
	 * @param d Double to convert
	 * @return 8-char stringified double
	 */
	public String fromDoubleToString(double d){
		String res = Double.toString(d);
		if (res.length() > 8) res = res.substring(0, 8);
		else{
			while(res.length() < 8){
				res = "0" + res;
			}
		}
		logger.info(res);
		return res;
	}
	
	/**
	 * - If the int has more than 8 digits then it is returned (nothing is done)
	 * - Otherwise it is converted to a 8-char string in order to send it to the python server,
	 * prepending zeroes till the string has 8 characters.
	 * @param d Int to convert
	 * @return 8-char stringified int
	 */
	public String fromIntToString(int i){
		String res = Integer.toString(i);
		while(res.length() < 8){
			res = "0" + res;
		}
		logger.info(res);
		return res;
	}
	
	public GraphData[] processPredictorResponse(String response){
		logger.info(response);
		GraphData[] res;
		List<Double> resList = new ArrayList<Double>();
		String[] resSplit = response.split(" ");
		res = new GraphData[resSplit.length];
		for(int i=0; i<resSplit.length; i++){
			resList.add(Double.parseDouble(resSplit[i]));
		}
		Collections.sort(resList);
		for(int i=0; i<resList.size(); i++){
			res[i] = new GraphData(PREDICTION_PERIOD[i], PREDICTION_COLOR[i], resList.get(i));
		}
		return res;
	}
	
	//@Async
	//@Scheduled(fixedRate=FEED_FREQ*1000)
	public void feedPredictor() throws UnknownHostException, IOException{
		Socket predictorSocket = new Socket(PREDICTOR_HOST, PREDICTOR_PORT);
		logger.info("Connected to the predictor");
		PrintWriter output = new PrintWriter(predictorSocket.getOutputStream(), false);
		BufferedReader input = new BufferedReader(new InputStreamReader(predictorSocket.getInputStream()));
		
		// Command
		output.printf("%s", MainController.FEED_COMMAND);
		output.flush();
		// Data


		String response = input.readLine();
		if (response.equals(PREDICTOR_RESPONSE_OK)) logger.info("Predictor successfully fed");
		else logger.info("Error while feeding predictor");
		
		// Close the connection
		predictorSocket.close();
	}
}
