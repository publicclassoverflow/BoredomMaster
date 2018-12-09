package rpc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;

/**
 * Servlet implementation class ItemHistory
 */
@WebServlet("/history")
public class ItemHistory extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ItemHistory() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// The POST method is used to set the favorite items/events list of a user
		DBConnection connection = DBConnectionFactory.getConnection();
		try {
			JSONObject requestBody = RpcHelper.readJSONObject(request);
			String userId = requestBody.getString("user_id");
			JSONArray array = requestBody.getJSONArray("favorite");
			// Get all the items/events liked by the user
			List<String> itemIds = new ArrayList<>();
			for (int i = 0; i < array.length(); i++) {
				itemIds.add(array.getString(i));
			}
			connection.setFavoriteItems(userId, itemIds);
			RpcHelper.writeJsonObject(response, new JSONObject().put("result", "SUCCESS"));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			connection.close();
		}
	}

	/**
	 * @see HttpServlet#doDelete(HttpServletRequest, HttpServletResponse)
	 */
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// The DELETE method is used to unset/remove liked items/events from a user's favorite history
		DBConnection connection = DBConnectionFactory.getConnection();
		try {
			JSONObject requestBody = RpcHelper.readJSONObject(request);
			String userId = requestBody.getString("user_id");
			JSONArray array = requestBody.getJSONArray("favorite");
			// Get all the items/events liked by the user
			// Let the user delete all of her/his history for now
			List<String> itemIds = new ArrayList<>();
			for (int i = 0; i < array.length(); i++) {
				itemIds.add(array.getString(i));
			}
			connection.unsetFavoriteItems(userId, itemIds);
			RpcHelper.writeJsonObject(response, new JSONObject().put("result", "SUCCESS"));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			connection.close();
		}
	}
}
