package com.example.routes.beans;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

public class Legs {

	public TimesObject arrival_time = null;
	public TimesObject departure_time = null;
	public String distance;
	public String duration;
	public String end_address;
	public String start_address;
	public String end_location;
	public String start_location;
	public ArrayList<Steps> steps;
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		JSONObject js = new JSONObject();
		try {
			js.put("arrival_time", arrival_time);
			js.put("departure_time", departure_time);
			js.put("distance", distance);
			js.put("duration", duration);
			js.put("end_location", end_location);
			js.put("start_location", start_location);
			js.put("end_address", end_address);
			js.put("start_address", start_address);
			js.put("steps", steps);
			return js.toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return super.toString();
		}
	}
}
