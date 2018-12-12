package rpc;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;
import entity.Item;

/**
 * Servlet implementation class SearchItem
 */
@WebServlet("/search") // "/SearchItem" --> "/search" based on naming convention
public class SearchItem extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Test with http request like:
		// http://localhost:8080/BoredomMaster/search?lat=32.7353&lon=-117.1490
		double lat = Double.parseDouble(request.getParameter("lat"));
		double lon = Double.parseDouble(request.getParameter("lon"));
		// The term may be empty
		String term = request.getParameter("term");
		// Get the user's info (user_id) such that her/his favorite history can be fetched
		String userId = request.getParameter("user_id");
		// Search and add the items/events into database
		DBConnection connection = DBConnectionFactory.getConnection();
		try {
			// Search for nearby items
			List<Item> items = connection.searchItems(lat, lon, term);
			// Get the user's favorite history
			Set<String> favoriteItems = connection.getFavoriteItemIds(userId);
			// Display the response
			JSONArray array = new JSONArray();
			for (Item item : items) {
				JSONObject obj = item.toJSONObject();
				obj.put("favorite", favoriteItems.contains(item.getItemId()));
				array.put(obj);
			}
			RpcHelper.writeJsonArray(response, array);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			connection.close();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
}