package com.example.routes.beans;

import org.json.JSONException;
import org.json.JSONObject;



public class Places{

	public String description;
	public String reference;
	public String LatLng = null;
	
	public Places(String description, String reference) {
		super();
		this.description = description;
		this.reference = reference;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		JSONObject js = new JSONObject();
		try {
			js.put("description", description);
			js.put("reference", reference);
			js.put("LatLng", LatLng);
			return js.toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return super.toString();
		}
		
		
	}
	
	
}
