package algarithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import db.DBConnection;
import db.DBConnectionFactory;
import entity.Item;

public class GeoRecommendation {
	public List<Item> recommendItems(String userId, double lat, double lon){
		List<Item> recommendedItems = new ArrayList<>();
		DBConnection conn = DBConnectionFactory.getConnectin();
		
		Set<String> favoriateItemIds = conn.getFavoriteItemIds(userId);
		System.out.printf("f count: %d\n, ", favoriateItemIds.size());
		
		Map<String, Integer> allCategories = new HashMap<>();
		
		for(String itemId: favoriateItemIds) {
			Set<String> categories = conn.getCategories(itemId);
			for(String category : categories) {
				if(allCategories.containsKey(category)) {
					allCategories.put(category, allCategories.get(category)+1); 
				}else {
					allCategories.put(category, 1);
				}
			}
		}
		System.out.printf("c count %d\n", allCategories.size());
		
		List<Entry<String, Integer>> categoryList = new ArrayList<Entry<String, Integer>>(allCategories.entrySet());
		Collections.sort(categoryList, new Comparator<Entry<String, Integer>>(){
			@Override
			public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
				return Integer.compare(o2.getValue(), o1.getValue());
			}
		});
		
		//search based on category, filtered out favorited items, sort by distance
		Set<Item> visitedItems = new HashSet<>();
		
		for(Entry<String, Integer> category : categoryList) {
			List<Item> items = conn.searchItems(lat, lon, category.getKey());
			List<Item> filteredItems = new ArrayList<>();
			for(Item item : items) {
				if(favoriateItemIds.contains(item.getItemId()) && !visitedItems.contains(item)) {
					filteredItems.add(item);
				}
			}
			
			Collections.sort(filteredItems, new Comparator<Item>(){
				@Override
				public int compare(Item item1, Item item2) {
					return Double.compare(item1.getDistance(), item2.getDistance());
				}
			});
			
			visitedItems.addAll(items);
			recommendedItems.addAll(filteredItems);
		}
		
		return recommendedItems;
	}
}
