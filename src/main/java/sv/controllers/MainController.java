package sv.controllers;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

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

import sv.dataprocess.DataExtractor;
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
	// Each FEED_FREQ minutes the web server will provide new flights to the predictor
	private static final long FEED_FREQ = 3*60;
	
	private static final String[] PREDICTION_COLOR = { "#a6f655", "#f6f655", "#f6a655", "#ff4d4d"};
	private static final String[] PREDICTION_PERIOD = { "0-5 min", "5-30 min", "30-60 min", ">60 min"};
	
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
	public static String fromDoubleToString(double d){
		String res = Double.toString(d);
		if (res.length() > 8) res = res.substring(0, 8);
		else{
			while(res.length() < 8){
				res = "0" + res;
			}
		}
		return res;
	}
	
	/**
	 * - If the int has more than 8 digits then it is returned (nothing is done)
	 * - Otherwise it is converted to a 8-char string in order to send it to the python server,
	 * prepending zeroes till the string has 8 characters.
	 * @param d Int to convert
	 * @return 8-char stringified int
	 */
	public static String fromIntToString(int i){
		String res = Integer.toString(i);
		while(res.length() < 8){
			res = "0" + res;
		}
		return res;
	}
	
	/**
	 * - If the long has more than 8 digits then it is returned (nothing is done)
	 * - Otherwise it is converted to a 8-char string in order to send it to the python server,
	 * prepending zeroes till the string has 8 characters.
	 * @param d Long to convert
	 * @return 8-char stringified long
	 */
	public static String fromLongToString(long i){
		String res = Long.toString(i);
		while(res.length() < 8){
			res = "0" + res;
		}
		return res;
	}
	
	/**
	 * Processes the predictor response when it is asked for a flight delay prediction
	 * @param response Response of the predictor
	 * @return Formatted data to return to the user
	 */
	public GraphData[] processPredictorResponse(String response){
		logger.info(response);
		GraphData[] res;
		String[] resSplit = response.split(" ");
		res = new GraphData[resSplit.length];
		for(int i=0; i<resSplit.length; i++){
			res[i] = new GraphData(PREDICTION_PERIOD[i], PREDICTION_COLOR[i], (double) Math.round(Double.parseDouble(resSplit[i]) * 100d) / 100d);
		}
		return res;
	}
	
	@Async
	@Scheduled(fixedRate=FEED_FREQ*1000)
	/**
	 * Provides new information about flights to the predictor
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public void feedPredictor() throws UnknownHostException, IOException{
		File f = new File(DataExtractor.FILE_NAME);
		if (f.exists()) f.delete();
		Socket predictorSocket = new Socket(PREDICTOR_HOST, PREDICTOR_PORT);
		logger.info("Connected to the predictor");
		PrintWriter output = new PrintWriter(predictorSocket.getOutputStream(), false);
		//BufferedReader input = new BufferedReader(new InputStreamReader(predictorSocket.getInputStream()));
		
		// Command
		output.printf("%s", MainController.FEED_COMMAND);
		output.flush();
		// Data size
		int fileSize = DataExtractor.extractInfo();
		//int fileSize = countLines(DataExtractor.FILE_NAME);
		output.printf("%s", fromIntToString(fileSize));
		output.flush();
		// Data
		BufferedReader in = new BufferedReader(new FileReader(DataExtractor.FILE_NAME));
		String line;
		while((line = in.readLine()) != null){
			if (!line.isEmpty() && !line.equals("\n")){
				output.printf("%s", line);
				output.flush();
			}
		}
		
		in.close();
		// Close the connection
		predictorSocket.close();
	}
	
	/**
	 * Gets the number of lines in a file
	 * @param filename Name of the file (path)
	 * @return Number of lines
	 * @throws IOException
	 */
	public static int countLines(String filename) throws IOException {
	    InputStream is = new BufferedInputStream(new FileInputStream(filename));
	    try {
	        byte[] c = new byte[1024];
	        int count = 0;
	        int readChars = 0;
	        boolean empty = true;
	        while ((readChars = is.read(c)) != -1) {
	            empty = false;
	            for (int i = 0; i < readChars; ++i) {
	                if (c[i] == '\n') {
	                    ++count;
	                }
	            }
	        }
	        return (count == 0 && !empty) ? 1 : count;
	    } finally {
	        is.close();
	    }
	}
}
