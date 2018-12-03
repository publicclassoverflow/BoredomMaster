package external;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

public class TicketMasterAPI {
	private static final String URL = "https://app.ticketmaster.com/discovery/v2/events.json";
	private static final String DEFAULT_KEYWORD = ""; // no search restrictions
	private static final String API_KEY = "mmOzmN0ZP3k7m8S4U3V5JB0lurLyb5UO"; // API key generated by TicketMaster
	
	// Search based on location
	public JSONArray search(double lat, double lon, String keyword) {
		if (keyword == null) {
			keyword = DEFAULT_KEYWORD;
		}
		
		try {
			// Specifically use UTF-8
			// e.g. spaces in the url will be converted to "20%"
			keyword = URLEncoder.encode(keyword, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		String geoHash = GeoHash.encodeGeohash(lat, lon, 9);
		
		String query = String.format("apikey=%s&geoPoint=%s&keyword=%s&radius=50", API_KEY, geoHash, keyword);

	    
		try {
			// Create a connection to the remote object referred to by the url in order to perform basic HTTP requests 
			HttpURLConnection connection = (HttpURLConnection) new URL(URL + "?" + query).openConnection();
			connection.setRequestMethod("GET");
			
			// Log the response code for debugging purpose
			int responseCode = connection.getResponseCode();
			System.out.println("Sending 'GET' request to URL: " + URL);
			System.out.println("Response Code: " + responseCode);
		
			// Get the full response body
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuilder response = new StringBuilder();
			
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			
			JSONObject obj = new JSONObject(response.toString());
			if (!obj.isNull("_embedded")) {
				JSONObject embbeded = obj.getJSONObject("_embedded");
				return embbeded.getJSONArray("events");
			}	
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new JSONArray();	
	}

	// Test the response from the API
	private void queryAPI(double lat, double lon) {
		JSONArray events = search(lat, lon, null);

		try {
			for (int i = 0; i < events.length(); ++i) {
				JSONObject event = events.getJSONObject(i);
				System.out.println(event.toString(2));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Test sample TicketMaster API requests
	 */
	public static void main(String[] args) {
		TicketMasterAPI api = new TicketMasterAPI();
		// San Diego Zoo
		api.queryAPI(32.7353, 117.1490);
		// SDCCU Stadium
		api.queryAPI(32.7831, 117.1196);
		// Old Trafford Stadium
		api.queryAPI(53.4631, 2.2913);
	}

}
