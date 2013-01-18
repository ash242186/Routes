package com.example.routes.beans;

import org.json.JSONException;
import org.json.JSONObject;

public class LinesObject {

	public String agencies;
	public String name;
	public String short_name = null;
	public String vehicle;
	
	
	
	
	public LinesObject(String agencies, String vehicle) {
		super();
		this.agencies = agencies;
		this.vehicle = vehicle;
	}




	@Override
	public String toString() {
		// TODO Auto-generated method stub
		JSONObject js = new JSONObject();
		try {
			js.put("agencies", agencies);
			js.put("name", name);
			js.put("short_name", short_name);
			js.put("vehicle", vehicle);
			return js.toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return super.toString();
		}
	}
}
