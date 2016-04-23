package sv.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import sv.configuration.MainConfiguration;

@Controller
public class MainController {
	
	private static final Logger logger = LoggerFactory.getLogger(MainController.class);
	
	private static final String PREDICT_COMMAND = "PR";
	private static final String FEED_COMMAND = "FE";
	
	/**
	 * Predicts the delay of a flight
	 * @param city City where the person is taking the flight
	 * @param date Date of the flight
	 */
	@RequestMapping(value = "/predict", method = RequestMethod.GET)
	public ResponseEntity<?> predict(@RequestParam(value = "city", required = true) String city,
			@RequestParam(value = "date", required = true) @DateTimeFormat(pattern="yyyy-MM-dd") Date date){
		try {
			Socket predictorSocket = new Socket(MainConfiguration.PREDICTOR_HOST, MainConfiguration.PREDICTOR_PORT);
			logger.info("Connected to the predictor");
			PrintWriter output = new PrintWriter(predictorSocket.getOutputStream(), false);
			BufferedReader input = new BufferedReader(new InputStreamReader(predictorSocket.getInputStream()));
			
			int windSpeed = 2097151;
			double dewPoint = 192.464643646256;
			double visibility = dewPoint;
			
			// Command
			output.printf("%s", MainController.PREDICT_COMMAND);
			output.flush();
			// Data
			output.printf("%s", fromIntToString(windSpeed));
			output.flush();
			output.printf("%s", fromDoubleToString(dewPoint));
			output.flush();
			output.printf("%s", fromDoubleToString(visibility));
			output.flush();
			String response = input.readLine();
			logger.info(response);
			String[] prediction = processPredictorResponse(response);
			
			// Close the connection
			predictorSocket.close();
			
			// Return the prediction
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
	
	public String[] processPredictorResponse(String response){
		return response.split(" ");
	}
}
