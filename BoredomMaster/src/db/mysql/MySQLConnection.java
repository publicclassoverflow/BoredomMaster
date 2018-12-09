package db.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import db.DBConnection;
import entity.Item;
import entity.Item.ItemBuilder;
import external.TicketMasterAPI;

public class MySQLConnection implements DBConnection {
	// Get the database connection
	private Connection connection;
	
	public MySQLConnection() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").getConstructor().newInstance();
			connection = DriverManager.getConnection(MySQLDBUtil.URL);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void close() {
		if (connection != null) {
			try {
				connection.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void setFavoriteItems(String userId, List<String> itemIds) {
		if (connection == null) {
			System.err.println("Database connection failed");
			return;
		}
		// Add the user's item/event into the history table
		try {
			String sql = "INSERT IGNORE INTO history(user_id, item_id) VALUES (?, ?)";
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			// <userId, itemId>
			preparedStatement.setString(1, userId);
			for (String itemId : itemIds) {
				preparedStatement.setString(2, itemId);
				preparedStatement.execute();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void unsetFavoriteItems(String userId, List<String> itemIds) {
		if (connection == null) {
			System.err.println("Database connection failed");
			return;
		}
		// Remove the item/event from the user's history table
		try {
			String sql = "DELETE FROM history WHERE user_id = ? AND item_id = ?";
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, userId);
			for (String itemId : itemIds) {
				preparedStatement.setString(2, itemId);
				preparedStatement.execute();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Set<String> getFavoriteItemIds(String userId) {
		if (connection == null) {
			return new HashSet<>();
		}
		
		Set<String> favoriteItems = new HashSet<>();

		try {
			String sql = "SELECT item_id FROM history WHERE user_id = ?";
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, userId);

			ResultSet resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				String itemId = resultSet.getString("item_id");
				favoriteItems.add(itemId);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return favoriteItems;
	}

	@Override
	public Set<Item> getFavoriteItems(String userId) {
		if (connection == null) {
			return new HashSet<>();
		}
		Set<Item> favoriteItems = new HashSet<>();
		Set<String> itemIds = getFavoriteItemIds(userId);
		// Gather the items/events info and build new items added to the favorite set
		try {
			String sql = "SELECT * FROM items WHERE item_id = ?";
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			for (String itemId : itemIds) {
				preparedStatement.setString(1, itemId);
				// Use ResultSet to parse the SQL result
				ResultSet resultSet = preparedStatement.executeQuery();

				ItemBuilder builder = new ItemBuilder();

				while (resultSet.next()) {
					builder.setItemId(resultSet.getString("item_id"));
					builder.setName(resultSet.getString("name"));
					builder.setAddress(resultSet.getString("address"));
					builder.setImageUrl(resultSet.getString("image_url"));
					builder.setUrl(resultSet.getString("url"));
					builder.setCategories(getCategories(itemId));
					builder.setDistance(resultSet.getDouble("distance"));
					builder.setRating(resultSet.getDouble("rating"));
					// Build and add the item to the favorite set
					favoriteItems.add(builder.build());
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return favoriteItems;
	}

	@Override
	public Set<String> getCategories(String itemId) {
		if (connection == null) {
			return null;
		}
		Set<String> categories = new HashSet<>();
		try {
			String sql = "SELECT category from categories WHERE item_id = ? ";
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, itemId);
			ResultSet resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				String category = resultSet.getString("category");
				categories.add(category);
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		}
		return categories;
	}

	@Override
	public List<Item> searchItems(double lat, double lon, String term) {
		TicketMasterAPI ticketMasterAPI = new TicketMasterAPI();
	    List<Item> items = ticketMasterAPI.search(lat, lon, term);
	    for(Item item : items) {
	    	// Save each item to the database
	    	saveItem(item);
	    }
	    return items;
	}

	@Override
	public void saveItem(Item item) {
		if (connection == null) {
			System.err.println("Database connection failed");
			return;
		}
		// Use this specific trick to avoid SQL injection
		try {
			String sql = "INSERT IGNORE INTO items VALUES (?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, item.getItemId());
			preparedStatement.setString(2, item.getName());
			preparedStatement.setDouble(3, item.getRating());
			preparedStatement.setString(4, item.getAddress());
			preparedStatement.setString(5, item.getImageUrl());
			preparedStatement.setString(6, item.getUrl());
			preparedStatement.setDouble(7, item.getDistance());
			preparedStatement.execute();

			sql = "INSERT IGNORE INTO categories VALUES(?, ?)";
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, item.getItemId());
			// An event may be classified into multiple categories
			for (String category : item.getCategories()) {
				preparedStatement.setString(2, category);
				preparedStatement.execute();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getFullname(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean verifyLogin(String userId, String password) {
		// TODO Auto-generated method stub
		return false;
	}

}
