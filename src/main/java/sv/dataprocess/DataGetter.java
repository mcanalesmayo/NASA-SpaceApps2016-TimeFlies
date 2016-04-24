package sv.dataprocess;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.dvdme.ForecastIOLib.ForecastIO;

public class DataGetter {

	private static Logger logger = LoggerFactory.getLogger(DataGetter.class);
	
	public static void main(String[] args) {
		try {
			getTimeWheater("40", "0", 0, 12, 0);
		} catch (Exception e) {
			logger.info(e.getMessage());
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
