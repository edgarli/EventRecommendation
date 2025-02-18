package rpc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;
import entity.Item;

/**
 * Servlet implementation class ItemHistroy
 */
@WebServlet("/Histroy")
public class ItemHistroy extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ItemHistroy() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String userId = request.getParameter("user_id");
		JSONArray array = new JSONArray();
		
		DBConnection conn = DBConnectionFactory.getConnectin();
		Set<Item> items = conn.getFavoriteItems(userId);
		
		for (Item item: items) {
			JSONObject obj = item.toJSONObject();
			try {
				obj.append("favoriate", true);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			array.put(obj);

		}
		
		RpcHelper.writeJsonArray(response, array);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		try {
			JSONObject input = RpcHelper.readJsonObject(request);
			String userId = input.getString("user_id");
			
			JSONArray array = input.getJSONArray("favoriate");
			List<String> ItemIds = new ArrayList<>();
			
			for(int i = 0; i < array.length(); i++) {
				ItemIds.add(array.get(i).toString());
			}
			
			DBConnection conn = DBConnectionFactory.getConnectin();
			conn.setFavoriteItems(userId, ItemIds);
			conn.close();
			
			RpcHelper.writeJsonObject(response, new JSONObject().put("result", "SUCCESS"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doDelete(HttpServletRequest, HttpServletResponse)
	 */
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			JSONObject input = RpcHelper.readJsonObject(request);
			String userId = input.getString("user_id");
			
			JSONArray array = input.getJSONArray("favoriate");
			List<String> ItemIds = new ArrayList<>();
			
			for(int i = 0; i < array.length(); i++) {
				ItemIds.add(array.get(i).toString());
			}
			
			DBConnection conn = DBConnectionFactory.getConnectin();
			conn.unsetFavoriteItems(userId, ItemIds);
			conn.close();
			
			RpcHelper.writeJsonObject(response, new JSONObject().put("result", "SUCCESS"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
