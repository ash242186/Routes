package com.example.routes.beans;

import org.json.JSONException;
import org.json.JSONObject;

public class LocationsObject {

	public String location;
	public String name;
	
	
	public LocationsObject(String location, String name) {
		super();
		this.location = location;
		this.name = name;
	}


	@Override
	public String toString() {
		// TODO Auto-generated method stub
		JSONObject js = new JSONObject();
		try {
			js.put("location", location);
			js.put("name", name);
			return js.toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return super.toString();
		}
	}
}
