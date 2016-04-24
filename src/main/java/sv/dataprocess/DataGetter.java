package sv.dataprocess;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.github.dvdme.ForecastIOLib.ForecastIO;
import com.jaunt.Elements;
import com.jaunt.UserAgent;

public class DataGetter {

	public static void main(String[] args) {

		try {
			getTimeWheater("40", "0", 0, 12, 0);


		} catch (Exception a) {
		}
	}

	public static void getTimeWheater(String lon, String lat, int offSetDays, int hour, int minutes) throws Exception {
		// Gets the hour offset
		Double desfase = 0.0;

		/*
		 * URL url = new
		 * URL("https://maps.googleapis.com/maps/api/timezone/xml?location=" +
		 * lon + "," + lat +
		 * "&timestamp=1331161200&key=AIzaSyCYBoVo8kKV-4dwSNtqY78cikkvUSxaXtQ");
		 * URLConnection con = url.openConnection();
		 *
		 * InputStream in = con.getInputStream(); DocumentBuilderFactory
		 * dbFactory = DocumentBuilderFactory.newInstance(); DocumentBuilder
		 * dBuilder = dbFactory.newDocumentBuilder(); Document doc =
		 * dBuilder.parse(in); doc.getDocumentElement().normalize();
		 *
		 * NodeList nList = doc.getChildNodes(); Node nodeData = nList.item(0);
		 * NodeList info = nodeData.getChildNodes(); for (int temp = 0; temp <
		 * info.getLength(); temp++) {
		 *
		 * Node nNode = info.item(temp); if (nNode.getNodeType() ==
		 * Node.ELEMENT_NODE) { try { Element eElement = (Element) nNode;
		 *
		 * if (eElement.getNodeName().contains("raw_offset")) desfase =
		 * Double.valueOf(eElement.getFirstChild().getNodeValue()); desfase =
		 * desfase / 3600;// given in seconds desfase++;// summer time
		 *
		 * } catch (Exception e) { e.printStackTrace(); }
		 *
		 * } }
		 */
		hour -= desfase;
		if (hour >= 24) {
			offSetDays++;
			hour -= 24;
		}
		if (hour < 0) {
			offSetDays--;
			hour += 24;
		}
		// offSetDays,int hour,int minutes
	    ForecastIO fio = new ForecastIO("3e9f39acff087ff700454b625f1f59d3");
	    fio.setUnits(ForecastIO.UNITS_SI);
	    fio.setLang(ForecastIO.LANG_ENGLISH);
	    fio.getForecast(lat, lon);
	    System.out.println("Latitude: "+fio.getLatitude());
	    System.out.println("Longitude: "+fio.getLongitude());
	    System.out.println("Timezone: "+fio.getTimezone());
	    System.out.println("Offset: "+fio.offset());
	}
}
