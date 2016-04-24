package sv.dataprocess;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.jaunt.Elements;
import com.jaunt.UserAgent;

public class DataExtractor {

	public static void main(String[] args) {

		PrintWriter pw = null;
		try {
			if (new File("Features.txt").exists()) {
				new File("Features.txt").delete();
			}
			pw = new PrintWriter(new FileWriter(new File("Features.txt")));
		} catch (Exception a) {
		}
		writeData(pw, "KJFK", "JFK", "America/New_York", "-0400");
		writeData(pw, "KLAX", "LAX", "America/Los_Angeles", "-0700");
		writeData(pw, "KMIA", "MIA", "America/Chicago", "-0500");
		writeData(pw, "KDAL", "DAL", "America/Chicago", "-0500");
		writeData(pw, "KAUS", "AUS", "America/Chicago", "-0500");
		writeData(pw, "KORD", "ORD", "America/Chicago", "-0500");
		writeData(pw, "KSEA", "SEA", "America/Los_Angeles", "-0700");
		writeData(pw, "KBOS", "BOS", "America/New_York", "-0400");
		writeData(pw, "KMSY", "MSY", "America/Chicago", "-0500");
		writeData(pw, "KPWM", "PWM", "America/Los_Angeles", "-0700");
		writeData(pw, "KDTW", "DTW", "America/New_York", "-0400");
		writeData(pw, "KLAS", "LAS", "America/Los_Angeles", "-0700");
		writeData(pw, "KPDX", "PDX", "America/Phoenix", "-0700");

		pw.close();
	}

	public static WeatherDate getInfo(URL url) throws Exception {
		URLConnection con = url.openConnection();

		InputStream in = con.getInputStream();

		String temp_c = "";
		String dewpoint_c = "";
		String wind_dir_degrees = "";
		String wind_speed_kt = "";
		// String visibility_statute_mi = "";
		String sky_cover = "";

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(in);
		doc.getDocumentElement().normalize();

		NodeList nList = doc.getElementsByTagName("METAR");
		Node nodeData = nList.item(nList.getLength() - 1);
		NodeList info = nodeData.getChildNodes();

		for (int temp = 0; temp < info.getLength(); temp++) {
			Node nNode = info.item(temp);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				try {
					Element eElement = (Element) nNode;
					if (eElement.getNodeName().contains("sky_condition")) {
						Node node = eElement.getAttributes().getNamedItem("sky_cover");
						sky_cover = node.getNodeValue();
					} else {

						if (eElement.getNodeName().contains("wind_speed_kt"))
							wind_speed_kt = eElement.getFirstChild().getNodeValue();
						if (eElement.getNodeName().contains("wind_dir_degrees"))
							wind_dir_degrees = eElement.getFirstChild().getNodeValue();
						if (eElement.getNodeName().contains("temp_c"))
							temp_c = eElement.getFirstChild().getNodeValue();
						if (eElement.getNodeName().contains("dewpoint_c"))
							dewpoint_c = eElement.getFirstChild().getNodeValue();
					}

				} catch (Exception e) {
					System.err.println(e.getMessage());

				}

			}
		}
		Double temp_roc = Double.valueOf(temp_c) - Double.valueOf(dewpoint_c);
		String cloud = "1";
		if (sky_cover.contains("CLR")) {
			cloud = "0";
		}
		if (sky_cover.contains("SKC")) {
			cloud = "0";
		}
		if (sky_cover.contains("SCT")) {
			cloud = "0.4";
		}
		if (sky_cover.contains("FEW")) {
			cloud = "0.2";
		}
		if (sky_cover.contains("BKN")) {
			cloud = "0.7";
		}
		return new WeatherDate(wind_speed_kt, String.valueOf(temp_roc), cloud);
	}

	public static void writeData(PrintWriter pw, String station, String stationVisit, String zoneid,
			String desfaseHorario) {
		try {
			Instant now = Instant.now();
			ZoneId zoneId = ZoneId.of(zoneid);// Los_Angeles //-7
			ZonedDateTime dateAndTime = ZonedDateTime.ofInstant(now, zoneId);
			int currentHour = dateAndTime.getHour(); // los angeles
			UserAgent userAgent = new UserAgent(); // create new userAgent
													// (headless browser)
			userAgent
					.visit("http://tracker.flightview.com/FVAccess2/tools/fids/fidsDefault.asp?accCustId=FVWebFids&fidsId=20001&fidsInit=departures&fidsApt="
							+ stationVisit + "&fidsFilterAl=&fidsFilterArrap="); // visit
			// google
			// userAgent.doc.apply("butterflies"); //apply form input (starting
			// at first editable field)
			// userAgent.doc.submit("Google Search"); //click submit button
			// labelled "Google Search"

			Elements links = userAgent.doc.findEvery("<td>");// .findEvery("<a>");
																// //find search
																// result links
			boolean siguiente = false;
			ArrayList<ArrayList<String>> horas = new ArrayList<>();
			ArrayList<String> siguienteHora = new ArrayList<>();

			for (com.jaunt.Element link : links) {
				String campo = link.getText();// &nbsp;AM

				if (siguiente) {
					if ((campo.contains(";PM") || campo.contains(";AM"))) {
						// hora real salida
						campo = campo.replace("&nbsp;", "");

						String hora = campo.split(":")[0];
						if (campo.contains("PM") && !campo.contains("12:")) {
							int nueva = Integer.valueOf(hora) + 12;
							hora = String.valueOf(nueva);
							campo = hora + ":" + campo.split(":")[1];
						}

						campo = campo.replace("AM", "");
						campo = campo.replace("PM", "");

						siguienteHora.add(campo);
						Integer horaSalidaReal = Integer.valueOf(hora);
						if (horaSalidaReal <= currentHour) {
							// si la hora que ha salido o va a salir es la hora
							// actual o menor, se peude añadir para
							// aprendizaje
							horas.add(siguienteHora);
						}
					}
					siguiente = false;
					siguienteHora = new ArrayList<>();
				} else {
					siguiente = false;
					if (campo.contains(";PM") || campo.contains(";AM")) {
						// hora scheduled salida

						String hora = campo.split(":")[0];
						campo = campo.replace("&nbsp;", "");

						if (campo.contains("PM") && !campo.contains("12:")) {
							int nueva = Integer.valueOf(hora) + 12;
							hora = String.valueOf(nueva);
							campo = hora + ":" + campo.split(":")[1];

						}
						siguiente = true;
						campo = campo.replace("AM", "");
						campo = campo.replace("PM", "");
						campo = campo.replace("&nbsp;", "");
						siguienteHora.add(campo);
					}
				}
			}

			String year = String.valueOf(dateAndTime.getYear());
			String month = String.valueOf(dateAndTime.getMonthValue());
			if (month.length() == 1)
				month = "0" + month;
			// ayer
			String day = String.valueOf(dateAndTime.getDayOfMonth() - 2);
			if (day.length() == 1)
				day = "0" + day;

			for (int i = 0; i < horas.size(); i++) {
				ArrayList<String> dosHoras = horas.get(i);
				String supuesta = dosHoras.get(0);
				String real = dosHoras.get(1);
				Integer hora1 = Integer.valueOf(supuesta.split(":")[0]);
				Integer hora2 = Integer.valueOf(real.split(":")[0]);
				Integer minutos1 = Integer.valueOf(supuesta.split(":")[1]);
				Integer minutos2 = Integer.valueOf(real.split(":")[1]);
				int retraso = (hora2 - hora1) * 60;
				retraso += (minutos2 - minutos1);

				String hour = String.valueOf(hora1);
				if (hour.length() == 1)
					hour = "0" + hour;
				String hour2 = String.valueOf(hora1 + 1);
				if (hour2.length() == 1)
					hour2 = "0" + hour2;
				String minutes = String.valueOf(minutos1);
				if (minutes.length() == 1)
					minutes = "0" + minutes;
				String seconds = String.valueOf((int) dateAndTime.getSecond());
				if (seconds.length() == 1)
					seconds = "0" + seconds;
				String timeNow = year + "-" + month + "-" + day + "T" + hour + ":" + minutes + ":" + seconds;
				String timeNow2 = year + "-" + month + "-" + day + "T" + hour2 + ":" + minutes + ":" + seconds;

				URL url = new URL(
						"https://www.aviationweather.gov/adds/dataserver_current/httpparam?dataSource=metars&requestType=retrieve&format=xml&startTime="
								+ timeNow + desfaseHorario + "&endTime=" + timeNow2 + desfaseHorario + "&stationString="
								+ station);
				WeatherDate data = getInfo(url);
				if (retraso < 0) {
					// sale antes, puede pasar, pero se pone a cero, pues no es
					// un retraso
					// y no tiene que ver la causa de salir antes
					retraso = 0;
				}

				try {
					pw.println(retraso + " " + data.wind_speed_kt + " " + data.temp_cMinusDewPoint + " "
							+ data.sky_cover + " ");
				} catch (Exception e) {
					System.err.println(e.getMessage());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e.getMessage());

		}
	}
}
