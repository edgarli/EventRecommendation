package external;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import entity.Item;
import entity.Item.ItemBuilder;


public class TicketMasterAPI {
	private static final String URL = "https://app.ticketmaster.com/discovery/v2/events.json";
	private static final String DEFAULT_KEYWORD = ""; // no restriction
	private static final String API_KEY = "YQPItiSamMjrt6yBb6lXl6t8z40PpCzB";
	
	private String getAddress(JSONObject event) throws JSONException {
		
		if(!event.isNull("_embedded")) {
			JSONObject embedded = event.getJSONObject("_embedded");
			
			if(!embedded.isNull("venues")) {
				JSONArray venues = embedded.getJSONArray("venues");
				
				for(int i = 0; i < venues.length(); ++i){
					JSONObject venue = venues.getJSONObject(i);
					
					StringBuilder sb = new StringBuilder();
					
					if(!venue.isNull("address")) {
						JSONObject address = venue.getJSONObject("address");
						
						if(!address.isNull("line1")) {
							sb.append(address.getString("line1"));
							
						}
						
						if(!address.isNull("line2")) {
							sb.append(" ");
							sb.append(address.getString("line2"));
						}
						
						if(!address.isNull("line3")) {
							sb.append(" ");
							sb.append(address.getString("line3"));
						}
					}
					
					if(!venue.isNull("city")) {
						sb.append(" ");
						JSONObject city = venue.getJSONObject("city");
						
						if(!city.isNull("name")) {
							sb.append(city.getString("name"));
						}
					}
					if(!sb.toString().equals("")) {
						return sb.toString();
					}
				}
			}
		}
		
		return "";
		
	}
	
	private String getImageUrl(JSONObject event) throws JSONException{
		if(!event.isNull("images")) {
			JSONArray images = event.getJSONArray("images");
			
			for(int i = 0; i < images.length(); ++i) {
				JSONObject image = images.getJSONObject(i);
				
				if(!image.isNull("url")) {
					return image.getString("url");
				}
			}
			
		}
		return "";
	}
	
	private Set<String> getCategories(JSONObject event) throws JSONException {
		Set<String> categories = new HashSet<>();
		
		if(!event.isNull("classifications")) {
			JSONArray classifications = event.getJSONArray("classifications");
			
			for(int i = 0; i < classifications.length(); i++) {
				JSONObject classification = classifications.getJSONObject((i));
				
				if(!classification.isNull("segment")) {
					JSONObject segment = classification.getJSONObject("segment");
					
					if(!segment.isNull("name")) {
						categories.add(segment.getString("name"));
					}
				}
			}
		}
		
		return categories;
	}
	
	private List<Item> getItemList(JSONArray events) throws JSONException {
		List<Item> itemList = new ArrayList<>();
		
		for(int i = 0; i < events.length(); i++) {
			JSONObject event = events.getJSONObject(i);
			
			ItemBuilder builder = new ItemBuilder();
			
			if(!event.isNull("name")) {
				builder.setName(event.getString("name"));
			}
			
			if(!event.isNull("id")) {
				builder.setItemId(event.getString("id"));
			}
			
			if(!event.isNull("url")) {
				builder.setUrl(event.getString("url"));
			}
			
			if(!event.isNull("distance")) {
				builder.setDistance(event.getDouble("distance"));
			}
			
			builder.setCategories(getCategories(event));
			builder.setAddress(getAddress(event));
			builder.setImageUrl(getImageUrl(event));
			
			itemList.add(builder.build());
		}
		
		return itemList;
	}
	
	
	public List<Item> search(double lat, double lon, String keyword) {
		//set default keyword
		if (keyword == null) {
			 keyword = DEFAULT_KEYWORD;
		 }
		 
		//encode keyword if this can not be encoded by ASC
		 try {
			 keyword = java.net.URLEncoder.encode(keyword, "UTF-8");
		 } catch(Exception e) {
			 e.printStackTrace();
		 }
		 
		 //String geoHash = GeoHash.encodeGeohash(lat, lon, 8);
		 
		 String query = String.format("apikey=%s&geoPoint=%s,%s&keyword=%s&radius=%s",API_KEY,lat,lon,keyword,50);
		 
		 try {
			 HttpURLConnection connection = (HttpURLConnection) new URL(URL + "?" + query).openConnection();
			 int responseCode = connection.getResponseCode();
			 
			 System.out.println("\nSending request to URL: "+ URL + "?" + query);
			 System.out.println("Rsponse Code: "+responseCode);
			 
			 if (responseCode != 200) {
				 //
			 }
			 
			 BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			 String inputLine;
			 StringBuilder response = new StringBuilder();
			 while((inputLine = in.readLine()) != null) {
				 response.append(inputLine);
			 }
			 in.close();
			 
			 JSONObject obj = new JSONObject(response.toString());
			 if (obj.isNull("_embedded")) {
				 return new ArrayList<>();
			 }
			 
			 JSONObject embedded = obj.getJSONObject("_embedded");
			 JSONArray events = embedded.getJSONArray("events");
			 
			 return getItemList(events);
			 
		 }catch(Exception e) {
			 e.printStackTrace();
		 }
		 
		 return new ArrayList<>();

		 
	}
	
	private void queryAPI(double lat, double lon) {
		List<Item> events = search(lat, lon, null);

		try {
		    for (int i = 0; i < events.size(); ++i) {
		       Item event = events.get(i);
		       System.out.println(event.toJSONObject());
		    }
		} catch (Exception e) {
	                  e.printStackTrace();
		}
	}
	
	public static void main(String[] ags) {
		TicketMasterAPI tmApi = new TicketMasterAPI();
		//New York
		tmApi.queryAPI(29.682684, -95.295410);
	}


}