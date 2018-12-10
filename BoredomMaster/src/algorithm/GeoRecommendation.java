package algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import db.DBConnection;
import db.DBConnectionFactory;
import entity.Item;

/**
 * 
 * @author mz
 * Geo-Recommendation based on the categories of items/events closed to the user's current location  
 *
 */

public class GeoRecommendation {
	public List<Item> recommendItems(String userId, double lat, double lon) {
		List<Item> recommendedItems = new ArrayList<>();

		// Step 1: fetch all the items/events liked by this user.
		DBConnection connection = DBConnectionFactory.getConnection();
		Set<String> favoritedItemIds = connection.getFavoriteItemIds(userId);

		// Step 2: fetch all categories according to the categories of liked items/events of this user
		// The result will look like: {sports: 7, music: 5, art: 2}
		Map<String, Integer> allCategories = new HashMap<>();
		for (String favoritedItemId : favoritedItemIds) {
			Set<String> categories = connection.getCategories(favoritedItemId);
			for (String category : categories) {
				allCategories.put(category, allCategories.getOrDefault(category, 0) + 1);
			}
		}
		// Compare and sort the number of occurrence of each category
		// such that we can get the the one that the user may like most
		List<Map.Entry<String, Integer>> categoryList = new ArrayList<>(allCategories.entrySet());
		Collections.sort(categoryList, (Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) -> {
			return Integer.compare(o2.getValue(), o1.getValue());
		});

		// Step 3: determine what categories the events/item belong to
		// {sports: 7, music: 5, art: 2}
		Set<String> visitedItemIds = new HashSet<>();
		for (Map.Entry<String, Integer> category : categoryList) {
			List<Item> items = connection.searchItems(lat, lon, category.getKey());
			for (Item item : items) {
				if (!favoritedItemIds.contains(item.getItemId()) && !visitedItemIds.contains(item.getItemId())) {
					recommendedItems.add(item);
					visitedItemIds.add(item.getItemId());
				}
			}
		}

		connection.close();
		return recommendedItems;
	}
}
