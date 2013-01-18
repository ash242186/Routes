package com.example.routes.beans;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.maps.GeoPoint;

public class Steps {

	public String distance;
	public String duration;
	public String end_location;
	public String html_instructions = null;
	public List<GeoPoint> polyline;
	public String start_location;
	public String travel_mode;
	public List<Steps> substeps;
	public Transit_Detail transit_details;
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		JSONObject js = new JSONObject();
		try {
			js.put("distance", distance);
			js.put("duration", duration);
			js.put("end_location", end_location);
			js.put("html_instructions", html_instructions);
			js.put("polyline", polyline);
			js.put("start_location", start_location);
			js.put("substeps", substeps);
			js.put("transit_details", transit_details);
			js.put("travel_mode", travel_mode);
			return js.toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return super.toString();
		}
	}
}
